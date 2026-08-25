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
        boolean equals;
        if (this == object) {
            equals = true;
        } else if (object == null || getClass() != object.getClass()) {
            equals = false;
        } else {
            Country country = (Country) object;
            equals = countryName.equalsIgnoreCase(country.countryName);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        String normalizedName = countryName.toLowerCase(Locale.ROOT);
        return Objects.hash(normalizedName);
    }
}
