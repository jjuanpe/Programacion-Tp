package model.event;

import java.io.Serializable;
import model.match.Match;

import model.people.Player;
import model.team.Team;

import java.util.Objects;

// Every event is a subclass, so the UI can list them without asking what type each one is.
public abstract class Incidence implements Serializable {
    private static final long serialVersionUID = 1L;
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

    public abstract String getTypeLabel();

    public abstract String getDetail();

    public String getMinuteLabel() {
        return new StringBuilder().append(minute).append('\'').toString();
    }

    public boolean isShootoutKick() {
        return false;
    }

    public boolean isScoredShootoutKickBy(Team team) {
        return false;
    }

    protected String describeWithTeam(Player player) {
        String teamName = "";
        if (match.getHomeTeam().getPlayers().contains(player)) {
            teamName = match.getHomeTeam().getName();
        } else if (match.getAwayTeam().getPlayers().contains(player)) {
            teamName = match.getAwayTeam().getName();
        }
        StringBuilder text = new StringBuilder();
        if (!teamName.isEmpty()) {
            text.append(teamName).append(" - ");
        }
        return text.append(player.getName()).toString();
    }

    @Override
    public final String toString() {
        return getDescription();
    }
}
