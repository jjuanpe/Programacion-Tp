package ui.fx;

/**
 * Una incidencia de un partido, lista para mostrar.
 */
public final class IncidenceRow {

    private final String minute;
    private final String type;
    private final String detail;

    public IncidenceRow(String minute, String type, String detail) {
        this.minute = minute;
        this.type = type;
        this.detail = detail;
    }

    public String getMinute() { return minute; }

    /** Gol, tarjeta amarilla, expulsion, cambio... */
    public String getType() { return type; }

    public String getDetail() { return detail; }
}
