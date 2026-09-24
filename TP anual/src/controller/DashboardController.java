package controller;

import model.match.Match;
import ui.fx.DashboardViewModel;
import ui.fx.TournamentStage;

import java.util.List;
import java.util.Objects;

/**
 * Completa el resumen del Dashboard a partir del estado real del campeonato.
 *
 * El Dashboard muestra solo el panorama general: cuantos equipos hay cargados,
 * cuantos partidos se jugaron, cuantos goles se convirtieron y en que fase esta
 * el torneo. El detalle vive en las paginas especificas.
 */
public class DashboardController {

    private final TournamentSession session;
    private final DashboardViewModel viewModel;

    public DashboardController(TournamentSession session, DashboardViewModel viewModel) {
        this.session = Objects.requireNonNull(session, "The tournament session is required");
        this.viewModel = Objects.requireNonNull(viewModel, "The view model is required");
    }

    /** Vuelve a leer el campeonato y actualiza el resumen. */
    public void refresh() {
        List<Match> matches = session.getMatches();

        viewModel.setTeams(session.getTeams().size());
        viewModel.setMatchesPlayed(countPlayedMatches(matches));
        viewModel.setGoals(countGoals(matches));

        TournamentState state = session.getState();
        TournamentStage stage = stageOf(state);
        viewModel.setCurrentStage(stage);
        viewModel.setStageLabel(stage == null ? state.getLabel() : stage.getLabel());
    }

    private int countPlayedMatches(List<Match> matches) {
        int played = 0;
        for (Match match : matches) {
            if (match.isPlayed()) {
                played++;
            }
        }
        return played;
    }

    private int countGoals(List<Match> matches) {
        int goals = 0;
        for (Match match : matches) {
            if (match.isPlayed()) {
                goals += match.getHomeGoals() + match.getAwayGoals();
            }
        }
        return goals;
    }

    /**
     * Fase que corresponde marcar en el stepper, o {@code null} cuando el
     * torneo todavia no arranco. La traduccion al enum de la interfaz se hace
     * aca, asi el dominio no depende de {@code ui.fx}.
     */
    private TournamentStage stageOf(TournamentState state) {
        return switch (state) {
            case EMPTY, DATA_LOADED -> null;
            case GROUPS_DRAWN -> TournamentStage.GROUP_STAGE;
            case GROUP_STAGE_PLAYED -> TournamentStage.QUARTERFINALS;
            case QUARTER_FINALS_PLAYED -> TournamentStage.SEMIFINALS;
            case SEMI_FINALS_PLAYED, FINISHED -> TournamentStage.FINAL;
        };
    }
}
