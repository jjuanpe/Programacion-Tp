package model;

import java.time.LocalDate;

public class GroupMatch extends Match {

    public GroupMatch(LocalDate date, Team homeTeam, Team awayTeam, Referee referee, Stadium stadium) {
        super(date, homeTeam, awayTeam, referee, stadium);
    }

}