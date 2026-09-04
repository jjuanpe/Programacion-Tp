package model.competition;

import model.people.Player;
import model.team.Team;

import java.util.Objects;

/**
 * Una fila del listado de jugadores: sus estadisticas en el campeonato.
 *
 * Los mutadores son de paquete: solo {@link PlayerReportService} arma estas
 * entradas.
 */
public class PlayerReportEntry {

    private final Player player;
    private final Team team;
    private int matchesPlayed;
    private int minutesPlayed;
    private int goals;
    private int goalsConceded;

    PlayerReportEntry(Player player, Team team) {
        this.player = Objects.requireNonNull(player, "The player is required");
        this.team = team;
    }

    /** Suma un partido en el que el jugador estuvo en cancha. */
    void registerMatch(int minutesPlayedInMatch) {
        matchesPlayed++;
        minutesPlayed += minutesPlayedInMatch;
    }

    /** Suma un gol convertido (no cuentan los goles en contra). */
    void registerGoal() {
        goals++;
    }

    /** Suma los goles recibidos por su equipo en un partido en el que jugo. */
    void registerConceded(int goalsConcededInMatch) {
        goalsConceded += goalsConcededInMatch;
    }

    public Player getPlayer() { return player; }

    public Team getTeam() { return team; }

    public int getMatchesPlayed() { return matchesPlayed; }

    public int getMinutesPlayed() { return minutesPlayed; }

    public int getGoals() { return goals; }

    /** Goles en contra recibidos. Solo tiene sentido para arqueros. */
    public int getGoalsConceded() { return goalsConceded; }

    /** Promedio de goles recibidos por partido. 0 si todavia no jugo ninguno. */
    public double getGoalsConcededAverage() {
        return matchesPlayed == 0 ? 0 : (double) goalsConceded / matchesPlayed;
    }
}
