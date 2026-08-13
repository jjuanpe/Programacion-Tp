package model;

public class Goal extends Incidence{
    private Player scorer;
    private boolean penalty;
    private boolean ownGoal;

    public Goal(int minute, Match match, Player scorer, boolean penalty, boolean ownGoal) {
        super(minute, match);
        this.scorer = scorer;
        this.penalty = penalty;
        this.ownGoal = ownGoal;
    }

    public Player getScorer() {
        return scorer;
    }

    public void setScorer(Player scorer) {
        this.scorer = scorer;
    }

    public boolean isPenalty() {
        return penalty;
    }

    public void setPenalty(boolean penalty) {
        this.penalty = penalty;
    }

    public boolean isOwnGoal() {
        return ownGoal;
    }

    public void setOwnGoal(boolean ownGoal) {
        this.ownGoal = ownGoal;
    }
}
