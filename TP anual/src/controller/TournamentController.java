package controller;

import javafx.concurrent.Task;
import model.match.Match;
import ui.fx.TournamentViewModel;

import java.util.List;
import java.util.Objects;

public class TournamentController {

    private final TournamentSession session;
    private final TournamentViewModel viewModel;

    private Runnable onStateChanged = () -> { };

    public TournamentController(TournamentSession session, TournamentViewModel viewModel) {
        this.session = Objects.requireNonNull(session, "The tournament session is required");
        this.viewModel = Objects.requireNonNull(viewModel, "The view model is required");
    }

    public void setOnStateChanged(Runnable onStateChanged) {
        this.onStateChanged = Objects.requireNonNull(onStateChanged, "The callback is required");
    }

    public void importData(String dataPath) {
        runStep("Importing data...", () -> session.importData(dataPath), "Data imported");
    }

    public void drawGroups() {
        runStep("Running the group draw...", session::drawGroups, "Group draw completed");
    }

    public void advance() {
        ActionStep step = stepFor(session.getState());
        if (step != null) {
            runStep(step.progressMessage(), step.action(), step.successMessage());
        }
    }

    private record ActionStep(String progressMessage, Step action, String successMessage) {
    }

    private ActionStep stepFor(TournamentState state) {
        return switch (state) {
            case GROUPS_DRAWN ->
                    new ActionStep("Playing the group stage...", session::playGroupStage, "Group stage played");
            case GROUP_STAGE_PLAYED ->
                    new ActionStep("Playing the quarterfinals...", session::playQuarterFinals, "Quarterfinals played");
            case QUARTER_FINALS_PLAYED ->
                    new ActionStep("Playing the semifinals...", session::playSemiFinals, "Semifinals played");
            case SEMI_FINALS_PLAYED ->
                    new ActionStep("Playing the final...", session::playFinal, "Final played");
            case EMPTY, DATA_LOADED, FINISHED -> null;
        };
    }

    public void refresh() {
        TournamentState state = session.getState();
        List<Match> matches = session.getMatches();

        viewModel.setState(state.getLabel());
        viewModel.setTeams(session.getTeams().size());
        viewModel.setReferees(session.getReferees().size());
        viewModel.setZones(session.getZones().size());
        viewModel.setTotalMatches(matches.size());
        viewModel.setMatchesPlayed(countPlayedMatches(matches));
        viewModel.setWarnings(session.getWarnings().size());
        viewModel.setAdvanceLabel(advanceLabelFor(state));

        updateAvailableActions(state);
    }

    private void updateAvailableActions(TournamentState state) {
        boolean idle = !viewModel.isBusy();
        viewModel.setCanImport(idle);
        viewModel.setCanDraw(idle && state == TournamentState.DATA_LOADED);
        viewModel.setCanAdvance(idle
                && (state == TournamentState.GROUPS_DRAWN
                || state == TournamentState.GROUP_STAGE_PLAYED
                || state == TournamentState.QUARTER_FINALS_PLAYED
                || state == TournamentState.SEMI_FINALS_PLAYED));
        viewModel.setCanSave(false);
    }

    private String advanceLabelFor(TournamentState state) {
        return switch (state) {
            case EMPTY, DATA_LOADED -> "Start tournament";
            case GROUPS_DRAWN -> "Start group stage";
            case GROUP_STAGE_PLAYED -> "Continue: quarterfinals";
            case QUARTER_FINALS_PLAYED -> "Continue: semifinals";
            case SEMI_FINALS_PLAYED -> "Continue: final";
            case FINISHED -> "Tournament finished";
        };
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

    private interface Step {
        void run() throws Exception;
    }

    private void runStep(String progressMessage, Step step, String successMessage) {
        viewModel.setBusy(true);
        viewModel.setMessage(progressMessage);
        refresh();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                step.run();
                return null;
            }
        };
        task.setOnSucceeded(event -> finishStep(successMessage, null));
        task.setOnFailed(event -> finishStep(null, task.getException()));

        Thread worker = new Thread(task, "tournament-action");
        worker.setDaemon(true);
        worker.start();
    }

    private void finishStep(String successMessage, Throwable failure) {
        viewModel.setBusy(false);
        refresh();
        viewModel.setMessage(failure == null ? successMessage : describe(failure));
        onStateChanged.run();
    }

    private String describe(Throwable failure) {
        String reason = failure.getMessage();
        if (reason == null || reason.isBlank()) {
            reason = failure.getClass().getSimpleName();
        }
        return "Failed: " + reason;
    }
}
