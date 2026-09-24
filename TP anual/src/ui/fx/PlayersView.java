package ui.fx;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Predicate;

public class PlayersView extends VBox {

    private static final String ALL_POSITIONS = "All positions";
    private static final List<String> POSITION_FILTERS =
            List.of(ALL_POSITIONS, "Goalkeeper", "Defender", "Midfielder", "Forward");

    public PlayersView(ObservableList<PlayerStatsRow> players, ObservableValue<String> emptyMessage) {
        getStyleClass().add("page-section");
        FilteredList<PlayerStatsRow> filteredPlayers = new FilteredList<>(players, ignored -> true);

        getChildren().addAll(
                buildHint(),
                buildFilterRow(filteredPlayers),
                buildPlayersTable(filteredPlayers, emptyMessage));
    }

    private Label buildHint() {
        Label hint = new Label(
                "Stats are computed over every match played so far, group stage and knockouts. "
                        + "Goals conceded and its average only apply to goalkeepers.");
        hint.getStyleClass().add("action-hint");
        hint.setWrapText(true);
        return hint;
    }

    private HBox buildFilterRow(FilteredList<PlayerStatsRow> filteredPlayers) {
        Label label = new Label("Position:");
        label.getStyleClass().add("action-hint");

        ComboBox<String> filter = new ComboBox<>(FXCollections.observableArrayList(POSITION_FILTERS));
        filter.getStyleClass().add("filter-combo");
        filter.getSelectionModel().select(ALL_POSITIONS);
        filter.valueProperty().addListener((observable, previous, selected) ->
                filteredPlayers.setPredicate(matchOfPosition(selected)));

        HBox row = new HBox(label, filter);
        row.getStyleClass().add("action-row");
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Predicate<PlayerStatsRow> matchOfPosition(String selected) {
        return player -> ALL_POSITIONS.equals(selected) || player.getPosition().equals(selected);
    }

    private TableView<PlayerStatsRow> buildPlayersTable(
            ObservableList<PlayerStatsRow> players, ObservableValue<String> emptyMessage) {
        TableView<PlayerStatsRow> table = new TableView<>(players);
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(460);

        Label placeholder = new Label();
        placeholder.getStyleClass().add("table-placeholder");
        placeholder.textProperty().bind(emptyMessage);
        table.setPlaceholder(placeholder);

        table.getColumns().add(TableColumns.text("Player", PlayerStatsRow::getName));
        table.getColumns().add(TableColumns.text("Position", PlayerStatsRow::getPosition));
        table.getColumns().add(TableColumns.text("Team", PlayerStatsRow::getTeam));
        table.getColumns().add(TableColumns.numeric("Age", row -> String.valueOf(row.getAge())));
        table.getColumns().add(TableColumns.numeric("Rating", row -> String.valueOf(row.getRating())));
        table.getColumns().add(TableColumns.numeric("Matches", row -> String.valueOf(row.getMatchesPlayed())));
        table.getColumns().add(TableColumns.numeric("Minutes", row -> String.valueOf(row.getMinutesPlayed())));
        table.getColumns().add(TableColumns.numeric("Goals", row -> String.valueOf(row.getGoals())));
        table.getColumns().add(TableColumns.numeric("Goals conceded", PlayerStatsRow::getGoalsConceded));
        table.getColumns().add(TableColumns.numeric("Conceded/match", PlayerStatsRow::getGoalsConcededAverage));
        return table;
    }
}
