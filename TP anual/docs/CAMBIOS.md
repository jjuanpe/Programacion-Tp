# Registro de Cambios — Copa Internacional de Clubes
## Programación B — UFASTA — 2026

Bitácora de los cambios hechos sobre el proyecto: qué se tocó, por qué, y qué
decisión técnica hay detrás. El orden es cronológico, del más viejo al más nuevo.

Convención de cada entrada:

- **Qué cambió** — archivos creados / modificados / eliminados
- **Por qué** — motivo del cambio
- **Decisiones** — sólo cuando hubo una alternativa descartada que vale la pena dejar asentada

---

## 2026-09-03 — Interfaz gráfica: barra lateral (sidebar)

Primer componente de la interfaz JavaFX. Sólo la barra lateral izquierda; sin
Dashboard, sin contenido central y sin navegación.

**Qué cambió**

| Archivo | Estado | Rol |
|---|---|---|
| `src/ui/fx/NavItem.java` | nuevo | Enum con las 10 secciones y su etiqueta en inglés |
| `src/ui/fx/Sidebar.java` | nuevo | Componente de la barra lateral (encabezado, opciones, pie) |
| `src/ui/fx/styles.css` | nuevo | Hoja de estilos de toda la interfaz |
| `src/app/MainFX.java` | modificado | Pasó de clase vacía a punto de entrada de la app |

**Por qué**

Arranque de la interfaz de escritorio pedida por el TP.

**Decisiones**

- **Bajo acoplamiento interfaz / dominio.** Ni `Sidebar` ni `NavItem` importan
  una sola clase de `model` o `dataload`. El sidebar sólo sabe dibujarse y
  avisar qué opción se eligió, mediante `setOnSelect(Consumer<NavItem>)`. Ese
  método es la costura prevista para conectar la navegación más adelante: hoy
  nadie se suscribe.
- **Nada de `setStyle()`.** Toda la apariencia vive en `styles.css`; el código
  Java sólo asigna clases de estilo (`getStyleClass().add(...)`). Los colores
  están definidos como variables en `.root` (`-sidebar-bg`, `-accent`, etc.)
  para no repetir literales hexadecimales por todo el archivo.
- **La opción activa se marca con una clase CSS** (`.active`), no con un estilo
  en línea, así el resaltado azul se puede cambiar sin tocar Java.

---

## 2026-09-03 — Instalación y configuración de JavaFX

**Qué cambió**

| Archivo | Estado | Rol |
|---|---|---|
| `lib/javafx-sdk-26.0.1/` | nuevo | SDK de JavaFX (115 MB) |
| `TP anual.iml` | modificado | Librería `JavaFX 26.0.1` (base, graphics, controls, fxml) |
| `.idea/runConfigurations/MainFX.xml` | nuevo | Configuración de ejecución con las VM options |
| `.gitignore` | nuevo | Ignora `out/` y `lib/javafx-sdk-*/` |

**Por qué**

JavaFX no viene incluido en el JDK desde Java 11: hay que bajarlo aparte y
agregarlo al proyecto.

**Decisiones**

- **Versión 26.0.1, no la 21 LTS.** El JDK configurado en el proyecto es
  `openjdk-26`. JavaFX 21 usa APIs internas (`sun.misc.Unsafe`) que en JDK 26
  ya están restringidas, así que se eligió la versión que acompaña al JDK.
- **El SDK no se versiona.** Son 115 MB; entra en `.gitignore`. Cada integrante
  del grupo lo baja de gluonhq.com y lo descomprime en `lib/`.
- **VM options necesarias** para que arranque sin advertencias:

  ```
  --module-path "$PROJECT_DIR$/lib/javafx-sdk-26.0.1/lib"
  --add-modules javafx.controls
  --enable-native-access=javafx.graphics
  ```

  Sin ellas la aplicación igual arranca (ver la entrada siguiente), pero imprime
  advertencias de JavaFX cargado desde el classpath y de acceso nativo.

---

## 2026-09-03 — Corrección: ruta del archivo de datos en `Main`

**Qué cambió**

`src/app/Main.java` — la constante `DEFAULT_DATA_PATH` pasó de
`"claudedocs/torneo.json"` a `"docs/torneo.json"`.

**Por qué**

