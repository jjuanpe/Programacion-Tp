package model.people;

import model.team.Country;
import model.team.Team;

import java.time.LocalDate;

public class HeadCoach extends Person{
    private Country country;
    private int titlesWon;
    private Team team;

    public HeadCoach(String name, LocalDate birthday, DocumentType documentType, String document, Country country, int titlesWon, Team team){
        super(name,birthday,documentType,document);
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

    public String getRole(){
        return "";
    }
}
