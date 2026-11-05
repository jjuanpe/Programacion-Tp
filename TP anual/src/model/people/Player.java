package model.people;

import java.time.LocalDate;

public class Player extends Person{
    private final Position position;
    private final int average;
    private static final long serialVersionUID = 1L;

    public Player (String name, LocalDate birthday, DocumentType documentType, String document,Position position,int average){
        super(name,birthday,documentType,document);
        if (position == null) {
            throw new IllegalArgumentException("The player's position is required");
        }
        if (average < 0 || average > 100) {
            throw new IllegalArgumentException("The player's average must be between 0 and 100");
        }
        this.position = position;
        this.average = average;
    }

    protected static int computeAverage(int... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Player attributes are required");
        }
        int sum = 0;
        for (int value : values) {
            if (value < 0 || value > 100) {
                throw new IllegalArgumentException("Player attributes must be between 0 and 100");
            }
            sum += value;
        }
        return sum / values.length;
    }

    public Position getPosition() {
        return position;
    }

    public int getAverage() {
        return average;
    }

    public boolean isGoalkeeper() {
        return false;
    }

    // The player as a goalkeeper, or null if they are a field player.
    public Goalkeeper asGoalkeeper() {
        return null;
    }

    @Override
    public String getRole() {
        return "Player";
    }
}