La ruta apuntaba a una carpeta inexistente y la simulación abortaba con
*"The tournament could not be completed: claudedocs\torneo.json (El sistema no
puede encontrar la ruta especificada)"*. El archivo está en `docs/torneo.json`.

**Nota:** la ruta es relativa, así que `Main` necesita que el directorio de
trabajo sea la raíz del proyecto (es el valor por defecto en IntelliJ). Si hace
falta, `Main` acepta una ruta alternativa como primer argumento.

---

## 2026-09-03 — Separación del lanzador JavaFX (`MainFX` / `AppWindow`)

**Qué cambió**

| Archivo | Estado | Rol |
|---|---|---|
| `src/ui/fx/AppWindow.java` | nuevo | La ventana propiamente dicha (`extends Application`) |
| `src/app/MainFX.java` | modificado | Quedó como lanzador: una clase común que llama a `Application.launch(AppWindow.class, args)` |
| `TP anual.iml` | modificado | La librería JavaFX pasó de scope `PROVIDED` a compilación normal |

**Por qué**

Ejecutar `MainFX` fallaba con `NoClassDefFoundError: javafx/application/Application`.

**Decisiones**

- **El punto de entrada no extiende `Application` a propósito.** Cuando la clase
  principal extiende `Application`, la JVM exige que JavaFX venga como módulo
  (`--module-path`) y aborta con *"JavaFX runtime components are missing"*
  incluso teniendo los `.jar` en el classpath. Lanzando desde una clase común se
  saltea esa verificación.
- **Resultado:** la aplicación arranca de las dos formas — con las VM options
  (sin ninguna advertencia) y sin ellas (funciona igual, con advertencias
  cosméticas). Deja de depender de que el IDE lea la configuración de ejecución.
- De paso, la separación es más prolija: `app.MainFX` es el punto de entrada y
  `ui.fx.AppWindow` es la ventana.

---

## 2026-09-03 — Interfaz gráfica: encabezado del Dashboard

Encabezado superior del área central. Sin tarjetas, partidos, grupos,
estadísticas, botones ni progreso del torneo.

**Qué cambió**

| Archivo | Estado | Rol |
|---|---|---|
| `src/ui/fx/DashboardHeader.java` | nuevo | Título, subtítulo y estado de guardado |
| `src/ui/fx/AppWindow.java` | modificado | El área central pasó de `StackPane` vacío a `BorderPane` con el encabezado arriba |
| `src/ui/fx/styles.css` | modificado | Fondo del contenido a `#F5F7FA` y estilos del encabezado |

El sidebar no se tocó.

**Decisiones**

- **Ocupa todo el ancho** gracias a un `Region` intermedio con
  `HBox.setHgrow(spacer, Priority.ALWAYS)`: los títulos quedan a la izquierda y
  el estado pegado al borde derecho al redimensionar la ventana.
- **El punto verde no es el carácter `●`**, es un `Region` de 9×9 con
  `-fx-background-radius`. Así el color y el tamaño se controlan desde el CSS y
  no dependen de que la fuente instalada tenga ese glifo.
- **Misma costura de desacoplamiento que el sidebar:** `setSubtitle(String)` y
  `setStatus(String)` permiten alimentarlo desde el dominio más adelante. Por
  ahora los textos están fijos.

---

## 2026-09-03 — Interfaz gráfica: fila de tarjetas de resumen

Tres tarjetas (Teams, Matches Played, Goals) debajo del encabezado del
Dashboard. Sin valores escritos en la vista.

**Qué cambió**

| Archivo | Estado | Rol |
|---|---|---|
| `src/ui/fx/DashboardViewModel.java` | nuevo | Estado observable del Dashboard (`teams`, `matchesPlayed`, `goals`) |
| `src/ui/fx/SummaryCard.java` | nuevo | Tarjeta reutilizable: título chico + valor grande |
| `src/ui/fx/AppWindow.java` | modificado | Crea el ViewModel y ata las tarjetas a sus propiedades |
| `src/ui/fx/styles.css` | modificado | Estilos de la fila y de las tarjetas |

**Decisiones**

