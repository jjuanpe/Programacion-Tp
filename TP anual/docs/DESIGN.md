# Design Document — International Club Cup
## Programación B — UFASTA — Final delivery

---

## 1. Scope

The application covers the whole championship:

- Loading of teams, players, head coaches and referees from a JSON file (`docs/torneo.json`).
- Cities and stadiums stored in PostgreSQL, with create, update and delete operations from the
  interface (*Cities* and *Stadiums* pages).
- Balanced draw of 16 teams into 4 zones, round-robin fixture and simulation of the group stage.
- Knockout stage: quarter-finals and semi-finals over two legs, and a single-match final.
- Suspensions carried over between match days and between stages.
- JavaFX desktop interface and eight reports, including the players' ID cards as PDF.
- Saving and resuming the tournament (`torneo.save`).

---

## 2. Architecture

The code is organised in layers. Each layer depends only on the ones below it.

```
 ui.fx   (JavaFX views)  ──►  viewmodel   (view models + table rows)
   │                              ▲
   ▼                              │
 controller  (TournamentSession, VenueCatalog, TournamentStorage, one controller per page)
   │
   ▼
 model ◄── dataload (JSON → model)      dao (PostgreSQL → model)      report (PDF)
```

| Package | Responsibility |
|---|---|
| `model.people` | `Person` (abstract), `Player`, `Goalkeeper`, `FieldPlayer`, `HeadCoach`, `Referee` |
| `model.common` | `Country` (shared by teams, people and venues) |
| `model.team` | `Team`, squad validation |
| `model.venue` | `City`, `Stadium`, `CityInUseException` |
| `model.match` | `Match` (abstract) and `GroupMatch`, `FirstLegMatch`, `SecondLegMatch`, `FinalMatch`; `TieCriterion` enum |
| `model.event` | `Incidence` (abstract) and `Goal`, `YellowCard`, `Expulsion`, `Change`, `PenaltyExecuted`, `PlayEvent` |
| `model.competition` | `Championship`, `Zone`, draw, fixture, standings, fair play and report services |
| `model.simulation` | Match simulation strategy, group and knockout simulators, penalty shoot-outs |
| `dataload` | `JsonTournamentLoader` (Gson) |
| `dao` | `DBManager`, `CityDao`, `StadiumDAO` (JDBC) |
| `report` | `IdCard`, `IdCardPdfExporter` (PDFBox + ZXing barcode) |
| `controller` | `TournamentSession` (tournament state and flow), `VenueCatalog` (cities and stadiums in the database), `TournamentStorage` (save and load) and one controller per page |
| `viewmodel` | View models and table rows: the only classes shared by the controllers and the views |
| `ui.fx` | JavaFX views, sidebar, styling and `AppWindow` |

### 2.1 Low coupling between interface and domain

- No view imports `model`. The views only know `viewmodel` (properties and simple row
  objects such as `StandingRow`, `FixtureRow`, `ScorerRow`, `CityRow`, …) and the controllers.
- The **controllers** read the domain, convert it into rows and fill the view model; the
  view only knows how to draw itself and report the user's actions. For the city and stadium
  forms the view sends plain values (id, name, city id…) and the controller builds the domain
  objects, so the domain validation and its exceptions stay out of the interface.
- `viewmodel` depends on nothing, so `ui.fx` and `controller` never import each other's
  internals: views call controllers, controllers fill `viewmodel`, views read `viewmodel`.
- All the styling lives in `styles.css`; the Java code only assigns style classes.

---

### 2.2 Package dependencies

Every arrow points one way; there is no cycle between packages, not even inside `model`:

| Package | Depends on |
|---|---|
| `app` | `ui.fx` |
| `ui.fx` | `controller`, `viewmodel` |
| `controller` | `viewmodel`, `model`, `dao`, `dataload`, `report` |
| `viewmodel`, `report` | nothing of the project |
| `dao`, `dataload` | `model` |
| `model.simulation` | `model.competition`, `model.match`, `model.event`, `model.team`, `model.people`, `model.venue` |
| `model.competition` | `model.match`, `model.event`, `model.team`, `model.people`, `model.venue`, `model.common` |
| `model.match` | `model.event`, `model.team`, `model.people`, `model.venue` |
| `model.team` | `model.people`, `model.venue`, `model.common` |
| `model.event` | `model.team`, `model.people` |
| `model.people`, `model.venue` | `model.common` |

The two-way link between a match and its incidences is solved with dependency inversion:
`model.event` declares the small interface `IncidenceContext` (what an incidence needs to
know about its match) and `Match` implements it, so `event` never imports `match`.

---

## 3. Design decisions

### 3.1 Player hierarchy

The JSON file provides completely different attributes for goalkeepers and field players,
so `Player` (which stores the `Position`) has two subclasses, `Goalkeeper` and
`FieldPlayer`, each with its own characteristics and statistics.
The player rating used by the simulation is computed polymorphically, with no `if` chains
on the position.

### 3.2 Person name in a single field

The data file provides surname and first name together in one field (for example
`"Lobos Vallejos Valentín"`). It is stored as a single `name` attribute in `Person`,
because splitting by words would fail with compound surnames.

