package model.event;

import java.io.Serializable;

import model.competition.FairPlayEntry;
import model.people.Player;
import model.team.Team;

import java.util.List;
import java.util.Map;
import java.util.Objects;

// Every event is a subclass, so the UI can list them without asking what type each one is.
public abstract class Incidence implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int minute;
    private final IncidenceContext match;

    public Incidence(int minute, IncidenceContext match) {
        if (minute <= 0) {
            throw new IllegalArgumentException("The incidence minute must be positive");
        }
        this.minute = minute;
        this.match = Objects.requireNonNull(match, "The incidence match is required");
    }

    public int getMinute() {
        return minute;
    }

    public IncidenceContext getContext() {
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

    // Goals this incidence adds to the score. Only goals count.
    public int goalsRecorded() {
        return 0;
    }

    // The expulsion this incidence represents, or null if it is not an expulsion.
    public Expulsion getExpulsion() {
        return null;
    }

    // The player sent off by this incidence, or null if nobody was sent off.
    public Player getExpelledPlayer() {
        return null;
    }

    // The player credited with a goal for the scorers table, or null (own goals do not count).
    public Player getCreditedScorer() {
        return null;
    }

    public boolean isPenaltyGoal() {
        return false;
    }

    // Fair play points this incidence adds to the given team. Only cards add points.
    public int fairPlayPointsFor(Team team) {
        return 0;
    }

    // Registers the card on the entry of the cautioned player. Incidences without cards do nothing.
    public void registerCardIn(Map<Player, FairPlayEntry> entries) {
    }

    // Updates the players still on the pitch. Only changes and expulsions modify it.
    public void updateOnPitch(List<Player> onPitch, List<Player> substitutes) {
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
