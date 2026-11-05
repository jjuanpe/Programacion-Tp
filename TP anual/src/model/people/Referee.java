package model.people;

import model.common.Country;

import java.time.LocalDate;

public class Referee extends Person {
    private final Country country;
    private final int years;
    private static final long serialVersionUID = 1L;

    public Referee(
            String name,
            LocalDate birthday,
            DocumentType documentType,
            String document,
            Country country,
            int years) {
        super(name, birthday, documentType, document);
        if (country == null) {
            throw new IllegalArgumentException("The referee's country is required");
        }
        if (years < 0) {
            throw new IllegalArgumentException("Refereeing years cannot be negative");
        }
        this.country = country;
        this.years = years;
    }

    public int getYears() {
        return years;
    }

    public Country getCountry() {
        return country;
    }

    public boolean canOfficiate(Country homeCountry, Country awayCountry) {
        boolean hasRequiredData = homeCountry != null && awayCountry != null && country != null;
        boolean canOfficiate = false;
        if (hasRequiredData) {
            // A referee can't work a game of his own country, unless both teams are from the same country.
            boolean sameCountryTeams = homeCountry.equals(awayCountry);
            canOfficiate = sameCountryTeams || (!country.equals(homeCountry) && !country.equals(awayCountry));
        }
        return canOfficiate;
    }

    @Override
    public String getRole() {
        return "Referee";
    }
}
