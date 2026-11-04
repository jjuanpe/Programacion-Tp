# Documento de Diseño — Copa Internacional de Clubes
## Programación B — UFASTA — Entrega final

---

## 1. Alcance

La aplicación cubre el campeonato completo:

- Carga de equipos, jugadores, directores técnicos y árbitros desde un archivo JSON (`docs/torneo.json`).
- Ciudades y estadios guardados en PostgreSQL, con alta, baja y modificación desde la interfaz
  (páginas *Ciudades* y *Stadiums*).
- Sorteo equilibrado de 16 equipos en 4 zonas, fixture todos contra todos y simulación de la fase de grupos.
- Eliminación directa: cuartos y semifinales a ida y vuelta, y final a partido único.
- Suspensiones que se arrastran entre fechas y entre fases.
- Interfaz de escritorio en JavaFX y ocho reportes, incluidos los carnets de los jugadores en PDF.
- Guardado y retome del torneo (`torneo.save`).

---

## 2. Arquitectura

El código está organizado en capas. Cada capa depende solamente de las que están debajo.

```
 ui.fx   (vistas JavaFX + view models)
   │
 controller   (TournamentSession + un controlador por página)
   │
 model ── dataload (JSON → modelo)      dao (PostgreSQL → modelo)      report (PDF)
```

| Paquete | Responsabilidad |
|---|---|
| `model.people` | `Person` (abstracta), `Player`, `Goalkeeper`, `FieldPlayer`, `HeadCoach`, `Referee` |
| `model.team` | `Team`, `Country`, validación del plantel |
| `model.venue` | `City`, `Stadium` |
| `model.match` | `Match` (abstracta) y `GroupMatch`, `FirstLegMatch`, `SecondLegMatch`, `FinalMatch`; enum `TieCriterion` |
| `model.event` | `Incidence` (abstracta) y `Goal`, `YellowCard`, `Expulsion`, `Change`, `PenaltyExecuted`, `PlayEvent` |
| `model.competition` | `Championship`, `Zone`, sorteo, fixture, tabla de posiciones, fair play y servicios de reportes |
| `model.simulation` | Estrategia de simulación de partidos, simuladores de grupos y eliminatorias, penales |
| `dataload` | `JsonTournamentLoader` (Gson) |
| `dao` | `DBManager`, `CityDao`, `StadiumDAO` (JDBC) |
| `report` | `IdCard`, `IdCardPdfExporter` (PDFBox + código de barras con ZXing) |
| `controller` | `TournamentSession` (punto de entrada único al dominio) y controladores de cada página |
| `ui.fx` | Vistas, view models y filas de las tablas |

### 2.1 Bajo acoplamiento entre interfaz y dominio

- Las vistas no importan clases de `model`. Cada página tiene un **view model** con
  propiedades de JavaFX y objetos fila simples (`StandingRow`, `FixtureRow`, `ScorerRow`, …).
- Los **controladores** leen el dominio a través de `TournamentSession` y completan el
  view model. La vista sólo sabe dibujarse y avisar las acciones del usuario.
- Toda la apariencia vive en `styles.css`; el código Java sólo asigna clases de estilo.

---

## 3. Decisiones de diseño

### 3.1 Jerarquía de jugadores

El archivo JSON trae atributos totalmente distintos para arqueros y jugadores de campo,
por eso `Player` (que guarda la `Position`) tiene dos subclases, `Goalkeeper` y
`FieldPlayer`, cada una con sus propias características y estadísticas.
La valoración del jugador que usa la simulación se calcula de forma polimórfica,
sin cadenas de `if` sobre la posición.

### 3.2 Nombre de persona en un único campo

El archivo de datos trae apellido y nombre juntos en un solo campo (por ejemplo
`"Lobos Vallejos Valentín"`). Se guarda como un único atributo `name` en `Person`,
porque separar por palabras fallaría con los apellidos compuestos.

### 3.3 Jerarquía de partidos

`Match` es abstracta. Cada fase es una subclase que responde sus propias preguntas:
etiqueta de la fase, categoría y si admite definición por penales
(`allowsPenaltyShootout()` es falso en partidos de grupo y de ida).

### 3.4 Incidencias — polimorfismo

Cada evento de un partido es una subclase de `Incidence` (`Goal`, `YellowCard`,
`Expulsion`, `Change`, `PenaltyExecuted`, `PlayEvent`). La clase abstracta define:

