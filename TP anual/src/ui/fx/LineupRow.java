package ui.fx;

/**
 * Un jugador en la alineacion de un partido, listo para mostrar.
 */
public final class LineupRow {

    private final String player;
    private final String position;
    private final String role;
    private final String minutes;

    public LineupRow(String player, String position, String role, String minutes) {
        this.player = player;
        this.position = position;
        this.role = role;
        this.minutes = minutes;
    }

    public String getPlayer() { return player; }

    public String getPosition() { return position; }

    /** Titular o suplente. */
    public String getRole() { return role; }

    /** Minutos jugados, o un guion si no entro. */
    public String getMinutes() { return minutes; }
}
