package model.match;

import java.io.Serializable;
import model.people.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Formation implements Serializable {
    private static final int STARTER_COUNT = 11;

    private final List<Player> starters;
    private final List<Player> substitutes;

    public Formation(List<Player> starters, List<Player> substitutes) {
        Objects.requireNonNull(starters, "The starter list is required");
        Objects.requireNonNull(substitutes, "The substitute list is required");
        if (starters.size() != STARTER_COUNT) {
            throw new IllegalArgumentException("A formation must contain exactly 11 starters");
        }

        Set<Player> allPlayers = new HashSet<>(starters);
        allPlayers.addAll(substitutes);
        if (allPlayers.size() != starters.size() + substitutes.size()) {
            throw new IllegalArgumentException("A player cannot appear twice in a formation");
        }

        this.starters = List.copyOf(starters);
        this.substitutes = List.copyOf(substitutes);
    }

    public List<Player> getStarters() {
        return starters;
    }

    public List<Player> getSubstitutes() {
        return substitutes;
    }
}
