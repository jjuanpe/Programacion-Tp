package model.people;

public enum Position {

    GOALKEEPER("arquero", 2),
    DEFENDER("defensor", 6),
    MIDFIELDER("mediocampista", 5),
    FORWARD("delantero", 5);

    private final String fileValue;
    private final int requiredPerSquad;

    Position(String fileValue, int requiredPerSquad) {
        this.fileValue = fileValue;
        this.requiredPerSquad = requiredPerSquad;
    }

    public int getRequiredPerSquad() {
        return requiredPerSquad;
    }

    public static Position fromFileValue(String fileValue) {
        for (Position position : values()) {
            if (position.fileValue.equalsIgnoreCase(fileValue)) {
                return position;
            }
        }
        throw new IllegalArgumentException("Unknown position value: " + fileValue);
    }
}