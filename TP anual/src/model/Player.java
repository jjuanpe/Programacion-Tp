package model;

import java.time.LocalDate;

public class Player extends Person{
    private Position position;
    private int average;

    public Player (String name, LocalDate birthday, String type, String document,Position position,int average){
        super(name,birthday,type,document);
        this.position = position;
        this.average = average;
    }

    public Position getPosition() {
        return position;
    }

    public int getAverage() {
        return average;
    }
}