### 3.3 Match hierarchy

`Match` is abstract. Each stage is a subclass that answers its own questions:
stage label, category, and whether it allows a penalty shoot-out
(`allowsPenaltyShootout()` is false for group matches and first legs).

### 3.4 Incidences — polymorphism

Every event in a match is a subclass of `Incidence` (`Goal`, `YellowCard`,
`Expulsion`, `Change`, `PenaltyExecuted`, `PlayEvent`). The abstract class defines:

| Method | Kind | Purpose |
|---|---|---|
| `getTypeLabel()` | abstract | Type label ("Goal", "Own goal", "Goal (penalty)", …) |
| `getDetail()` | abstract | Event detail (player, team, goalkeeper, etc.) |
| `getMinuteLabel()` | concrete | Formatted minute (`45'`) |
| `isShootoutKick()` | concrete, `false` by default | Whether the event is a shoot-out kick |
| `isScoredShootoutKickBy(team)` | concrete, `false` by default | Whether it is a shoot-out kick scored by that team |
| `goalsRecorded()` | concrete, `0` by default | Goals the event adds to the score (`Goal` returns 1) |
| `getExpulsion()` / `getExpelledPlayer()` | concrete, `null` by default | The expulsion and the player sent off (`Expulsion`) |
| `getCreditedScorer()` / `isPenaltyGoal()` | concrete, `null` / `false` by default | Scorer for the scorers table, own goals excluded (`Goal`) |
| `fairPlayPointsFor(team)` | concrete, `0` by default | Fair play points for that team (`YellowCard`, `Expulsion`) |
| `registerCardIn(entries)` | concrete, does nothing by default | Registers the card in the fair play ranking (`YellowCard`, `Expulsion`) |
| `updateOnPitch(onPitch, substitutes)` | concrete, does nothing by default | Updates the players on the pitch (`Change`, `Expulsion`) |

`PenaltyExecuted` overrides `isShootoutKick()` and `isScoredShootoutKickBy(team)`
(and `getMinuteLabel()`, which shows "Kick n").
This lets `Match` count goals, expulsions and shoot-out kicks, and lets the statistics
services (`FairPlayCalculator`, `FairPlayRankingService`, `TopScorersService`,
`PlayerReportService`) and `PenaltyShootoutSimulator` work with any incidence **without
`instanceof`** and without knowing the concrete type of each event.
The fair play points of an expulsion depend on its `ExpulsionReason` (`getFairPlayPoints()`:
4 for a direct red card, 1 extra for a second yellow).

In the same way, `Player.isGoalkeeper()` / `asGoalkeeper()` (overridden by `Goalkeeper`) and
`Position.isGoalkeeper()` replace the `instanceof Goalkeeper` checks and the comparisons with
`Position.GOALKEEPER`.

### 3.5 Match simulation — Strategy pattern

`MatchSimulationStrategy<T extends Match>` defines how a match is simulated.
The implementation used is `GroupMatchSimulationService`, for group matches and for
knockout matches alike (so all incidences are generated in every stage):

1. A formation is chosen for each team and suspended players are left out.
2. `TeamStrengthCalculator` combines the starters' average, the team ranking and the head
   coach's titles.
3. Goals are distributed according to each team's strength (raised to a power of 3, so the
   stronger team wins more often), a small home advantage and a random "surprise" factor.
   A match averages about 2.7 goals.
4. Yellow cards, expulsions, substitutions, penalties and own goals are generated with
   fixed probabilities and limits (at most 3 substitutions per team).
5. Each player's minutes played are computed from the substitutions and expulsions.

### 3.6 Concurrency

- **Group stage:** the matches of each match day are simulated in parallel with an
  `ExecutorService` (pool of 8 threads). The simulator waits for the whole match day to
  finish before computing the suspensions for the next one.
- **Knockout stage:** each tie (two legs) is a `KnockoutTieTask` running in its own
  thread; the stage waits for all the ties before building the next round.
- Each task receives its own `Random` seed, so threads share no mutable state.

### 3.7 Standings and qualification

`StandingsService` sorts each zone by points, goal difference and goals scored.
If still tied, the fair-play score decides (yellow = 1, second yellow = +1,
straight red = 4) and, last, the team ranking.
The top two of each zone qualify. The quarter-finals cross zones:
A1–D2, B1–C2, C1–A2, D1–B2.

**Table before and after each match:** `MatchSimulationReport` stores two lists of
`StandingEntry` (`standingsBefore` and `standingsAfter`) for each group match.
`TournamentSession` keeps those reports (`getGroupStageReports()`) and the *Matches* page
shows them side by side in the match detail. Since `MatchSimulationReport` and
`StandingEntry` implement `Serializable`, the tables are saved in `torneo.save` and remain
available when the tournament is resumed.

### 3.8 Knockout tie-break

Two-leg ties are resolved in `SecondLegMatch`, which knows its first leg. They are decided,
in this order, by:

1. **Points** obtained in the two matches (3 per win, 1 per draw).
2. **Goal difference**, with away goals counting double.
3. **Penalty shoot-out.**

