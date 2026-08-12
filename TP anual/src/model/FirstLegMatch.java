package model;

import java.time.LocalDate;

public class FirstLegMatch extends Match {

    private PhaseType phase;

    public FirstLegMatch(LocalDate date, Team homeTeam, Team awayTeam, Referee referee,
                         Stadium stadium, PhaseType phase) {
        super(date, homeTeam, awayTeam, referee, stadium);
        this.phase = phase;
    }

    public PhaseType getPhase() {
        return phase;
    }
}