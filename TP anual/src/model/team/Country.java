package model.team;

public class Country {
    private String countryName;

    public Country (String countryName){
        this.countryName = countryName;
    }

    public static Country of(String countryName) {
        return new Country(countryName);
    }

    public String getCountryName() {
        return countryName;
    }
}
