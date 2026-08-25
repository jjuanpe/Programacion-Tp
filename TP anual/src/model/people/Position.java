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
        Position selectedPosition = null;
        int positionIndex = 0;
        Position[] positions = values();
        while (positionIndex < positions.length && selectedPosition == null) {
            Position position = positions[positionIndex];
            if (position.fileValue.equalsIgnoreCase(fileValue)) {
                selectedPosition = position;
            }
            positionIndex++;
        }
        if (selectedPosition == null) {
            throw new IllegalArgumentException("Unknown position value: " + fileValue);
        }
        return selectedPosition;
    }
}
