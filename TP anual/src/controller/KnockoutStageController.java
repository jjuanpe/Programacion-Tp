package controller;

import model.match.FinalMatch;
import model.match.FirstLegMatch;
import model.match.Match;
import model.match.SecondLegMatch;
import ui.fx.KnockoutStageViewModel;
import ui.fx.MatchDetail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Arma la pagina Knockout Stage: solo los partidos de la fase eliminatoria
 * (cuartos, semis y final), con sus alineaciones e incidencias.
 *
 * Es el mismo partido que muestra la pagina Matches, pero filtrado a las
 * llaves eliminatorias; el filtro por fase (cuartos/semis/final) lo aplica
 * la vista sobre esta lista.
 */
public class KnockoutStageController {

    private static final String NO_MATCHES_MESSAGE =
            "No knockout matches yet. Play the group stage from the Tournament page first.";

    private final TournamentSession session;
    private final KnockoutStageViewModel viewModel;

    public KnockoutStageController(TournamentSession session, KnockoutStageViewModel viewModel) {
        this.session = Objects.requireNonNull(session, "The tournament session is required");
        this.viewModel = Objects.requireNonNull(viewModel, "The view model is required");
    }

    /** Vuelve a leer los partidos de la fase eliminatoria. */
    public void refresh() {
        List<Match> matches = new ArrayList<>();
        for (Match match : session.getMatches()) {
            if (isKnockoutMatch(match)) {
                matches.add(match);
            }
        }
        matches.sort(Comparator.comparing(Match::getDate));

        List<MatchDetail> details = new ArrayList<>();
        for (Match match : matches) {
            details.add(MatchDetailFactory.toDetail(match));
        }
        viewModel.setMatches(details);
        viewModel.setStatus(details.isEmpty() ? NO_MATCHES_MESSAGE : "");
    }

    private boolean isKnockoutMatch(Match match) {
        return match instanceof FirstLegMatch
                || match instanceof SecondLegMatch
                || match instanceof FinalMatch;
    }
}
