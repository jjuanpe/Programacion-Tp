package model.match;

import java.io.Serializable;

import model.event.Expulsion;
import model.event.IncidenceContext;
import model.event.Incidence;
import model.event.Goal;
import model.event.PenaltyExecuted;
import model.people.Player;
import model.people.Referee;
import model.team.Team;
import model.venue.Stadium;

import java.time.LocalDate;
import java.util.*;

public abstract class Match implements Serializable, IncidenceContext {
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
    private static final long serialVersionUID = 1L;

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
        if (!referee.canOfficiate(homeTeam.getCountry(), awayTeam.getCountry())) {
            throw new IllegalArgumentException("The referee cannot officiate this match");
        }
        this.referee = referee;
    }

    // synchronized because the match is filled by a worker thread and read by the UI.
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
        int incidenceIndex = 0;
        boolean validIncidences = true;
        int recordedGoals = 0;
        while (incidenceIndex < incidences.size() && validIncidences) {
            Incidence incidence = incidences.get(incidenceIndex);
            if (incidence == null || incidence.getContext() != this) {
                validIncidences = false;
            } else if (incidence instanceof Goal) {
                recordedGoals++;
            }
            incidenceIndex++;
        }
        if (!validIncidences) {
            throw new IllegalArgumentException("Every incidence must belong to this match");
        }
        // Forfeit rule from the assignment: whoever gets 5 reds loses 0-3.
        Expulsion fifthRed = findFifthExpulsion(incidences);
        if (fifthRed != null) {
            boolean homeLost = homeTeam.getPlayers().contains(fifthRed.getPlayer());
            int expectedHomeGoals;
            int expectedAwayGoals;
            if (homeLost) {
                expectedHomeGoals = 0;
                expectedAwayGoals = 3;
            } else {
                expectedHomeGoals = 3;
                expectedAwayGoals = 0;
            }
            if (homeGoals != expectedHomeGoals || awayGoals != expectedAwayGoals) {
                throw new IllegalArgumentException("Five red cards require a 0-3 forfeit");
            }
        } else if (recordedGoals != homeGoals + awayGoals) {
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

    @Override
    public Expulsion findFifthExpulsion(List<Incidence> events) {
        List<Incidence> ordered = new ArrayList<>(events);
        ordered.sort(Comparator.comparingInt(Incidence::getMinute));
        Set<Player> homeDismissed = new HashSet<>();
        Set<Player> awayDismissed = new HashSet<>();
        Expulsion fifth = null;
        int index = 0;
        while (index < ordered.size() && fifth == null) {
            if (ordered.get(index) instanceof Expulsion expulsion) {
                Set<Player> dismissed;
                if (homeTeam.getPlayers().contains(expulsion.getPlayer())) {
                    dismissed = homeDismissed;
                } else {
                    dismissed = awayDismissed;
                }
                dismissed.add(expulsion.getPlayer());
                if (dismissed.size() == 5) {
                    fifth = expulsion;
                }
            }
            index++;
        }
        return fifth;
    }

    public synchronized void registerPenaltyShootout(List<PenaltyExecuted> kicks) {
        Objects.requireNonNull(kicks, "The penalty kicks are required");
        if (!played || !allowsPenaltyShootout()) {
            throw new IllegalStateException("Shoot-outs require a completed deciding match");
        }
        int incidenceIndex = 0;
        boolean shootoutRecorded = false;
        while (incidenceIndex < incidences.size() && !shootoutRecorded) {
            shootoutRecorded = incidences.get(incidenceIndex).isShootoutKick();
            incidenceIndex++;
        }
        if (shootoutRecorded) {
            throw new IllegalStateException("The shoot-out is already recorded");
        }
        int kickIndex = 0;
        boolean validKicks = true;
        while (kickIndex < kicks.size() && validKicks) {
            PenaltyExecuted kick = kicks.get(kickIndex);
            validKicks = kick != null && kick.getContext() == this;
            kickIndex++;
        }
        if (!validKicks) {
            throw new IllegalArgumentException("Every kick must belong to this match");
        }
        incidences.addAll(kicks);
    }

    public synchronized Set<Player> getExpelledPlayers() {
        Set<Player> expelledPlayers = new HashSet<>();
        for (Incidence incidence : incidences) {
            if (incidence instanceof Expulsion expulsion) {
                expelledPlayers.add(expulsion.getPlayer());
            }
        }
        return expelledPlayers;
    }

    public synchronized boolean isPlayed() {
        return played;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public Team getHomeTeam() {
        return homeTeam;
    }

    @Override
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

    @Override
    public synchronized List<Incidence> getIncidences() {
        return List.copyOf(incidences);
    }

    public synchronized boolean hasPenaltyShootout() {
        boolean found = false;
        int index = 0;
        while (index < incidences.size() && !found) {
            found = incidences.get(index).isShootoutKick();
            index++;
        }
        return found;
    }

    public synchronized int getShootoutGoals(Team team) {
        int goals = 0;
        for (Incidence incidence : incidences) {
            if (incidence.isScoredShootoutKickBy(team)) {
                goals++;
            }
        }
        return goals;
    }

    public String describeAggregate() {
        return "";
    }

    public abstract String getPhaseLabel();

    public abstract String getPhaseCategory();

    public abstract boolean allowsPenaltyShootout();
}
