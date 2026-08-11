package model;

import java.time.LocalDate;
import java.util.List;

public class Match {
    private LocalDate date;
    private Team homeTeam;
    private Team awayTeam;
    private Referee referee;
    private Stadium stadium;
    private List<Incidence> incidents;

    public LocalDate getDate() {
        return date;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public Referee getReferee() {
        return referee;
    }

    public Stadium getStadium() {
        return stadium;
    }

    public List<Incidence> getIncidents() {
        return incidents;
    }
}
