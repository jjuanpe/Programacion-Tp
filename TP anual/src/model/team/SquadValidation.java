package model.team;

import model.people.Player;
import model.people.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SquadValidation {

    private static final int REQUIRED_SQUAD_SIZE = 18;

    private final List<String> problems;

    private SquadValidation(List<String> problems) {
        this.problems = List.copyOf(problems);
    }

    public static SquadValidation of(Team team) {
        Objects.requireNonNull(team, "The team is required");
        List<Player> players = team.getPlayers();
        List<String> problems = new ArrayList<>();

        if (players.size() != REQUIRED_SQUAD_SIZE) {
            problems.add(new StringBuilder().append(players.size()).append(" players instead of ")
                    .append(REQUIRED_SQUAD_SIZE).toString());
        }
        for (Position position : Position.values()) {
            int expected = position.getRequiredPerSquad();
            int actual = team.countByPosition(position);
            if (actual != expected) {
                problems.add(new StringBuilder().append(actual).append(" of ").append(expected).append(' ')
                        .append(position.name().toLowerCase()).append('s').toString());
            }
        }
        return new SquadValidation(problems);
    }

    public boolean isValid() {
        return problems.isEmpty();
    }

    public List<String> getProblems() {
        return problems;
    }
}