- **Ningún valor vive en la vista.** `SummaryCard` no recibe un texto sino un
  `ObservableValue<String>`, y ata la etiqueta con
  `valueLabel.textProperty().bind(value)`. Cuando el ViewModel cambie, las
  tarjetas se actualizan solas: no hace falta ningún método de "refrescar".
- **Se agregó un ViewModel en vez de leer el dominio desde la vista.**
  `DashboardViewModel` expone `IntegerProperty` (patrón estándar de JavaFX:
  `teamsProperty()` / `getTeams()` / `setTeams()`) y **no importa ninguna clase
  de `model`**. Un controlador va a completarlo más adelante a partir del
  campeonato real. La vista queda del todo ignorante del dominio, y el
  ViewModel se puede probar sin levantar la interfaz.
- **`AppWindow` es el punto de composición:** es el único lugar donde se cruzan
  ViewModel y vistas (`viewModel.teamsProperty().asString()`). Las tarjetas
  siguen siendo genéricas y reusables para cualquier otro dato.
- **Las tres tarjetas miden siempre lo mismo.** En el CSS llevan
  `-fx-pref-width: 0` y desde Java `HBox.setHgrow(card, Priority.ALWAYS)`: al
  partir todas del mismo ancho base, el `HBox` reparte el sobrante en partes
  iguales. Si se dejara el ancho preferido calculado por el contenido, la
  tarjeta con el texto más largo quedaría más ancha.
- **Sombra por CSS** (`-fx-effect: dropshadow(...)`), no por código.

**Pendiente**

Todavía nada completa el ViewModel, así que las tres tarjetas muestran `0`.
Es el estado real (no hay campeonato cargado), no un valor de ejemplo. Se
resuelve al conectar el controlador con `Championship`.

---

## 2026-09-03 — Interfaz gráfica: sección "Tournament Progress"

Stepper horizontal con las cuatro etapas del torneo. Sin porcentajes ni datos
extra.

**Qué cambió**

| Archivo | Estado | Rol |
|---|---|---|
| `src/ui/fx/TournamentStage.java` | nuevo | Enum de etapas, en orden de avance |
| `src/ui/fx/TournamentProgress.java` | nuevo | La sección con el stepper |
| `src/ui/fx/DashboardViewModel.java` | modificado | Nueva propiedad `currentStage` |
| `src/ui/fx/AppWindow.java` | modificado | Suma la sección al cuerpo del Dashboard |
| `src/ui/fx/styles.css` | modificado | Estilos del stepper y ajuste del espaciado del cuerpo |

**Decisiones**

- **La etapa actual no está escrita en la vista.** `TournamentProgress` recibe
  un `ObservableValue<TournamentStage>` y se suscribe: cuando el ViewModel
  cambia la etapa, el stepper se redibuja solo.
- **Los tres estados se deducen, no se cargan por separado.** Alcanza con saber
  cuál es la etapa actual: comparando por `ordinal()` contra el orden de
  declaración del enum, las anteriores quedan `completed`, la actual `current`
  y las posteriores `pending`. Guardar los tres estados a mano permitiría
  estados imposibles (por ejemplo una etapa completada después de una
  pendiente).
- **`currentStage` arranca en `null`**, que representa "el campeonato todavía no
  arrancó": todas las etapas se muestran pendientes. Es un estado real, no un
  valor de ejemplo.
- **Los estados son clases CSS** (`.completed`, `.current`, `.pending`), no
  estilos aplicados desde Java. La distinción visual queda entera en
  `styles.css`: círculo vacío con borde gris para pendiente, círculo lleno azul
  para completada, y azul un poco más grande con destello para la actual.
- **Los conectores crecen** (`Hgrow ALWAYS`), así el stepper ocupa todo el ancho
  y se estira al agrandar la ventana; las etapas conservan su ancho natural.
- **El orden del enum es la fuente de verdad** del avance. Si más adelante se
  agregan octavos de final, se insertan en la posición correspondiente y el
  stepper se ajusta solo.

**Verificación**

Se probó el cálculo de estados con las cuatro etapas y con `null`: en cada caso
las anteriores quedan completadas, la actual marcada, y las siguientes
pendientes.

---

## 2026-09-03 — Reporte: Top Scorers

Ranking de goleadores del campeonato, calculado desde las incidencias reales.
Es el primer componente de la interfaz que consume datos del dominio.

