package model.team;

import model.people.Player;
import model.people.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Resultado de revisar el plantel de un equipo.
 *
 * {@link Team#validateSquad()} corta con una excepcion en el primer problema,
 * que sirve para la carga pero no para mostrar en pantalla. Esta clase revisa
 * todo y devuelve la lista completa de faltantes o sobrantes, sin lanzar nada.
 *
 * Las cantidades esperadas salen de {@link Position#getRequiredPerSquad()}:
 * 2 arqueros, 6 defensores, 5 mediocampistas y 5 delanteros.
 */
public final class SquadValidation {

    private static final int REQUIRED_SQUAD_SIZE = 18;

    private final int squadSize;
    private final List<String> problems;

    private SquadValidation(int squadSize, List<String> problems) {
        this.squadSize = squadSize;
        this.problems = List.copyOf(problems);
    }

    public static SquadValidation of(Team team) {
        Objects.requireNonNull(team, "The team is required");
        List<Player> players = team.getPlayers();
        List<String> problems = new ArrayList<>();

        if (players.size() != REQUIRED_SQUAD_SIZE) {
            problems.add(players.size() + " players instead of " + REQUIRED_SQUAD_SIZE);
        }
        for (Position position : Position.values()) {
            int expected = position.getRequiredPerSquad();
            int actual = team.countByPosition(position);
            if (actual != expected) {
                problems.add(actual + " of " + expected + " " + position.name().toLowerCase() + "s");
            }
        }
        return new SquadValidation(players.size(), problems);
    }

    public boolean isValid() {
        return problems.isEmpty();
    }

    public int getSquadSize() {
        return squadSize;
    }

    /** Los problemas encontrados; vacia si el plantel esta completo. */
    public List<String> getProblems() {
        return problems;
    }
}
