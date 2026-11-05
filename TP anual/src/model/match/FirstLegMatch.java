package model.match;

import model.venue.Stadium;
import model.people.Referee;
import model.team.Team;

import java.time.LocalDate;

public class FirstLegMatch extends Match {
    private static final long serialVersionUID = 1L;
    private final PhaseType phase;

    public FirstLegMatch(LocalDate date, Team homeTeam, Team awayTeam, Referee referee,
                         Stadium stadium, PhaseType phase) {
        super(date, homeTeam, awayTeam, referee, stadium);
        this.phase = phase;
    }

    public PhaseType getPhase() {
        return phase;
    }

    @Override
    public String getPhaseLabel() {
        return new StringBuilder(phase.getLabel()).append(" (1st leg)").toString();
    }

    @Override
    public String getPhaseCategory() {
        return phase.getLabel();
    }

    @Override
    public boolean allowsPenaltyShootout() {
        return false;
    }
}