# Programacion-Tp

Java 21 project with Maven, JavaFX, Gson and PostgreSQL.

## Running the project

1. Open `TP anual` as a Maven project in IntelliJ (JDK 21 or later).
2. In pgAdmin, create the `tpprogra` database on `localhost:5432` and run the script `TP anual/docs/database.sql`. The script creates the tables `ciudades` (`idciudad`, `nombre`, `pais`) and `estadios` (`idestadio`, `idciudad`, `nombre`, `capacidad`, with a foreign key to `ciudades`) and loads 16 sample cities and 16 sample stadiums. At least 13 stadiums are needed for the knockout stage. The script drops and recreates the tables, so it can also be used to reset the database.
3. If the credentials differ from the project's original ones, set `TP_DB_URL`, `TP_DB_USER` and `TP_DB_PASSWORD` in the environment.
4. From `TP anual`, run `mvn clean package` to build and `mvn javafx:run` to open the interface.
5. In Tournament, import `docs/torneo.json` and run the draw. The cities and stadiums are read from PostgreSQL at that point.

Group matches show their stadium and may reuse venues. Every knockout match uses a different stadium. When the knockout stage ends, the stadiums used are removed from their cities' in-memory lists; a city leaves the list of available cities once it has no stadiums left. PostgreSQL records are never deleted during the simulation.

A city that still has stadiums assigned cannot be deleted.

## Squad validation

Every team must have exactly 18 players (2 goalkeepers, 6 defenders, 5 midfielders, 5 forwards). If the JSON file contains a team with a surplus of players in some position, the loader removes random players from that position until the squad is valid, and the Tournament page shows a warning panel naming the team and the removed players.

## ID card photos

The JSON file contains no photos. Decision: ID cards look for a photo in the `photos/` folder (working directory), using the player's barcode value as the file name (`.jpg`, `.jpeg` or `.png`). If no photo is found, the ID card shows the player's initials, so the PDF is always complete.
