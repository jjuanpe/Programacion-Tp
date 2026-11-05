package ui.fx;

/** Clase de estilo segun el puesto: oro, plata y bronce para el podio. */
final class Ranks {

    private Ranks() {
    }

    static String styleFor(int position) {
        return switch (position) {
            case 1 -> "rank-gold";
            case 2 -> "rank-silver";
            case 3 -> "rank-bronze";
            default -> "rank-other";
        };
    }
}
