package model.simulation;

import model.match.Formation;
import model.people.HeadCoach;
import model.people.Player;
import model.team.Team;

import java.util.Objects;

public class TeamStrengthCalculator {
    private static final int TOURNAMENT_TEAM_COUNT = 16;
    private static final double PLAYER_AVERAGE_WEIGHT = 0.70;
    private static final double RANKING_WEIGHT = 1.50;
    private static final double COACH_TITLE_WEIGHT = 2.00;

    public double calculate(Team team, Formation formation) {
        Objects.requireNonNull(team, "The team is required");
        Objects.requireNonNull(formation, "The formation is required");

        int totalPlayerAverage = 0;
        for (Player player : formation.getStarters()) {
            totalPlayerAverage += player.getAverage();
        }
        double playerAverage = totalPlayerAverage / (double) formation.getStarters().size();

        int rankingValue = Math.max(0, TOURNAMENT_TEAM_COUNT - team.getRanking() + 1);
        HeadCoach headCoach = team.getHeadCoach();
        int titlesWon = headCoach == null ? 0 : Math.max(0, headCoach.getTitlesWon());

        return playerAverage * PLAYER_AVERAGE_WEIGHT
                + rankingValue * RANKING_WEIGHT
                + titlesWon * COACH_TITLE_WEIGHT;
    }
}
