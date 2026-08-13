package model;

import java.time.LocalDate;

public class Referee extends Person{
    private Country country;
    private int years;

    public Referee(String name, LocalDate birthday,String type,String document,Country country,int years){
        super(name,birthday,type,document);
        this.country = country;
        this.years = years;
    }

    public int getYears() {
        return years;
    }

    public Country getCountry() {
        return country;
    }
}
