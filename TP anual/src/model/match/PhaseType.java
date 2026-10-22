package model.match;

public enum PhaseType {
    QUARTER_FINAL("Quarter-final"),
    SEMI_FINAL("Semi-final"),
    FINAL("Final");

    private final String label;

    PhaseType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}