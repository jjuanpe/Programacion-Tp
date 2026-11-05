package ui.fx;

import viewmodel.IncidenceRow;
import viewmodel.LineupRow;
import viewmodel.MatchDetail;
import viewmodel.StandingRow;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Predicate;

public class MatchesView extends VBox {

    private static final String ALL_PHASES = "All phases";
    private static final List<String> PHASE_FILTERS =
            List.of(ALL_PHASES, "Group Stage", "Quarter-final", "Semi-final", "Final");

    private final FilteredList<MatchDetail> filteredMatches;

    private final Label detailTitle = new Label();
    private final Label detailSubtitle = new Label();
    private final Label homeTeamName = new Label();
    private final Label awayTeamName = new Label();
    private final Label scoreboardHome = new Label();
    private final Label scoreboardScore = new Label();
    private final Label scoreboardAway = new Label();
    private final TableView<LineupRow> homeLineup = new TableView<>();
    private final TableView<LineupRow> awayLineup = new TableView<>();
    private final TableView<IncidenceRow> incidences = new TableView<>();
    private final TableView<StandingRow> standingsBefore = new TableView<>();
    private final TableView<StandingRow> standingsAfter = new TableView<>();
    private final VBox standingsPanel = new VBox();

    public MatchesView(ObservableList<MatchDetail> matches, ObservableValue<String> emptyMessage) {
        getStyleClass().add("page");
        this.filteredMatches = new FilteredList<>(matches, ignored -> true);

        Label title = Icons.titled(new Label("Matches"), Icon.BALL);
        title.getStyleClass().add("section-title");

        getChildren().addAll(
                title,
                buildHint(),
                buildFilterRow(),
                buildSubtitle("All matches"),
                buildMatchesTable(emptyMessage),
                buildSubtitle("Match detail"),
                buildDetailPanel());
    }

    private Label buildHint() {
        Label hint = new Label(
                "Matches are played by phase from the Tournament page. This page is read-only.");
        hint.getStyleClass().add("action-hint");
        return hint;
    }

