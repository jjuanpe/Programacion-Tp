package model.match;

public enum TieCriterion {
    POINTS("Points in both legs"),
    GOAL_DIFFERENCE("Goal difference (away goals count double)"),
    PENALTY_SHOOTOUT("Penalty shoot-out");

    private final String label;

    TieCriterion(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
