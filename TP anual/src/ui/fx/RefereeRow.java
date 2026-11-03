package ui.fx;

public final class RefereeRow {

    private final int position;
    private final String referee;
    private final String country;
    private final int matchesOfficiated;
    private final int years;

    public RefereeRow(int position, String referee, String country, int matchesOfficiated, int years) {
        this.position = position;
        this.referee = referee;
        this.country = country;
        this.matchesOfficiated = matchesOfficiated;
        this.years = years;
    }

    public int getPosition() { return position; }

    public String getReferee() { return referee; }

    public String getCountry() { return country; }

    public int getMatchesOfficiated() { return matchesOfficiated; }

    public int getYears() { return years; }
}
