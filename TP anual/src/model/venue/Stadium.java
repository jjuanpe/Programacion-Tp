package model.venue;

public class Stadium {

    private Long id;
    private String name;
    private City city;
    private int capacity;

    public Stadium(Long id, String name, City city, int capacity) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public City getCity() {
        return city;
    }

    public int getCapacity() {
        return capacity;
    }
    
}