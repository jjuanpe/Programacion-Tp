# Documento de Diseño — Copa Internacional de Clubes
## Programación B — UFASTA — Entrega final

---

## 1. Alcance

La aplicación cubre el campeonato completo:

- Carga de equipos, jugadores, directores técnicos y árbitros desde un archivo JSON (`docs/torneo.json`).
- Ciudades y estadios guardados en PostgreSQL, con alta, baja y modificación desde la interfaz.
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
| `model.match` | `Match` (abstracta) y `GroupMatch`, `FirstLegMatch`, `SecondLegMatch`, `FinalMatch` |
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

### 3.4 Incidencias

Cada evento de un partido es una subclase de `Incidence` (gol, amarilla, expulsión,
cambio, penal, jugada). Cada una da su propia descripción, así la interfaz puede listar
las incidencias de un partido sin conocer su tipo concreto.

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

La tabla de antes y después de cada partido de zona se guarda junto con el partido y se
muestra en el detalle del partido.

### 3.8 Desempate en eliminación directa

Las llaves de ida y vuelta se definen, en este orden, por:
1. puntos obtenidos en los dos partidos,
2. diferencia de gol, con los goles de visitante valiendo doble,
3. definición por penales.

La aplicación informa cuál de los tres criterios definió al ganador.
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
`ciudades` con `ON DELETE RESTRICT`; además, la aplicación valida la regla y muestra un
mensaje claro antes de intentar borrar una ciudad que tiene estadios.

### 3.11 Fotos de los carnets

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

El diagrama de clases actualizado está en `docs/class-diagram.puml` (fuente PlantUML).

---

## 6. Cómo ejecutar

Ver `README.md` en la raíz del repositorio.