**Qué cambió**

| Archivo | Estado | Rol |
|---|---|---|
| `src/model/competition/ScorerEntry.java` | nuevo | Fila del ranking: jugador, equipo, goles, goles de penal, posición |
| `src/model/competition/TopScorersService.java` | nuevo | Calcula el ranking a partir de los partidos |
| `src/controller/TournamentSession.java` | nuevo | Carga el archivo de datos, juega el campeonato y reúne todos los partidos |
| `src/controller/TopScorersController.java` | nuevo | Pide el cálculo al dominio y traduce el resultado a filas de la vista |
| `src/ui/fx/ScorerRow.java` | nuevo | Fila lista para mostrar (sólo texto y números) |
| `src/ui/fx/TopScorersViewModel.java` | nuevo | Lista observable de filas + mensaje de tabla vacía |
| `src/ui/fx/TopScorersView.java` | nuevo | La sección con el `TableView` |
| `src/ui/fx/AppWindow.java` | modificado | Suma la sección, envuelve el cuerpo en un `ScrollPane` y dispara la carga |
| `src/ui/fx/styles.css` | modificado | Estilos de la tabla y del contenedor scrolleable |

**Reglas de conteo**

- Sólo cuentan las incidencias de tipo `Goal`, es decir goles convertidos
  durante el partido.
- **Los penales de una definición por penales no cuentan.** La tanda se
  registra como `PenaltyExecuted`, una clase distinta de `Goal`, así que queda
  afuera por construcción. Además `Match.completeMatch()` valida que la
  cantidad de incidencias `Goal` coincida con el marcador, lo que impide que un
  penal de la tanda se cuele como gol.
- Un penal convertido **durante** el partido sí cuenta, y además suma en la
  columna `Penalty Goals` (`Goal.isPenalty()`).
- Los goles en contra no se le acreditan al jugador que los hizo: no son goles
  convertidos por él. La regla no lo decía explícitamente; se dejó asentado.
- Los jugadores sin goles no aparecen: sólo se crea una entrada cuando el
  jugador convierte.

**Decisiones**

- **Ningún dato en la vista.** `TopScorersView` recibe una
  `ObservableList<ScorerRow>` de sólo lectura y la muestra. Las filas guardan
  texto y números: no referencian `Player` ni `Team`. El único punto donde se
  cruzan dominio e interfaz es el controlador.
- **Paquete `controller` nuevo.** Hacía falta una capa que conociera los dos
  lados sin ensuciar ninguno.
- **Posiciones con empate compartido** (1, 2, 2, 4), como en cualquier tabla de
  goleadores. La numeración la asigna el dominio, no la vista.
- **Criterio de orden:** más goles primero; a igualdad de goles, primero el que
  convirtió menos de penal; y por último, orden alfabético, para que el ranking
  sea estable entre ejecuciones.
- **Las columnas no se pueden reordenar** (`setSortable(false)`): el orden del
  ranking ya viene resuelto por el dominio y reordenar por columna lo perdería.
- **La carga corre en un hilo aparte** (`javafx.concurrent.Task`). Cargar el
  JSON y simular el campeonato entero tarda varios segundos: hacerlo en el hilo
  de JavaFX congelaría la ventana. Mientras tanto la tabla muestra
  "Loading...", y si algo falla muestra el motivo en vez de quedar vacía.
- **El cuerpo del Dashboard ahora scrollea** (`ScrollPane`): con la tabla
  agregada, el contenido ya no entra en una ventana de 720 px de alto. No se
  modificó ningún componente ya aprobado.

**Verificación**

Sobre un campeonato simulado completo (37 partidos):

- Suma de marcadores: 74 — incidencias `Goal`: 74. No falta ni sobra ningún gol.
- Goles en el ranking: 71 = 74 − 3 goles en contra.
- Goles de penal en el ranking: 9, igual a las incidencias `Goal` con
  `isPenalty()`.
- 51 goleadores listados sobre 288 jugadores: los que no convirtieron quedan
  afuera.
- Posiciones con empate: 1, 1, 1, 4.

**Hallazgo**

