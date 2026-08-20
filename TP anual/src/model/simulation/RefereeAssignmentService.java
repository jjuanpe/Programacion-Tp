package model.simulation;

import model.people.Referee;
import model.team.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RefereeAssignmentService {

    public Referee selectReferee(
            Team homeTeam,
            Team awayTeam,
            List<Referee> availableReferees,
            Random random) {
        Objects.requireNonNull(homeTeam, "The home team is required");
        Objects.requireNonNull(awayTeam, "The away team is required");
        Objects.requireNonNull(availableReferees, "The referee list is required");
        Objects.requireNonNull(random, "The random generator is required");

        List<Referee> eligibleReferees = new ArrayList<>();
        for (Referee referee : availableReferees) {
            if (referee.canOfficiate(homeTeam, awayTeam)) {
                eligibleReferees.add(referee);
            }
        }
        if (eligibleReferees.isEmpty()) {
            throw new IllegalStateException(
                    "There is no eligible referee for " + homeTeam.getName() + " vs " + awayTeam.getName());
        }
        return eligibleReferees.get(random.nextInt(eligibleReferees.size()));
    }
}
