package model.event;

import model.match.Match;
import model.people.Goalkeeper;
import model.people.Player;

import java.util.Objects;

public class Goal extends Incidence {
    private final Player scorer;
    private final Goalkeeper goalkeeper;
    private final boolean penalty;
    private final boolean ownGoal;

    public Goal(int minute, Match match, Player scorer, boolean penalty, boolean ownGoal) {
        this(minute, match, scorer, null, penalty, ownGoal);
    }

    public Goal(int minute, Match match, Player scorer, Goalkeeper goalkeeper, boolean penalty, boolean ownGoal) {
        super(minute, match);
        this.scorer = Objects.requireNonNull(scorer, "The goal scorer is required");
        this.goalkeeper = goalkeeper;
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
    public String getDescription() {
        String goalType = penalty ? " (penalty)" : ownGoal ? " (own goal)" : "";
        String goalkeeperName = goalkeeper == null ? "not recorded" : goalkeeper.getName();
        return getMinute() + "' Goal" + goalType + ": " + scorer.getName()
                + " | goalkeeper: " + goalkeeperName;
    }
}
