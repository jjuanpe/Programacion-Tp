package ui.fx;

/**
 * Un jugador del plantel, listo para mostrar en la tabla de detalle.
 */
public final class PlayerRow {

    private final String name;
    private final String position;
    private final int age;
    private final int rating;

    public PlayerRow(String name, String position, int age, int rating) {
        this.name = name;
        this.position = position;
        this.age = age;
        this.rating = rating;
    }

    public String getName() { return name; }

    public String getPosition() { return position; }

    public int getAge() { return age; }

    public int getRating() { return rating; }
}
