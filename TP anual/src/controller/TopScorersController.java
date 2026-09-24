package controller;

import javafx.application.Platform;
import model.competition.ScorerEntry;
import model.competition.TopScorersService;
import model.team.Team;
import ui.fx.ScorerRow;
import ui.fx.TopScorersViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TopScorersController {

    private static final String UNKNOWN_TEAM = "-";
    private static final String NO_GOALS_MESSAGE = "No goals recorded yet";

    private final TopScorersViewModel viewModel;
    private final TopScorersService topScorersService;

    public TopScorersController(TopScorersViewModel viewModel) {
        this(viewModel, new TopScorersService());
    }

    public TopScorersController(TopScorersViewModel viewModel, TopScorersService topScorersService) {
        this.viewModel = Objects.requireNonNull(viewModel, "The view model is required");
        this.topScorersService = Objects.requireNonNull(topScorersService, "The service is required");
    }

    public void show(TournamentSession session) {
        Objects.requireNonNull(session, "The tournament session is required");
        publish(computeRanking(session));
    }

    public void showFailure(Throwable failure) {
        reportFailure(failure);
    }

    private List<ScorerRow> computeRanking(TournamentSession session) {
        List<ScorerEntry> ranking =
                topScorersService.computeTopScorers(session.getTeams(), session.getMatches());
        return toRows(ranking);
    }

    private List<ScorerRow> toRows(List<ScorerEntry> ranking) {
        List<ScorerRow> rows = new ArrayList<>();
        for (ScorerEntry entry : ranking) {
            rows.add(new ScorerRow(
                    entry.getPosition(),
                    entry.getPlayer().getName(),
                    teamNameOf(entry),
                    entry.getGoals(),
                    entry.getPenaltyGoals()));
        }
        return rows;
    }

    private String teamNameOf(ScorerEntry entry) {
        Team team = entry.getTeam();
        return team == null ? UNKNOWN_TEAM : team.getName();
    }

    private void publish(List<ScorerRow> rows) {
        runOnFxThread(() -> {
            viewModel.setStatus(NO_GOALS_MESSAGE);
            viewModel.setRows(rows);
        });
    }

    private void reportFailure(Throwable failure) {
        String reason = failure == null ? "unknown error" : String.valueOf(failure.getMessage());
        runOnFxThread(() -> {
            viewModel.setRows(List.of());
            viewModel.setStatus("The ranking could not be loaded: " + reason);
        });
    }

    private void runOnFxThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
}
