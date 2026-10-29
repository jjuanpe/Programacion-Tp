package model.people;

public enum Position {

    GOALKEEPER("arquero", "Goalkeeper", 2),
    DEFENDER("defensor", "Defender", 6),
    MIDFIELDER("mediocampista", "Midfielder", 5),
    FORWARD("delantero", "Forward", 5);

    private final String fileValue;
    private final String label;
    private final int requiredPerSquad;

    Position(String fileValue, String label, int requiredPerSquad) {
        this.fileValue = fileValue;
        this.label = label;
        this.requiredPerSquad = requiredPerSquad;
    }

    public String getLabel() {
        return label;
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