En `KnockoutTieTask` (línea 107) la tanda de penales se simula pero el
resultado (`PenaltyShootoutResult.getKicks()`) se descarta: esos
`PenaltyExecuted` nunca se agregan a las incidencias del partido. Para este
reporte da igual — filtramos por `Goal` —, pero conviene tenerlo presente si
más adelante se quiere reportar la tanda.

**Pendiente**

`TournamentSession` repite la secuencia de armado del campeonato que ya hace
`app.Main`. Convendría que la versión de consola use esta misma clase para no
mantener dos copias.

---

## 2026-09-03 — El Dashboard pasa a ser sólo resumen

Se definió el alcance de cada página de la aplicación. El Dashboard muestra el
panorama general del campeonato y nada de detalle: los reportes van a la página
*Reports*.

**Qué cambió**

| Archivo | Estado | Rol |
|---|---|---|
| `src/controller/DashboardController.java` | nuevo | Completa el resumen a partir del estado real del campeonato |
| `src/controller/TournamentLoader.java` | nuevo | Carga y simula el campeonato en segundo plano, una sola vez |
| `src/controller/TournamentSession.java` | modificado | Guarda el resultado de la fase eliminatoria y expone los partidos de grupos |
| `src/controller/TopScorersController.java` | modificado | Ya no carga el campeonato: recibe la sesión ya armada |
| `src/ui/fx/DashboardViewModel.java` | modificado | Nueva propiedad `stageLabel` |
| `src/ui/fx/DashboardHeader.java` | modificado | El subtítulo se ata al ViewModel en vez de estar fijo |
| `src/ui/fx/AppWindow.java` | modificado | Saca la sección de goleadores del Dashboard y conecta el resumen |
| `src/ui/fx/TopScorersViewModel.java` | modificado | Mensaje inicial: "No report generated yet" |

`TopScorersService`, `TopScorersView` y `TopScorersController` **no se
eliminaron**: quedan listos para la página *Reports*, que es donde corresponden.

**Decisiones**

- **La sección "Top Scorers" salió del Dashboard.** Es información de reporte;
  según el alcance definido, el Dashboard no muestra rankings.
- **El resumen ahora es real.** Las tarjetas dejaron de mostrar `0`: el
  controlador cuenta equipos cargados, partidos disputados y goles convertidos
  a partir de la sesión del campeonato.
- **La fase actual se deduce del estado de los partidos**, no se carga a mano:
  es la primera fase que todavía no terminó (grupos → cuartos → semis → final).
  La traducción al enum de interfaz (`TournamentStage`) la hace el controlador,
  así el dominio sigue sin depender de `ui.fx`.
- **El subtítulo del encabezado dejó de estar fijo.** Decía siempre
  "Group Stage · Matchday 2", que era falso apenas avanzaba el torneo. Ahora se
  ata a `stageLabel` y muestra la fase real, o el estado de la carga.
- **La carga se extrajo a `TournamentLoader`.** Antes vivía dentro de
  `TopScorersController`; con dos controladores necesitando los mismos datos,
  dejarla ahí habría simulado el torneo dos veces. Ahora se carga una sola vez
  y la misma `TournamentSession` se reparte.

**Verificación**

Sobre un campeonato simulado completo: 16 equipos, 37 partidos disputados de 37,
79 goles, fase `FINAL` — todos los valores coinciden con el conteo directo sobre
los partidos. Durante la carga el encabezado muestra "Loading tournament
data..." y ante un fallo muestra el motivo.

Las ramas intermedias del cálculo de fase (cuartos, semis) todavía no se
ejercitan, porque la aplicación simula el torneo entero al arrancar. Se van a
poder probar cuando la página *Tournament* maneje el avance por etapas.

**Pendiente del Dashboard**

Falta la mitad de "acciones principales" del alcance: los accesos rápidos a la
próxima acción pendiente y a las secciones principales. Necesitan que la
navegación del sidebar esté implementada para llevar a algún lado.

---

## 2026-09-03 — Navegación entre páginas

Cada opción del sidebar ahora lleva a una página distinta.

**Qué cambió**

| Archivo | Estado | Rol |
|---|---|---|
| `src/ui/fx/PlaceholderPage.java` | nuevo | Página todavía no implementada: título y qué va a mostrar |
| `src/ui/fx/AppWindow.java` | modificado | Arma el mapa de páginas y las intercambia según la opción elegida |
| `src/ui/fx/styles.css` | modificado | Estilos genéricos de página y panel |

