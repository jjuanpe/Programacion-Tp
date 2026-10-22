package model.match;

import model.venue.Stadium;
import model.people.Referee;
import model.team.Team;

import java.time.LocalDate;

public class GroupMatch extends Match {
    private static final long serialVersionUID = 1L;

    public GroupMatch(LocalDate date, Team homeTeam, Team awayTeam, Referee referee, Stadium stadium) {
        super(date, homeTeam, awayTeam, referee, stadium);
    }

    @Override
    public String getPhaseLabel() {
        return "Group Stage";
    }

    @Override
    public String getPhaseCategory() {
        return "Group Stage";
    }

    @Override
    public boolean allowsPenaltyShootout() {
        return false;
    }

}