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
