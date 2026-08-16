package model.people;

import java.time.LocalDate;

public class Player extends Person{
    private Position position;
    private int average;

    public Player (String name, LocalDate birthday, DocumentType documentType, String document,Position position,int average){
        super(name,birthday,documentType,document);
        this.position = position;
        this.average = average;
    }

    protected static int computeAverage(int... values) {
        int sum = 0;
        for (int value : values) {
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