`getDecidingCriterion()` returns which of the three decided the tie, as a value of the
`TieCriterion` enum (`POINTS`, `GOAL_DIFFERENCE`, `PENALTY_SHOOTOUT`), each with its
interface label. `KnockoutTieTask` stores that label in each tie's `KnockoutTieReport`,
and the *Knockout Stage* page shows it next to the winner. The second-leg detail in
*Matches* also shows the aggregate (`describeAggregate()`, for example "Aggregate: 3 - 2")
and, if there was one, the penalty shoot-out result. `Match.describeAggregate()` returns an
empty string by default and only `SecondLegMatch` overrides it.

The final is a single match; if it ends in a draw it is decided by penalties.

### 3.9 Stadiums

- For the group stage, each team is assigned a home stadium at random.
- Each knockout match is played in a different stadium, so at least 13 are needed
  (4 quarter-finals × 2 + 2 semi-finals × 2 + 1 final).
- When the knockout stage ends, the stadiums used are removed from their city in memory
  only. The simulation never modifies the database.

### 3.10 Persistence

| Data | Where | Why |
|---|---|---|
| Cities and stadiums | PostgreSQL (`ciudades`, `estadios`) | Master data required by the assignment, editable from the interface |
| Tournament state | `torneo.save` (serialization of `TournamentSession`, done by `TournamentStorage`) | Lets the user close the application and resume where it was left |
| Teams and people | `docs/torneo.json` | Input file provided by the course |

All the domain classes saved in the session implement `Serializable`.
If an old `torneo.save` cannot be read (for example because a class changed), loading can
fail with `IOException`, `ClassNotFoundException` or `ClassCastException`; in every case the
application shows a warning and offers to start a new tournament instead of closing.

The database script is `docs/database.sql`. `estadios.idciudad` is a foreign key to
`ciudades` with `ON DELETE RESTRICT`.

### 3.11 Cities and stadiums management

- **Cities** (*Cities* page, `CitiesView`): create, update (name and country) and delete.
- **Stadiums** (*Stadiums* page, `StadiumsView`): create, update (including the ID, the
  city, the name and the capacity) and delete.
- Operations go through `CitiesController` / `StadiumsController` and `VenueCatalog`
  (`addCity`, `updateCity`, `deleteCity`, `addStadium`, `updateStadium`, `deleteStadium`),
  which builds the domain objects and delegates to `CityDao` and `StadiumDAO`.

**Rule: a city with stadiums cannot be deleted.** `CityDao.deleteCity()` works in a
transaction:

1. It locks the `estadios` table (`LOCK TABLE ... IN SHARE MODE`) so nobody assigns a
   stadium to that city while the check runs.
2. It verifies that the city exists.
3. If the city has stadiums, it cancels the operation and throws `CityInUseException`
   (a domain exception in `model.venue`) whose message lists them:
   *"The city Buenos Aires cannot be deleted because it has stadiums assigned: …"*.
4. If it has none, it deletes the city and commits the transaction.

The database foreign key is a second safeguard in case the check is bypassed.

`StadiumDAO` also validates, inside a transaction, that the city exists and that the ID is
not repeated; the input is validated by the `Stadium` constructor, and PostgreSQL
constraint errors are translated into clear messages.

### 3.12 Squad validation and surplus players

A valid squad has 18 players (2 goalkeepers, 6 defenders, 5 midfielders, 5 forwards).
If the JSON file has a surplus in some position, `JsonTournamentLoader` removes random
players from that position (`Team.removeRandomPlayer`) before validating the squad and
records a warning for each one. `TournamentSession.getWarnings()` exposes those warnings
and the Tournament page shows them in a warning panel.

### 3.13 ID card photos

The JSON file contains no photos. The exporter looks in the `photos/` folder for a file
named after the player's barcode value (`.jpg`, `.jpeg` or `.png`). If it is not found, it
draws the player's initials on a grey box, so the PDF always comes out complete.
This is a deliberate decision: the course data has no images.

---

## 4. Reports

| Report | Content |
|---|---|
| ID cards (PDF) | One card per player with photo, data and barcode |
| Top scorers | Players sorted by goals |
| Fair play | Teams sorted by disciplinary score |
| Minutes played | Players sorted by minutes |
| Bracket | Knockout ties and results |
| Teams | List of teams with head coach and stadium |
| Referees | Matches officiated and cards shown |
| Players by position | Players grouped by position |

---

## 5. Diagrams

The diagrams are in `docs/diagrams/` (PNG next to its PlantUML source):

| File | Shows |
|---|---|
| `01-packages` | Package dependencies |
| `02-domain` | People, teams, venues and the match hierarchy |
| `03-incidences` | The incidence hierarchy and `IncidenceContext` (polymorphism) |
| `04-simulation` | Competition services, Strategy pattern and the simulation threads |
| `05-ui-layers` | View, view model, controller and session, with the Knockout page as an example |

If the code changes, edit the `.puml` file and export it again (IntelliJ's *PlantUML
Integration* plugin, or `java -jar plantuml.jar -tpng docs/diagrams/*.puml`).

---

## 6. How to run

See `README.md` at the repository root.
