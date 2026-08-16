package model.people;

import model.team.Country;

import java.time.LocalDate;

public class HeadCoach extends Person{
    private Country country;
    private int titlesWon;

    public HeadCoach(String name, LocalDate birthday, DocumentType documentType, String document, Country country, int titlesWon){
        super(name,birthday,documentType,document);
        this.country = country;
        this.titlesWon = titlesWon;
    }

    public Country getCountry() {
        return country;
    }

    public int getTitlesWon() {
        return titlesWon;
    }

    public String getRole(){
        return "";
    }
}
