package model;

import java.time.LocalDate;

public class HeadCoach extends Person{
    private Country country;
    private int titlesWon;
    private Team team;

    public HeadCoach(String name, LocalDate birthday, String type, String document, Country country, int titlesWon, Team team){
        super(name,birthday,type,document);
        this.country = country;
        this.team = team;
        this.titlesWon = titlesWon;
    }

    public Country getCountry() {
        return country;
    }

    public int getTitlesWon() {
        return titlesWon;
    }

    public Team getTeam() {
        return team;
    }
}
