package model.event;

import model.people.Goalkeeper;
import model.people.Player;

import java.util.Objects;

public class Goal extends Incidence {
    private static final long serialVersionUID = 1L;
    private final Player scorer;
    private final Goalkeeper goalkeeper;
    private final boolean penalty;
    private final boolean ownGoal;

    public Goal(int minute, IncidenceContext match, Player scorer, Goalkeeper goalkeeper, boolean penalty, boolean ownGoal) {
        super(minute, match);
        this.scorer = Objects.requireNonNull(scorer, "The goal scorer is required");
        this.goalkeeper = Objects.requireNonNull(goalkeeper, "The goalkeeper is required");
        this.penalty = penalty;
        this.ownGoal = ownGoal;
    }

    public Player getScorer() {
        return scorer;
    }

    public Goalkeeper getGoalkeeper() {
        return goalkeeper;
    }

    public boolean isPenalty() {
        return penalty;
    }

    public boolean isOwnGoal() {
        return ownGoal;
    }

    @Override
    public int goalsRecorded() {
        return 1;
    }

    @Override
    public Player getCreditedScorer() {
        return ownGoal ? null : scorer;
    }

    @Override
    public boolean isPenaltyGoal() {
        return penalty;
    }

    @Override
    public String getTypeLabel() {
        String label = "Goal";
        if (ownGoal) {
            label = "Own goal";
        } else if (penalty) {
            label = "Goal (penalty)";
        }
        return label;
    }

    @Override
    public String getDetail() {
        StringBuilder detail = new StringBuilder(describeWithTeam(scorer));
        if (goalkeeper != null) {
            detail.append(" (goalkeeper: ").append(goalkeeper.getName()).append(')');
        }
        return detail.toString();
    }

    @Override
    public String getDescription() {
        String goalType = penalty ? " (penalty)" : ownGoal ? " (own goal)" : "";
        String goalkeeperName = goalkeeper == null ? "not recorded" : goalkeeper.getName();
        return new StringBuilder().append(getMinute()).append("' Goal").append(goalType).append(": ")
                .append(scorer.getName()).append(" | goalkeeper: ").append(goalkeeperName).toString();
    }
}
