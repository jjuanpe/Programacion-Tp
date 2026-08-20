package model.event;

import model.match.Match;

import java.util.Objects;

public abstract class Incidence {
    private final int minute;
    private final Match match;

    public Incidence(int minute, Match match) {
        if (minute <= 0) {
            throw new IllegalArgumentException("The incidence minute must be positive");
        }
        this.minute = minute;
        this.match = Objects.requireNonNull(match, "The incidence match is required");
    }

    public int getMinute() {
        return minute;
    }

    public Match getMatch() {
        return match;
    }

    public abstract String getDescription();

    @Override
    public final String toString() {
        return getDescription();
    }
}
