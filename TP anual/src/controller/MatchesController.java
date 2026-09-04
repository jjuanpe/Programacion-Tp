package controller;

import model.match.Match;
import ui.fx.MatchDetail;
import ui.fx.MatchesViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Arma la pagina Matches: todos los partidos del campeonato, de cualquier fase,
 * con sus alineaciones y sus incidencias.
 *
 * La traduccion de cada partido a fila de interfaz la hace
 * {@link MatchDetailFactory}, compartida con la pagina Knockout Stage. Los
 * partidos van ordenados por fecha, que es el orden en que se juegan.
 */
public class MatchesController {

    private static final String NO_MATCHES_MESSAGE =
            "No matches yet. Import the data and run the group draw from the Tournament page.";

    private final TournamentSession session;
    private final MatchesViewModel viewModel;

    public MatchesController(TournamentSession session, MatchesViewModel viewModel) {
        this.session = Objects.requireNonNull(session, "The tournament session is required");
        this.viewModel = Objects.requireNonNull(viewModel, "The view model is required");
    }

    /** Vuelve a leer los partidos del campeonato. */
    public void refresh() {
        List<Match> matches = new ArrayList<>(session.getMatches());
        matches.sort(Comparator.comparing(Match::getDate));

        List<MatchDetail> details = new ArrayList<>();
        for (Match match : matches) {
            details.add(MatchDetailFactory.toDetail(match));
        }
        viewModel.setMatches(details);
        viewModel.setStatus(details.isEmpty() ? NO_MATCHES_MESSAGE : "");
    }
}
