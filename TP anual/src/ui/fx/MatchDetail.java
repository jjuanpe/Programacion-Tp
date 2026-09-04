package ui.fx;

import java.util.List;

/**
 * Un partido del campeonato, listo para mostrar.
 *
 * Lleva tanto el resumen que se ve en la tabla (fase, fecha, equipos,
 * resultado) como el detalle que aparece al seleccionarlo (alineaciones e
 * incidencias). Al elegir otro partido la vista solo muestra otro objeto de
 * estos: no vuelve a consultar el dominio.
 */
public final class MatchDetail {

    private final String phase;
    private final String phaseCategory;
    private final String date;
    private final String homeTeam;
    private final String awayTeam;
    private final String score;
    private final String status;
    private final boolean played;
    private final String referee;
    private final String stadium;
    private final String aggregate;
    private final List<LineupRow> homeLineup;
    private final List<LineupRow> awayLineup;
    private final List<IncidenceRow> incidences;

    public MatchDetail(
            String phase,
            String phaseCategory,
            String date,
            String homeTeam,
            String awayTeam,
            String score,
            String status,
            boolean played,
            String referee,
            String stadium,
            String aggregate,
            List<LineupRow> homeLineup,
            List<LineupRow> awayLineup,
            List<IncidenceRow> incidences) {
        this.phase = phase;
        this.phaseCategory = phaseCategory;
        this.date = date;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.score = score;
        this.status = status;
        this.played = played;
        this.referee = referee;
        this.stadium = stadium;
        this.aggregate = aggregate;
        this.homeLineup = List.copyOf(homeLineup);
        this.awayLineup = List.copyOf(awayLineup);
        this.incidences = List.copyOf(incidences);
    }

    public String getPhase() { return phase; }

    /** Fase sin distinguir ida/vuelta, para filtrar (ej. "Semi-final"). */
    public String getPhaseCategory() { return phaseCategory; }

    public String getDate() { return date; }

    public String getHomeTeam() { return homeTeam; }

    public String getAwayTeam() { return awayTeam; }

    public String getScore() { return score; }

    public String getStatus() { return status; }

    public boolean isPlayed() { return played; }

    public String getReferee() { return referee; }

    public String getStadium() { return stadium; }

    /** Global de la llave, solo en los partidos de vuelta. Vacio en el resto. */
    public String getAggregate() { return aggregate; }

    public List<LineupRow> getHomeLineup() { return homeLineup; }

    public List<LineupRow> getAwayLineup() { return awayLineup; }

    public List<IncidenceRow> getIncidences() { return incidences; }

    /** Titulo del partido, para el encabezado del detalle. */
    public String getTitle() {
        return homeTeam + "  " + score + "  " + awayTeam;
    }
}
