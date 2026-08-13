package model.event;

import model.match.Match;

public abstract class Incidence {
    private int minute;
    private Match match;

    public Incidence(int minute, Match match) {
        this.minute = minute;
        this.match = match;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}
