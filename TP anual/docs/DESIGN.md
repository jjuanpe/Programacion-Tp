# Documento de Diseño — Copa Internacional de Clubes
## Programación B — UFASTA — Avance 1

---

## 1. Estado del avance

Esta entrega cubre la **Fase 1** del plan de trabajo: el modelo de dominio base
(personas, jugadores, sin todavía carga de archivo, simulación, ni interfaz).

---

## 2. Decisiones de diseño tomadas (y su justificación)

### 2.1 — Separación Arquero / JugadorDeCampo

El diagrama sugerido por la cátedra propone una única clase `Jugador` con un
atributo `posicion` (enum con 4 valores: arquero, defensor, mediocampista,
delantero). Al revisar el archivo de datos real (`campeonato.json`) provisto
como ejemplo, se observó que:

- Los **arqueros** registran características totalmente distintas al resto
  (`reflejos`, `juegoAereo`, `ubicacion`, `achique`, `seguridadManos`,
  `juegoPies`) y estadísticas propias (`golesRecibidos`, `penalesAtajados`).
- Los **jugadores de campo** (defensor/mediocampista/delantero) comparten
  entre sí un conjunto de características y estadísticas distinto
  (`capacidadQuite`, `velocidad`, `habilidad`, `cabezazo`, `definicion`,
  `potenciaDisparo`, `visionDeJuego`, `resistenciaFisica`, y estadísticas de
  `goles`/`pasesGol`/`penalesConvertidos`).

**Decisión:** se reemplaza el enum de 4 posiciones por una jerarquía:

```
Jugador (abstracta)
 ├── Arquero
 └── JugadorDeCampo
      (posicion: DEFENSOR | MEDIOCAMPISTA | DELANTERO)
```

**Justificación:** esto permite resolver el cálculo de "fuerza"/valoración
del jugador (necesario para la simulación de partidos, Fase 4) de forma
**polimórfica**, sin condicionales sobre el tipo de posición — cumpliendo
el requisito de aprobación que exige aplicar polimorfismo de forma
indefectible. Es una mejora de diseño respecto al sugerido, no una
desviación arbitraria: el diagrama de la cátedra se aclara como "sugerido".

### 2.2 — Nombre de persona: campo único, no separado

El enunciado indica registrar "apellido y nombre" como si fueran dos campos,
pero el archivo de datos real trae un único campo `nombre` con ambos
combinados (ej: `"Fontanals Facundo"`, `"Lobos Vallejos Valentín"`).

**Decisión:** se mantiene como un único atributo `nombreCompleto` en
`Persona`, en lugar de intentar separar apellido/nombre por posición de
palabra (un split ingenuo fallaría con apellidos compuestos, como se ve en
varios registros del archivo real).

### 2.3 — Fecha de nacimiento con `LocalDate`

Se utiliza `java.time.LocalDate` (no `Date`, clase legada) para
`fechaNacimiento`, parseando el formato `dd/MM/yyyy` que trae el archivo de
origen mediante `DateTimeFormatter`.

---

## 3. Diagrama de clases — Fase 1

```
Persona (abstracta)
 - nombreCompleto: String
 - fechaNacimiento: LocalDate
 - tipoDocumento: String
 - numeroDocumento: long
 + getEdad(): int   [método común, calculado]

Jugador extends Persona (abstracta)
 - partidosJugados: int
 - expulsiones: int
 + getValoracion(): double   [abstracto — polimórfico]

Arquero extends Jugador
 - reflejos, juegoAereo, ubicacion, achique, seguridadManos, juegoPies: int
 - golesRecibidos, penalesRecibidos, penalesAtajados: int

JugadorDeCampo extends Jugador
 - posicion: PosicionCampo
 - capacidadQuite, velocidad, habilidad, cabezazo, definicion,
   potenciaDisparo, visionDeJuego, resistenciaFisica: int
 - goles, penales, penalesConvertidos, pasesGol: int

PosicionCampo <<enum>>
 - DEFENSOR, MEDIOCAMPISTA, DELANTERO

DT extends Persona
 - nacionalidad: Pais
 - cantidadTitulos: int

Referi extends Persona
 - nacionalidad: Pais
 - cantidadAniosReferato: int

Pais
 - nombre: String
```

---

## 4. Pendiente para el próximo avance

- [ ] `Equipo` (con su plantel de 18 jugadores + DT)
- [ ] Carga de datos desde el archivo JSON (evaluar librería Gson)
- [ ] Sorteo equilibrado de zonas
- [ ] Tabla de posiciones con criterio de desempate en cascada

---

## 5. Notas de gestión del proyecto

*(completar con el equipo: link al tablero de tareas, sprint actual, y
responsable de cada módulo)*

---

## 6. Cómo correr `app.MainFX` (JavaFX) en IntelliJ

Desde Java 11, JavaFX **no viene incluido en el JDK** — hay que agregarlo aparte:

1. Descargar el **JavaFX SDK** (versión compatible con tu JDK, ej. 21) desde
   `https://gluonhq.com/products/javafx/` — elegir el SDK, no el jmods.
2. En IntelliJ: `File → Project Structure → Libraries → +` → agregar la
   carpeta `lib` del JavaFX SDK descargado.
3. En la configuración de ejecución (`Run → Edit Configurations`) de
   `MainFX`, agregar en **VM options**:
   ```
   --module-path "C:\ruta\a\javafx-sdk-21\lib" --add-modules javafx.controls
   ```
   (ajustar la ruta a donde hayan descomprimido el SDK).
4. Ejecutar `MainFX.main()`.

**Nota:** `app.Main` (Swing) sigue funcionando sin este paso extra — Swing sí
viene incluido en el JDK estándar.
