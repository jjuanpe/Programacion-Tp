package model.competition;

import model.match.GroupMatch;
import model.team.Team;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FixtureService {

    private static final int ROUNDS = 3;

    public List<GroupMatch> generateFixture(Zone zone, LocalDate firstDate, int daysBetweenRounds) {
        List<Team> teams = zone.getTeams();
        if (teams.size() != 4) {
            throw new IllegalArgumentException("La zona debe tener 4 equipos, tiene: " + teams.size());
        }

        Team fixedTeam = teams.get(0);
        List<Team> rotatingTeams = new ArrayList<>(teams.subList(1, teams.size()));

        List<GroupMatch> fixture = new ArrayList<>();
        LocalDate date = firstDate;

        for (int round = 0; round < ROUNDS; round++) {
            Team homeTeam1 = fixedTeam;
            Team awayTeam1 = rotatingTeams.get(2);
            Team homeTeam2 = rotatingTeams.get(0);
            Team awayTeam2 = rotatingTeams.get(1);


            if (round % 2 == 1) {
                Team temporaryTeam = homeTeam1;
                homeTeam1 = awayTeam1;
                awayTeam1 = temporaryTeam;
            }

            fixture.add(new GroupMatch(date, homeTeam1, awayTeam1, null, null));
            fixture.add(new GroupMatch(date, homeTeam2, awayTeam2, null, null));

            Collections.rotate(rotatingTeams, 1);
            date = date.plusDays(daysBetweenRounds);
        }

        zone.addGroupMatches(fixture);
        return fixture;
    }
}
