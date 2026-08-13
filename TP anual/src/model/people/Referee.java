package model.people;

import model.team.Country;
import model.team.Team;

import java.time.LocalDate;

public class Referee extends Person{
    private Country country;
    private int years;

    public Referee(String name, LocalDate birthday,DocumentType documentType,String document,Country country,int years){
        super(name,birthday, documentType,document);
        this.country = country;
        this.years = years;
    }

    public int getYears() {
        return years;
    }

    public Country getCountry() {
        return country;
    }

    public boolean canOfficiate(Team home, Team away){
        return false;
    }

    public String getRole(){
        return "";
    }
}
