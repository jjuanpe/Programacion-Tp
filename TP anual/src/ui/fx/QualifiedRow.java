package ui.fx;

public final class QualifiedRow {

    private final String zone;
    private final String first;
    private final String second;

    public QualifiedRow(String zone, String first, String second) {
        this.zone = zone;
        this.first = first;
        this.second = second;
    }

    public String getZone() { return zone; }

    public String getFirst() { return first; }

    public String getSecond() { return second; }
}