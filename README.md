# Programacion-Tp

Proyecto Java 21 con Maven, JavaFX, Gson y PostgreSQL.

## Ejecutar

1. Abrir `TP anual` como proyecto Maven en IntelliJ (JDK 21 o superior).
2. En pgAdmin, crear la base `tpprogra` en `localhost:5432` y ejecutar el script `TP anual/docs/database.sql`. El script crea las tablas `ciudades` (`idciudad`, `nombre`, `pais`) y `estadios` (`idestadio`, `idciudad`, `nombre`, `capacidad`, con clave foránea a `ciudades`) y carga 16 ciudades y 16 estadios de ejemplo. Se necesitan al menos 13 estadios para las eliminatorias. El script borra y vuelve a crear las tablas, así que también sirve para dejar la base limpia.
3. Si las credenciales difieren de las originales del proyecto, configurar `TP_DB_URL`, `TP_DB_USER` y `TP_DB_PASSWORD` en el entorno.
4. Desde `TP anual`, ejecutar `mvn clean package` para compilar y `mvn javafx:run` para abrir la interfaz.
5. En Tournament, importar `docs/torneo.json` y ejecutar el sorteo. Alli se leen las ciudades y los estadios desde PostgreSQL.

Los partidos de grupos muestran su estadio y pueden reutilizar sedes. Cada partido de eliminatorias usa un estadio distinto. Cuando termina la fase eliminatoria, los estadios utilizados se quitan de las listas de sus ciudades en memoria; una ciudad sale de la lista de ciudades disponibles cuando ya no tiene estadios. Los registros de PostgreSQL no se borran durante la simulacion.

No se puede eliminar una ciudad que tenga estadios asociados.

Las fotos de los carnets se buscan en la carpeta `photos/` (directorio de trabajo), con el valor del código de barras del jugador como nombre de archivo (`.jpg`, `.jpeg` o `.png`). Si no hay foto, el carnet muestra las iniciales del jugador.
