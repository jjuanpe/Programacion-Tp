package model.simulation;

import model.match.Formation;
import model.people.Player;
import model.people.Position;
import model.team.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FormationService {
    private static final int STARTER_COUNT = 11;
    private static final Map<Position, Integer> STARTERS_BY_POSITION = Map.of(
            Position.GOALKEEPER, 1,
            Position.DEFENDER, 4,
            Position.MIDFIELDER, 4,
            Position.FORWARD, 2
    );

    public Formation createFormation(Team team, Set<Player> unavailablePlayers) {
        Objects.requireNonNull(team, "The team is required");
        Set<Player> unavailable = unavailablePlayers == null ? Set.of() : Set.copyOf(unavailablePlayers);

        List<Player> availablePlayers = new ArrayList<>();
        for (Player player : team.getPlayers()) {
            if (!unavailable.contains(player)) {
                availablePlayers.add(player);
            }
        }
        availablePlayers.sort(Comparator.comparingInt(Player::getAverage).reversed());

        List<Player> starters = new ArrayList<>();
        for (Position position : Position.values()) {
            int required = STARTERS_BY_POSITION.get(position);
            int selected = 0;
            for (Player player : availablePlayers) {
                if (player.getPosition() == position && selected < required) {
                    starters.add(player);
                    selected++;
                }
            }
            if (position == Position.GOALKEEPER && selected != required) {
                throw new IllegalStateException(
                        team.getName() + " does not have enough available players for position " + position);
            }
        }

        for (Player player : availablePlayers) {
            if (starters.size() == STARTER_COUNT) {
                break;
            }
            if (player.getPosition() != Position.GOALKEEPER && !starters.contains(player)) {
                starters.add(player);
            }
        }
        if (starters.size() != STARTER_COUNT) {
            throw new IllegalStateException(
                    team.getName() + " does not have eleven available players for the formation");
        }

        List<Player> substitutes = new ArrayList<>(availablePlayers);
        substitutes.removeAll(starters);
        return new Formation(starters, substitutes);
    }
}
