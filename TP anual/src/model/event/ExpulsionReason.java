package model.event;

public enum ExpulsionReason {
    DIRECT_RED_CARD("direct red card"),
    SECOND_YELLOW_CARD("second yellow card");

    private final String description;

    ExpulsionReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
