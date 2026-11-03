package model.competition;

import model.people.Referee;

import java.util.Objects;

public class RefereeReportEntry {

    private final Referee referee;
    private int position;
    private int matchesOfficiated;

    RefereeReportEntry(Referee referee) {
        this.referee = Objects.requireNonNull(referee, "The referee is required");
    }

    void registerMatch() {
        matchesOfficiated++;
    }

    void setPosition(int position) {
        if (position <= 0) {
            throw new IllegalArgumentException("The ranking position must be positive");
        }
        this.position = position;
    }

    public Referee getReferee() { return referee; }

    public int getPosition() { return position; }

    public int getMatchesOfficiated() { return matchesOfficiated; }
}