| Método | Tipo | Para qué |
|---|---|---|
| `getTypeLabel()` | abstracto | Etiqueta del tipo ("Goal", "Own goal", "Goal (penalty)", …) |
| `getDetail()` | abstracto | Detalle del evento (jugador, equipo, arquero, etc.) |
| `getMinuteLabel()` | concreto | Minuto con formato (`45'`) |
| `isShootoutKick()` | concreto, por defecto `false` | Si el evento es un penal de la tanda |
| `isScoredShootoutKickBy(team)` | concreto, por defecto `false` | Si es un penal de la tanda convertido por ese equipo |

`PenaltyExecuted` sobrescribe los dos últimos. De esta forma `Match` cuenta los penales
de la tanda (`hasPenaltyShootout()`, `getShootoutGoals(team)`) y la interfaz lista las
incidencias **sin usar `instanceof`** ni conocer el tipo concreto de cada evento.
Gracias a esto, `MatchesController` dejó de tener una cadena de condicionales por tipo.

### 3.5 Simulación de partidos — patrón Strategy

`MatchSimulationStrategy<T extends Match>` define cómo se simula un partido.
La implementación usada es `GroupMatchSimulationService`:

1. Se elige una formación para cada equipo y se dejan afuera los suspendidos.
2. `TeamStrengthCalculator` combina el promedio de los titulares, el ranking del equipo
   y los títulos del director técnico.
3. Los goles se reparten según la fuerza de cada equipo, una pequeña ventaja de local
   y un factor de "sorpresa" aleatorio.
4. Amarillas, expulsiones, cambios, penales y goles en contra se generan con
   probabilidades y límites fijos (máximo 3 cambios por equipo).
5. Los minutos jugados de cada jugador se calculan a partir de los cambios y expulsiones.

### 3.6 Concurrencia

- **Fase de grupos:** los partidos de cada fecha se simulan en paralelo con un
  `ExecutorService`. El simulador espera a que termine la fecha completa antes de
  calcular las suspensiones para la siguiente.
- **Eliminación directa:** cada llave (ida y vuelta) es un `KnockoutTieTask` que corre en
  su propio hilo; la fase espera a todas las llaves antes de armar la ronda siguiente.
- Cada tarea recibe su propia semilla de `Random`, así los hilos no comparten estado mutable.

### 3.7 Tabla de posiciones y clasificación

`StandingsService` ordena cada zona por puntos, diferencia de gol y goles a favor.
Si sigue el empate, deciden el puntaje de fair play (amarilla = 1, doble amarilla = +1,
roja directa = 4) y, por último, el ranking del equipo.
Clasifican los dos primeros de cada zona. Los cuartos cruzan zonas:
A1–D2, B1–C2, C1–A2, D1–B2.

**Tabla antes y después de cada partido:** `MatchSimulationReport` guarda dos listas de
`StandingEntry` (`standingsBefore` y `standingsAfter`) por cada partido de zona.
`TournamentSession` conserva esos reportes (`getGroupStageReports()`) y la página
*Matches* los muestra lado a lado en el detalle del partido. Como `MatchSimulationReport`
y `StandingEntry` implementan `Serializable`, las tablas se guardan en `torneo.save` y
siguen disponibles al retomar el torneo.

### 3.8 Desempate en eliminación directa

Las llaves de ida y vuelta se resuelven en `SecondLegMatch`, que conoce su partido de
ida. Se definen, en este orden, por:

1. **Puntos** obtenidos en los dos partidos (3 por victoria, 1 por empate).
2. **Diferencia de gol**, con los goles de visitante valiendo doble.
3. **Definición por penales.**

`getDecidingCriterion()` devuelve cuál de los tres decidió la llave, como un valor del
enum `TieCriterion` (`POINTS`, `GOAL_DIFFERENCE`, `PENALTY_SHOOTOUT`), cada uno con su
etiqueta para la interfaz. `KnockoutTieTask` guarda esa etiqueta en el
`KnockoutTieReport` de cada llave, y la página *Knockout Stage* la muestra junto al
ganador. El detalle del partido de vuelta en *Matches* muestra además el global
(`describeAggregate()`, por ejemplo "Aggregate: 3 - 2") y, si hubo, el resultado de la
tanda de penales. `Match.describeAggregate()` devuelve vacío por defecto y sólo
`SecondLegMatch` lo sobrescribe.

