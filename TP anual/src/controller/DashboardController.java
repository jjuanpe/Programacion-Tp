package controller;

import model.match.Match;
import viewmodel.DashboardViewModel;
import viewmodel.TournamentStage;

import java.util.List;
import java.util.Objects;

public class DashboardController {

    private final TournamentSession session;
    private final DashboardViewModel viewModel;

    public DashboardController(TournamentSession session, DashboardViewModel viewModel) {
        this.session = Objects.requireNonNull(session, "The tournament session is required");
        this.viewModel = Objects.requireNonNull(viewModel, "The view model is required");
    }

    public void refresh() {
        List<Match> matches = session.getMatches();

        viewModel.setTeams(session.getTeams().size());
        viewModel.setMatchesPlayed(session.countPlayedMatches());
        viewModel.setGoals(countGoals(matches));

        TournamentState state = session.getState();
        TournamentStage stage = stageOf(state);
        viewModel.setCurrentStage(stage);
        viewModel.setStageLabel(stage == null ? state.getLabel() : stage.getLabel());
        viewModel.setNextStep(nextStepOf(state));
        if (session.getKnockoutResult() == null) {
            viewModel.setChampion("");
        } else {
            viewModel.setChampion(session.getKnockoutResult().getChampion().getName());
        }
    }

    private String nextStepOf(TournamentState state) {
        return switch (state) {
            case EMPTY -> "Import the tournament data";
            case DATA_LOADED -> "Run the group draw";
            case GROUPS_DRAWN -> "Play the group stage";
            case GROUP_STAGE_PLAYED -> "Play the quarter-finals";
            case QUARTER_FINALS_PLAYED -> "Play the semi-finals";
            case SEMI_FINALS_PLAYED -> "Play the final";
            case FINISHED -> "The tournament is over";
        };
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
