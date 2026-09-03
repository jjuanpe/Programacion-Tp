package model.competition;

import model.team.Country;
import model.team.Team;

import java.util.Objects;

/**
 * Una fila del listado alfabetico de equipos: promedio de edad del plantel,
 * datos del DT y rendimiento en el campeonato.
 *
 * Los mutadores son de paquete: solo {@link TeamReportService} arma estas
 * entradas.
 */
public class TeamReportEntry {

    private static final int POINTS_PER_WIN = 3;
    private static final int POINTS_PER_DRAW = 1;

    private final Team team;
    private final double averagePlayerAge;
    private final int coachAge;
    private final Country coachCountry;

    private int played;
    private int points;
    private int goalsFor;
    private int goalsAgainst;

    TeamReportEntry(Team team, double averagePlayerAge, int coachAge, Country coachCountry) {
        this.team = Objects.requireNonNull(team, "The team is required");
        this.averagePlayerAge = averagePlayerAge;
        this.coachAge = coachAge;
        this.coachCountry = coachCountry;
    }

    /** Suma un partido jugado, con los goles a favor y en contra de este equipo. */
    void registerMatch(int scored, int conceded) {
        played++;
        goalsFor += scored;
        goalsAgainst += conceded;
        if (scored > conceded) {
            points += POINTS_PER_WIN;
        } else if (scored == conceded) {
            points += POINTS_PER_DRAW;
        }
    }

    public Team getTeam() { return team; }

    /** Promedio de edad de los jugadores del plantel. */
    public double getAveragePlayerAge() { return averagePlayerAge; }

    /** Edad del DT, o 0 si el equipo no tiene DT cargado. */
    public int getCoachAge() { return coachAge; }

    /** Nacionalidad del DT, o {@code null} si no tiene DT cargado. */
    public Country getCoachCountry() { return coachCountry; }

    public int getPlayed() { return played; }

    public int getPoints() { return points; }

    /** Puntos que el equipo podria haber sumado en los partidos que jugo. */
    public int getPossiblePoints() { return played * POINTS_PER_WIN; }

    public int getGoalsFor() { return goalsFor; }

    public int getGoalsAgainst() { return goalsAgainst; }

    /** Porcentaje de puntos obtenidos sobre los posibles. 0 si no jugo. */
    public double getEffectiveness() {
        double effectiveness = 0;
        if (played > 0) {
            effectiveness = points * 100.0 / getPossiblePoints();
        }
        return effectiveness;
    }
}