    private HBox buildFilterRow() {
        Label label = new Label("Phase:");
        label.getStyleClass().add("action-hint");

        ComboBox<String> filter = new ComboBox<>(FXCollections.observableArrayList(PHASE_FILTERS));
        filter.getStyleClass().add("filter-combo");
        filter.getSelectionModel().select(ALL_PHASES);
        filter.valueProperty().addListener((observable, previous, selected) ->
                filteredMatches.setPredicate(matchOfPhase(selected)));

        HBox row = new HBox(label, filter);
        row.getStyleClass().add("action-row");
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Predicate<MatchDetail> matchOfPhase(String selected) {
        return match -> ALL_PHASES.equals(selected) || match.getPhaseCategory().equals(selected);
    }

    private Label buildSubtitle(String text) {
        Label subtitle = new Label(text);
        subtitle.getStyleClass().add("subsection-title");
        return subtitle;
    }

    private TableView<MatchDetail> buildMatchesTable(ObservableValue<String> emptyMessage) {
        TableView<MatchDetail> table = new TableView<>(filteredMatches);
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(300);

        Label placeholder = new Label();
        placeholder.getStyleClass().add("table-placeholder");
        placeholder.textProperty().bind(emptyMessage);
        table.setPlaceholder(placeholder);

        table.getColumns().add(TableColumns.text("Phase", MatchDetail::getPhase));
        table.getColumns().add(TableColumns.text("Date", MatchDetail::getDate));
        table.getColumns().add(TableColumns.text("Home", MatchDetail::getHomeTeam));
        table.getColumns().add(TableColumns.numeric("Score", MatchDetail::getScore));
        table.getColumns().add(TableColumns.text("Away", MatchDetail::getAwayTeam));
        table.getColumns().add(TableColumns.text("Status", MatchDetail::getStatus));
        table.getColumns().add(TableColumns.text("Referee", MatchDetail::getReferee));

        table.getSelectionModel().selectedItemProperty()
                .addListener((observable, previous, selected) -> showDetail(selected));
        filteredMatches.addListener((ListChangeListener<MatchDetail>) change -> selectFirst(table));
        selectFirst(table);
        return table;
    }

    private void selectFirst(TableView<MatchDetail> table) {
        if (table.getItems().isEmpty()) {
            showDetail(null);
        } else if (!table.getItems().contains(table.getSelectionModel().getSelectedItem())) {
            table.getSelectionModel().selectFirst();
        }
    }

    private VBox buildDetailPanel() {
        detailTitle.getStyleClass().add("detail-title");
        detailSubtitle.getStyleClass().add("detail-subtitle");
        homeTeamName.getStyleClass().add("subsection-title");
        awayTeamName.getStyleClass().add("subsection-title");

        VBox home = new VBox(homeTeamName, buildLineupTable(homeLineup));
        VBox away = new VBox(awayTeamName, buildLineupTable(awayLineup));
        home.getStyleClass().add("lineup");
        away.getStyleClass().add("lineup");
        HBox.setHgrow(home, Priority.ALWAYS);
        HBox.setHgrow(away, Priority.ALWAYS);

        HBox lineups = new HBox(home, away);
        lineups.getStyleClass().add("split");

        VBox panel = new VBox(
                detailTitle,
                buildScoreboard(),
                detailSubtitle,
                buildSubtitle("Line-ups"),
                lineups,
                buildSubtitle("Incidences"),
                buildIncidencesTable(),
                buildStandingsPanel());
        panel.getStyleClass().addAll("panel", "match-detail");
        return panel;
    }

    private HBox buildScoreboard() {
        scoreboardHome.getStyleClass().add("scoreboard-team");
        scoreboardAway.getStyleClass().add("scoreboard-team");
        scoreboardScore.getStyleClass().add("scoreboard-score");
        scoreboardHome.setAlignment(Pos.CENTER_RIGHT);
        scoreboardAway.setAlignment(Pos.CENTER_LEFT);
        scoreboardHome.setMaxWidth(Double.MAX_VALUE);
        scoreboardAway.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(scoreboardHome, Priority.ALWAYS);
        HBox.setHgrow(scoreboardAway, Priority.ALWAYS);

        HBox board = new HBox(scoreboardHome, scoreboardScore, scoreboardAway);
        board.getStyleClass().add("scoreboard");
        board.setAlignment(Pos.CENTER);
        return board;
    }

    private VBox buildStandingsPanel() {
        VBox before = new VBox(buildSubtitle("Standings before the match"),
                buildStandingsTable(standingsBefore));
        VBox after = new VBox(buildSubtitle("Standings after the match"),
                buildStandingsTable(standingsAfter));
        HBox.setHgrow(before, Priority.ALWAYS);
        HBox.setHgrow(after, Priority.ALWAYS);

        HBox tables = new HBox(before, after);
        tables.getStyleClass().add("split");
        standingsPanel.getChildren().setAll(tables);
        standingsPanel.setManaged(false);
        standingsPanel.setVisible(false);
        return standingsPanel;
    }

    private TableView<StandingRow> buildStandingsTable(TableView<StandingRow> table) {
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(190);
        table.setPlaceholder(buildPlaceholder("No standings"));

        table.getColumns().add(TableColumns.numeric("#", row -> String.valueOf(row.getPosition())));
        table.getColumns().add(TableColumns.text("Team", StandingRow::getTeam));
        table.getColumns().add(TableColumns.numeric("Pts", row -> String.valueOf(row.getPoints())));
        table.getColumns().add(TableColumns.numeric("PJ", row -> String.valueOf(row.getPlayed())));
        table.getColumns().add(TableColumns.numeric("PG", row -> String.valueOf(row.getWon())));
        table.getColumns().add(TableColumns.numeric("PE", row -> String.valueOf(row.getDrawn())));
        table.getColumns().add(TableColumns.numeric("PP", row -> String.valueOf(row.getLost())));
        table.getColumns().add(TableColumns.numeric("GF", row -> String.valueOf(row.getGoalsFor())));
        table.getColumns().add(TableColumns.numeric("GC", row -> String.valueOf(row.getGoalsAgainst())));
        table.getColumns().add(TableColumns.numeric("Dif", row -> String.valueOf(row.getGoalDifference())));
        return table;
    }

    private TableView<LineupRow> buildLineupTable(TableView<LineupRow> table) {
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(300);
        table.setPlaceholder(buildPlaceholder("Not played yet"));

        table.getColumns().add(TableColumns.text("Player", LineupRow::getPlayer));
        table.getColumns().add(TableColumns.text("Position", LineupRow::getPosition));
        table.getColumns().add(TableColumns.text("Role", LineupRow::getRole));
        table.getColumns().add(TableColumns.numeric("Min.", LineupRow::getMinutes));
        return table;
    }

    private TableView<IncidenceRow> buildIncidencesTable() {
        incidences.getStyleClass().add("data-table");
        incidences.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        incidences.setPrefHeight(260);
        incidences.setPlaceholder(buildPlaceholder("No incidences recorded"));

        incidences.setRowFactory(ignored -> buildIncidenceRow());
        incidences.getColumns().add(TableColumns.numeric("Minute", IncidenceRow::getMinute));
        incidences.getColumns().add(TableColumns.text("Type", IncidenceRow::getType));
        incidences.getColumns().add(TableColumns.text("Detail", IncidenceRow::getDetail));
        return incidences;
    }

    // Colors the row by event type so goals and cards stand out in the list.
    private TableRow<IncidenceRow> buildIncidenceRow() {
        return new TableRow<>() {
            @Override
            protected void updateItem(IncidenceRow item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("row-goal", "row-yellow", "row-red");
                if (!empty && item != null) {
                    String type = item.getType().toLowerCase();
                    if (type.contains("goal")) {
                        getStyleClass().add("row-goal");
                    } else if (type.contains("yellow")) {
                        getStyleClass().add("row-yellow");
                    } else if (type.contains("red") || type.contains("expul")) {
                        getStyleClass().add("row-red");
                    }
                }
            }
        };
    }

    private Label buildPlaceholder(String text) {
        Label placeholder = new Label(text);
        placeholder.getStyleClass().add("table-placeholder");
        return placeholder;
    }

    private void showDetail(MatchDetail match) {
        boolean hasMatch = match != null;

        detailTitle.setText(hasMatch ? match.getTitle() : "No match selected");
        detailSubtitle.setText(hasMatch ? describe(match) : "");
        scoreboardHome.setText(hasMatch ? match.getHomeTeam() : "");
        scoreboardAway.setText(hasMatch ? match.getAwayTeam() : "");
        scoreboardScore.setText(hasMatch ? match.getScore() : "");
        homeTeamName.setText(hasMatch ? match.getHomeTeam() : "");
        awayTeamName.setText(hasMatch ? match.getAwayTeam() : "");

        homeLineup.getItems().setAll(hasMatch ? match.getHomeLineup() : List.of());
        awayLineup.getItems().setAll(hasMatch ? match.getAwayLineup() : List.of());
        incidences.getItems().setAll(hasMatch ? match.getIncidences() : List.of());

        boolean showStandings = hasMatch && match.hasStandings();
        standingsBefore.getItems().setAll(showStandings ? match.getStandingsBefore() : List.of());
        standingsAfter.getItems().setAll(showStandings ? match.getStandingsAfter() : List.of());
        standingsPanel.setVisible(showStandings);
        standingsPanel.setManaged(showStandings);
    }

private String describe(MatchDetail match) {
    StringBuilder description = new StringBuilder();
    description.append(match.getPhase())
            .append("  |  ").append(match.getDate())
            .append("  |  Stadium: ").append(match.getStadium())
            .append("  |  Referee: ").append(match.getReferee());
    if (!match.getAggregate().isEmpty()) {
        description.append("  |  ").append(match.getAggregate());
    }
    return description.toString();
}
}