La final es a partido único; si termina empatada se define por penales.

### 3.9 Estadios

- Para la fase de grupos, a cada equipo se le asigna un estadio local al azar.
- Cada partido de eliminatorias se juega en un estadio distinto, por eso hacen falta
  al menos 13 (4 cuartos × 2 + 2 semifinales × 2 + 1 final).
- Al terminar las eliminatorias, los estadios usados se quitan de su ciudad sólo en
  memoria. La simulación nunca modifica la base de datos.

### 3.10 Persistencia

| Datos | Dónde | Por qué |
|---|---|---|
| Ciudades y estadios | PostgreSQL (`ciudades`, `estadios`) | Datos maestros que pide la consigna, editables desde la interfaz |
| Estado del torneo | `torneo.save` (serialización de `TournamentSession`) | Permite cerrar la aplicación y retomar donde quedó |
| Equipos y personas | `docs/torneo.json` | Archivo de entrada provisto por la cátedra |

Todas las clases del dominio que se guardan en la sesión implementan `Serializable`.
Si un `torneo.save` viejo no se puede leer (por ejemplo, porque cambió una clase), la
aplicación avisa y ofrece empezar un torneo nuevo en lugar de cerrarse.

El script de la base es `docs/database.sql`. `estadios.idciudad` es clave foránea a
`ciudades` con `ON DELETE RESTRICT`.

Si un `torneo.save` fue generado con una versión anterior de las clases, al cargarlo
puede fallar con `IOException`, `ClassNotFoundException` o `ClassCastException`; en
todos los casos la aplicación avisa y arranca un torneo nuevo.

### 3.11 ABM de ciudades y estadios

- **Ciudades** (página *Ciudades*, `CitiesView`): alta, modificación (nombre y país) y baja.
- **Estadios** (página *Stadiums*, `StadiumsView`): alta, modificación (incluido el ID,
  la ciudad, el nombre y la capacidad) y baja.
- Las operaciones pasan por `TournamentSession` (`addCity`, `updateCity`, `deleteCity`,
  `addStadium`, `updateStadium`, `deleteStadium`), que delega en `CityDao` y `StadiumDAO`.

**Regla: no se puede borrar una ciudad con estadios.** `CityDao.deleteCity()` trabaja en
una transacción:

1. Bloquea la tabla `estadios` (`LOCK TABLE ... IN SHARE MODE`) para que nadie asigne un
   estadio a esa ciudad mientras se valida.
2. Verifica que la ciudad exista.
3. Si tiene estadios, cancela la operación y muestra un mensaje que los lista:
   *"No se puede eliminar la ciudad seleccionada: Buenos Aires, debido a que contiene
   estadios asignados: …"*.
4. Si no tiene, la borra y confirma la transacción.

La clave foránea de la base es un segundo control por si la validación se saltea.

`StadiumDAO` también valida dentro de una transacción que la ciudad exista y que el ID no
esté repetido, y traduce los errores de restricciones de PostgreSQL a mensajes claros.

### 3.12 Fotos de los carnets

El archivo JSON no trae fotos. El exportador busca en la carpeta `photos/` un archivo con
el valor del código de barras del jugador como nombre (`.jpg`, `.jpeg` o `.png`). Si no lo
encuentra, dibuja las iniciales del jugador sobre un recuadro gris, así el PDF siempre sale completo.

---

## 4. Reportes

| Reporte | Contenido |
|---|---|
| Carnets (PDF) | Un carnet por jugador con foto, datos y código de barras |
| Goleadores | Jugadores ordenados por goles |
| Fair play | Equipos ordenados por puntaje disciplinario |
| Minutos jugados | Jugadores ordenados por minutos |
| Cuadro de cruces | Llaves de eliminación directa y resultados |
| Equipos | Listado de equipos con director técnico y estadio |
| Árbitros | Partidos dirigidos y tarjetas mostradas |
| Jugadores por posición | Jugadores agrupados por posición |

---

## 5. Diagrama de clases

El diagrama de clases actualizado está en `docs/class-diagram.png`. La fuente es
`docs/class-diagram.puml` (PlantUML): si cambia el modelo, se edita ese archivo y se
vuelve a exportar la imagen (por ejemplo con el plugin *PlantUML Integration* de IntelliJ).

---

## 6. Cómo ejecutar

Ver `README.md` en la raíz del repositorio.