**Decisiones**

- **Se usó la costura que ya existía.** `Sidebar` exponía
  `setOnSelect(Consumer<NavItem>)` desde el primer día sin que nadie se
  suscribiera; ahora `AppWindow` se ata ahí. No hubo que tocar el sidebar.
- **El encabezado es fijo y sólo cambia el cuerpo.** Título del torneo, fase y
  estado de guardado son de la aplicación, no de la página.
- **Las páginas se crean una sola vez** y se guardan en un `EnumMap`, así al
  volver a una sección conserva su estado (por ejemplo el mensaje de la última
  acción en Tournament).
- **Las páginas sin implementar no muestran datos de ejemplo:** dicen qué van a
  mostrar y nada más.

**Verificación**

Se recorrieron las diez opciones del sidebar: cada una muestra su página y
marca la opción correspondiente como activa.

---

## 2026-09-03 — El torneo deja de simularse solo al arrancar

La aplicación abría cargando el archivo y jugando el campeonato entero. Ahora
arranca vacía y el torneo avanza por acciones explícitas desde la página
*Tournament*.

**Qué cambió**

| Archivo | Estado | Rol |
|---|---|---|
| `src/controller/TournamentState.java` | nuevo | Los cinco estados del ciclo de vida |
| `src/controller/TournamentController.java` | nuevo | Traduce las acciones del usuario en pasos del torneo |
| `src/ui/fx/TournamentViewModel.java` | nuevo | Datos del campeonato + qué acciones están habilitadas |
| `src/ui/fx/TournamentView.java` | nuevo | La página con el panel de estado y la botonera |
| `src/controller/TournamentSession.java` | reescrito | De "cargar y simular todo" a un ciclo de vida paso a paso |
| `src/controller/DashboardController.java` | modificado | El resumen se deriva del estado del torneo |
| `src/controller/TournamentLoader.java` | eliminado | Ya no hay una carga automática que orquestar |
| `src/ui/fx/AppWindow.java` | modificado | Crea la sesión compartida y ata los controladores |

**Ciclo de vida**

| Estado | Qué falta | Acción habilitada |
|---|---|---|
| `EMPTY` | todo | Import data |
| `DATA_LOADED` | el sorteo | Run group draw |
| `GROUPS_DRAWN` | jugar los grupos | Start group stage |
| `GROUP_STAGE_PLAYED` | las eliminatorias | Continue: knockout stage |
| `FINISHED` | nada | — |

**Decisiones**

- **El estado no se guarda en un campo:** `TournamentSession.getState()` lo
  deduce de lo que hay cargado y jugado. Un campo aparte podría quedar
  desincronizado con los datos reales; así es imposible.
- **Una sola `TournamentSession` compartida.** La crea `AppWindow` y la reciben
  los dos controladores, de modo que el Dashboard y la página Tournament
  siempre miran el mismo campeonato.
- **El Dashboard se entera por un callback.** `TournamentController` avisa con
  `onStateChanged` después de cada paso; `AppWindow` lo engancha a
  `dashboardController::refresh`. Los controladores no se conocen entre sí.
- **Cada acción corre en un hilo aparte** y deshabilita la botonera mientras
  tanto. Jugar la fase de grupos tarda: hacerlo en el hilo de JavaFX congelaría
  la ventana.
- **Los métodos de la sesión son `synchronized`**, porque las simulaciones
  corren en un hilo mientras la interfaz lee el estado desde otro.
- **Las precondiciones se validan en la sesión** (`requireState`), no sólo
  deshabilitando botones: la regla vive en un solo lugar y no depende de que la
  interfaz esté bien.
- **Un import fallido no rompe lo que había:** el archivo se lee primero y
  recién después se reemplaza el estado.
- **Elegir el archivo lo hace la vista** (`FileChooser`), que es una decisión de
  interfaz; el controlador sólo recibe una ruta.

**Verificación**

Se recorrió el ciclo completo:

