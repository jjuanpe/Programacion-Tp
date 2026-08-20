package model.team;

import java.util.Locale;
import java.util.Objects;

public final class Country {
    private final String countryName;

    public Country (String countryName){
        if (countryName == null || countryName.isBlank()) {
            throw new IllegalArgumentException("The country name is required");
        }
        this.countryName = countryName.trim();
    }

    public static Country of(String countryName) {
        return new Country(countryName);
    }

    public String getCountryName() {
        return countryName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Country country = (Country) object;
        return countryName.equalsIgnoreCase(country.countryName);
    }

    @Override
    public int hashCode() {
        String normalizedName = countryName.toLowerCase(Locale.ROOT);
        return Objects.hash(normalizedName);
    }
}
