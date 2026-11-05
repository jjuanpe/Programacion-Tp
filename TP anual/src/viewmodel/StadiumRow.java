package viewmodel;

public final class StadiumRow {

    private final long id;
    private final String name;
    private final long cityId;
    private final String cityName;
    private final int capacity;

    public StadiumRow(long id, String name, long cityId, String cityName, int capacity) {
        this.id = id;
        this.name = name;
        this.cityId = cityId;
        this.cityName = cityName;
        this.capacity = capacity;
    }

    public long getId() { return id; }

    public String getName() { return name; }

    public long getCityId() { return cityId; }

    public String getCityName() { return cityName; }

    public int getCapacity() { return capacity; }
}
