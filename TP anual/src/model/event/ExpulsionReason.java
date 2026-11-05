package model.event;

public enum ExpulsionReason {
    DIRECT_RED_CARD("direct red card", 4),
    // The first yellow card already added its own point, so the expulsion only adds one more.
    SECOND_YELLOW_CARD("second yellow card", 1);

    private final String description;
    private final int fairPlayPoints;

    ExpulsionReason(String description, int fairPlayPoints) {
        this.description = description;
        this.fairPlayPoints = fairPlayPoints;
    }

    public int getFairPlayPoints() {
        return fairPlayPoints;
    }

    public String getDescription() {
        return description;
    }
}
