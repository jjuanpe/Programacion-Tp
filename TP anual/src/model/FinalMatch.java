package model;

import java.time.LocalDate;

public class FinalMatch extends Match {

    public FinalMatch(LocalDate date, Team homeTeam, Team awayTeam, Referee referee, Stadium stadium) {
        super(date, homeTeam, awayTeam, referee, stadium);
    }

}