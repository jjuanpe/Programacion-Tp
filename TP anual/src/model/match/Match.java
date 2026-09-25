package model.match;

import java.io.Serializable;
import model.event.Incidence;
import model.event.Goal;
import model.people.Referee;
import model.team.Team;
import model.venue.Stadium;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Match implements Serializable {
    private final LocalDate date;
    private final Team homeTeam;
    private final Team awayTeam;
    private Referee referee;
    private final Stadium stadium;
    private int homeGoals;
    private int awayGoals;
    private Formation homeFormation;
    private Formation awayFormation;
    private final List<PlayerParticipation> participations;
    private final List<Incidence> incidences;
    private boolean played;

    public Match(LocalDate date, Team homeTeam, Team awayTeam, Referee referee, Stadium stadium) {
        this.date = Objects.requireNonNull(date, "The match date is required");
        this.homeTeam = Objects.requireNonNull(homeTeam, "The home team is required");
        this.awayTeam = Objects.requireNonNull(awayTeam, "The away team is required");
        if (homeTeam == awayTeam) {
            throw new IllegalArgumentException("A team cannot play against itself");
        }
        this.stadium = stadium;
        this.participations = new ArrayList<>();
        this.incidences = new ArrayList<>();
        this.played = false;
        if (referee != null) {
            assignReferee(referee);
        }
    }

    public synchronized void assignReferee(Referee referee) {
        Objects.requireNonNull(referee, "The referee is required");
        if (played) {
            throw new IllegalStateException("A referee cannot be assigned after the match was played");
        }
        if (this.referee != null) {
            throw new IllegalStateException("The match already has an assigned referee");
        }
        if (!referee.canOfficiate(homeTeam, awayTeam)) {
            throw new IllegalArgumentException("The referee cannot officiate this match");
        }
        this.referee = referee;
    }

    public synchronized void completeMatch(
            int homeGoals,
            int awayGoals,
            Formation homeFormation,
            Formation awayFormation,
            List<PlayerParticipation> participations,
            List<Incidence> incidences) {
        if (played) {
            throw new IllegalStateException("The match was already completed");
        }
        if (referee == null) {
            throw new IllegalStateException("The match must have a referee before it can be completed");
        }
        if (homeGoals < 0 || awayGoals < 0) {
            throw new IllegalArgumentException("Goals cannot be negative");
        }
        Objects.requireNonNull(homeFormation, "The home formation is required");
        Objects.requireNonNull(awayFormation, "The away formation is required");
        Objects.requireNonNull(participations, "The player participations are required");
        Objects.requireNonNull(incidences, "The incidences are required");
        for (Incidence incidence : incidences) {
            if (incidence == null || incidence.getMatch() != this) {
                throw new IllegalArgumentException("Every incidence must belong to this match");
            }
        }
        long recordedGoals = incidences.stream()
                .filter(Goal.class::isInstance)
                .count();
        if (recordedGoals != homeGoals + awayGoals) {
            throw new IllegalArgumentException("The score must match the recorded goal incidences");
        }

        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
        this.homeFormation = homeFormation;
        this.awayFormation = awayFormation;
        this.participations.clear();
        this.participations.addAll(participations);
        this.incidences.clear();
        this.incidences.addAll(incidences);
        this.played = true;
    }

    public synchronized boolean isPlayed() {
        return played;
    }

    public LocalDate getDate() {
        return date;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public synchronized Referee getReferee() {
        return referee;
    }

    public Stadium getStadium() {
        return stadium;
    }

    public synchronized int getHomeGoals() {
        return homeGoals;
    }

    public synchronized int getAwayGoals() {
        return awayGoals;
    }

    public synchronized Formation getHomeFormation() {
        return homeFormation;
    }

    public synchronized Formation getAwayFormation() {
        return awayFormation;
    }

    public synchronized List<PlayerParticipation> getParticipations() {
        return List.copyOf(participations);
    }

    public synchronized List<Incidence> getIncidences() {
        return List.copyOf(incidences);
    }
}
