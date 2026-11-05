package viewmodel;

public final class CityRow {

    private final long id;
    private final String name;
    private final String country;

    public CityRow(long id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }

    public long getId() { return id; }

    public String getName() { return name; }

    public String getCountry() { return country; }
}
