package ui.fx;

/**
 * Un jugador del campeonato, listo para mostrar en el listado de Players.
 *
 * Los goles recibidos y su promedio por partido solo tienen sentido para los
 * arqueros; en el resto llegan como "-" ya formateados por el controlador.
 */
public final class PlayerStatsRow {

    private final String name;
    private final String position;
    private final String team;
    private final int age;
    private final int rating;
    private final int matchesPlayed;
    private final int minutesPlayed;
    private final int goals;
    private final String goalsConceded;
    private final String goalsConcededAverage;

    public PlayerStatsRow(
            String name,
            String position,
            String team,
            int age,
            int rating,
            int matchesPlayed,
            int minutesPlayed,
            int goals,
            String goalsConceded,
            String goalsConcededAverage) {
        this.name = name;
        this.position = position;
        this.team = team;
        this.age = age;
        this.rating = rating;
        this.matchesPlayed = matchesPlayed;
        this.minutesPlayed = minutesPlayed;
        this.goals = goals;
        this.goalsConceded = goalsConceded;
        this.goalsConcededAverage = goalsConcededAverage;
    }

    public String getName() { return name; }

    public String getPosition() { return position; }

    public String getTeam() { return team; }

    public int getAge() { return age; }

    public int getRating() { return rating; }

    public int getMatchesPlayed() { return matchesPlayed; }

    public int getMinutesPlayed() { return minutesPlayed; }

    public int getGoals() { return goals; }

    public String getGoalsConceded() { return goalsConceded; }

    public String getGoalsConcededAverage() { return goalsConcededAverage; }
}
