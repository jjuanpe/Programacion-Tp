package model.venue;

public class Stadium {

    private Long id;
    private String name;
    private City city;
    private int capacity;

    public Stadium(Long id, String name, City city, int capacity) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("The stadium id must be positive");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The stadium name is required");
        }
        if (city == null) {
            throw new IllegalArgumentException("The stadium city is required");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("The stadium capacity must be positive");
        }
        this.id = id;
        this.name = name.trim();
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
