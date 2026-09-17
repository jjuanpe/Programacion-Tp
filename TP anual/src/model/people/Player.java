package model.people;

import java.time.LocalDate;

public class Player extends Person{
    private Position position;
    private int average;

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

    public String getRole(){
        return "";
    }
}
