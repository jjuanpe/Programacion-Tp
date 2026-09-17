package model.people;

import model.team.Country;
import model.team.Team;

import java.time.LocalDate;

public class Referee extends Person {
    private final Country country;
    private final int years;

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

    public boolean canOfficiate(Team home, Team away) {
        boolean hasRequiredData = home != null
                && away != null
                && country != null
                && home.getCountry() != null
                && away.getCountry() != null;
        boolean canOfficiate = false;
        if (hasRequiredData) {
            canOfficiate = !country.equals(home.getCountry())
                    && !country.equals(away.getCountry());
        }
        return canOfficiate;
    }

    @Override
    public String getRole() {
        return "";
    }
}