| Paso | Estado | Datos | Dashboard |
|---|---|---|---|
| al abrir | No data imported | — | 0 equipos, sin fase |
| Import data | Data imported | 16 equipos, 19 árbitros, 1 aviso | 16 equipos, sin fase |
| Run group draw | Group stage pending | 4 zonas, 0/24 partidos | fase Group Stage |
| Start group stage | Knockout stage pending | 24/24 partidos | 54 goles, fase Quarterfinals |
| Continue: knockout | Tournament finished | 37/37 partidos | 72 goles, fase Final |

Importar un archivo inexistente muestra el motivo del fallo y deja intacto el
campeonato anterior.

**Pendiente**

- **Guardar el estado no está implementado.** El botón está visible pero
  deshabilitado, con una aclaración en la página. Falta definir qué significa:
  exportar un resumen de resultados, o un guardado que se pueda volver a
  cargar para retomar el torneo.
- La fase de grupos y la eliminatoria se juegan completas de una vez. Si se
  quiere avanzar fecha por fecha o partido por partido, hay que dividir esos
  pasos.

---

## 2026-09-03 — Página Teams: los dos listados

**Qué cambió**

| Archivo | Estado | Rol |
|---|---|---|
| `src/model/team/SquadValidation.java` | nuevo | Revisa el plantel completo y devuelve todos los problemas |
| `src/model/competition/TeamReportEntry.java` | nuevo | Fila del resumen: edades, goles, puntos |
| `src/model/competition/TeamReportService.java` | nuevo | Arma el listado alfabético con el rendimiento |
| `src/controller/TeamsController.java` | nuevo | Traduce el resumen y las fichas a filas de interfaz |
| `src/ui/fx/TeamRow.java` | nuevo | Fila del resumen, lista para mostrar |
| `src/ui/fx/PlayerRow.java` | nuevo | Jugador del plantel |
| `src/ui/fx/TeamDetail.java` | nuevo | Ficha de un equipo |
| `src/ui/fx/TeamsViewModel.java` | nuevo | Los dos listados observables |
| `src/ui/fx/TeamsView.java` | nuevo | La página |
| `src/ui/fx/TableColumns.java` | nuevo | Fábrica de columnas compartida por todas las tablas |
| `src/model/people/Person.java` | modificado | Nuevo `getAge(LocalDate)` |
| `src/ui/fx/TopScorersView.java` | modificado | Usa `TableColumns` en vez de repetir la lógica |
| `src/ui/fx/AppWindow.java` | modificado | La página Teams reemplaza al placeholder |
| `src/ui/fx/styles.css` | modificado | Estilos de la página; `.scorers-table` pasó a `.data-table` |

**Decisiones**

- **La edad se calcula en el dominio.** Se agregó `Person.getAge(LocalDate)` en
  vez de repetir la cuenta con `Period` en el servicio y en el controlador. La
  fecha de referencia se recibe: no se usa `LocalDate.now()` adentro, así el
  cálculo es predecible y se puede probar.
- **Goles y efectividad se cuentan sobre TODOS los partidos jugados**, de grupos
  y de eliminatorias, con el mismo criterio de puntos (3/1/0) y puntos posibles
  = 3 × partidos jugados. Es el criterio uniforme con "goles a favor y en
  contra". *Si la cátedra espera la efectividad sólo sobre la fase de grupos, es
  cambiar la lista de partidos que recibe el servicio.*
- **Validar el plantel no puede ser `Team.validateSquad()`.** Ese método corta
  con una excepción en el primer problema, que sirve para la carga pero no para
  mostrar en pantalla. `SquadValidation` revisa todo y devuelve la lista
  completa, sin lanzar nada. Las cantidades salen de
  `Position.getRequiredPerSquad()`, que ya tenía 2-6-5-5: no se escribieron de
  nuevo.
- **Las fichas se calculan una sola vez, al refrescar.** Seleccionar un equipo
  en la lista sólo cambia cuál se muestra; la vista no recalcula ni consulta el
  dominio.
- **Las dos listas van en el mismo orden alfabético**, así lo que se ve arriba
  se encuentra igual abajo.
- **Los valores con decimales llegan formateados desde el controlador**
  (`%.1f` con `Locale.US`), para que la vista no decida cómo redondear ni
  dependa del locale de la máquina.
- **Se extrajo `TableColumns`** al aparecer la segunda y tercera tabla de la
  aplicación: la lógica de columnas de texto y numéricas estaba duplicada.

