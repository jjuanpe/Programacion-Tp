package model.competition;

import model.people.Player;
import model.team.Team;

import java.util.Objects;

/**
 * Una fila del ranking de goleadores: el jugador, su equipo y los goles que
 * convirtio en el campeonato.
 *
 * Los mutadores son de paquete: solo {@link TopScorersService} arma estas
 * entradas, asi ningun otro codigo puede inventar estadisticas.
 */
public class ScorerEntry {

    private final Player player;
    private final Team team;
    private int position;
    private int goals;
    private int penaltyGoals;

    ScorerEntry(Player player, Team team) {
        this.player = Objects.requireNonNull(player, "The player is required");
        this.team = team;
    }

    /** Suma un gol convertido en un partido. */
    void registerGoal(boolean penalty) {
        goals++;
        if (penalty) {
            penaltyGoals++;
        }
    }

    void setPosition(int position) {
        if (position <= 0) {
            throw new IllegalArgumentException("The ranking position must be positive");
        }
        this.position = position;
    }

    public Player getPlayer() { return player; }

    /** Equipo del jugador, o {@code null} si no pertenece a ninguno de los recibidos. */
    public Team getTeam() { return team; }

    public int getPosition() { return position; }

    public int getGoals() { return goals; }

    /** Goles convertidos de penal durante el partido (sin contar la tanda). */
    public int getPenaltyGoals() { return penaltyGoals; }
}