**Verificación**

Sobre un campeonato completo:

- Las 16 filas salen en orden alfabético.
- La suma de goles a favor (81) y en contra (81) coincide con los goles del
  torneo: ningún gol se cuenta de más ni de menos.
- Las fichas siguen el mismo orden y traen los 18 jugadores de cada plantel.
- Un plantel vacío se reporta con los cinco problemas: cantidad total y las
  cuatro posiciones.

**Hallazgo en los datos**

**"Los Rompe Redes de UFASTA" tiene 19 jugadores, con 6 delanteros en lugar de
5.** El cargador ya avisaba de la cantidad total (es el aviso que el Dashboard
cuenta); la validación agrega qué posición está de más. La página lo muestra en
rojo en la ficha del equipo.

---

## 2026-09-03 — Página Group Stage

Las cuatro zonas, cada una con sus equipos, su fixture, su tabla de posiciones
y sus clasificados.

**Qué cambió**

| Archivo | Estado | Rol |
|---|---|---|
| `src/controller/GroupStageController.java` | nuevo | Arma las zonas: posiciones, fixture y clasificados |
| `src/ui/fx/StandingRow.java` | nuevo | Fila de la tabla de posiciones |
| `src/ui/fx/FixtureRow.java` | nuevo | Partido del fixture |
| `src/ui/fx/GroupZone.java` | nuevo | Una zona lista para mostrar |
| `src/ui/fx/GroupStageViewModel.java` | nuevo | Las zonas observables |
| `src/ui/fx/GroupStageView.java` | nuevo | La página |
| `src/model/competition/KnockoutBracketService.java` | modificado | `QUALIFIED_PER_ZONE` pasó a ser público |
| `src/ui/fx/AppWindow.java` | modificado | La página reemplaza al placeholder |
| `src/ui/fx/styles.css` | modificado | Estilos de zona y de fila clasificada |

**Decisiones**

- **No se escribió ningún cálculo de posiciones.** `StandingsService` ya existía
  y aplica todos los criterios de desempate del TP (puntos, diferencia de gol,
  goles a favor, enfrentamiento directo, fair play, ranking). El controlador
  sólo lo llama y traduce el resultado.
- **La cantidad de clasificados sale de `KnockoutBracketService`.** Se hizo
  pública su constante `QUALIFIED_PER_ZONE` en vez de escribir "2" de nuevo en
  la interfaz: si mañana clasifican tres por zona, la página acompaña sola.
- **Los clasificados no se muestran hasta que la zona termina.** Mientras queden
  partidos por jugar dice "still to be decided", porque las posiciones todavía
  pueden cambiar y mostrar un clasificado provisorio sería engañoso.
- **Las cuatro zonas se muestran apiladas**, no con pestañas ni selector: la
  consigna pide ver las cuatro, y la página ya scrollea.
- **Los puestos que clasifican se marcan con una clase CSS en la fila**
  (`qualified-row`, aplicada desde un `rowFactory`), no con una columna extra.
- **Las tablas de zona no tienen scroll propio:** su alto se calcula a partir de
  la cantidad de filas, así se ven completas y scrollea la página.
- **El botón de sorteo se repite acá**, como pide el alcance de la página. Es el
  mismo `TournamentController`: no hay una segunda implementación del sorteo, y
  se habilita o deshabilita con la misma propiedad que en Tournament.

**Verificación**

Recorriendo los tres momentos:

| Momento | Resultado |
|---|---|
| sin sorteo | 0 zonas, mensaje "No group draw yet" |
| después del sorteo | 4 zonas, 4 equipos y 6 partidos cada una, todos pendientes, clasificados "still to be decided" |
| fase de grupos jugada | 24 partidos jugados, tablas completas, 8 clasificados |

Controles sobre las tablas ya jugadas:

- Puntos = 3×ganados + empatados en las 16 filas.
- Partidos jugados = ganados + empatados + perdidos en las 16 filas.
- Las tablas quedan ordenadas por puntos de mayor a menor.
- **Los 8 equipos marcados como clasificados son exactamente los 8 que
  `KnockoutBracketService` pone en el cuadro de cuartos.** La página no muestra
  una clasificación paralela: es la misma que juega la fase eliminatoria.
