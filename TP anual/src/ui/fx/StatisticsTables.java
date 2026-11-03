package ui.fx;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public final class StatisticsTables {

    private static final double TABLE_HEIGHT = 460;

    private StatisticsTables() {
    }

    public static VBox topScorers(ObservableList<ScorerRow> rows, ObservableValue<String> emptyMessage) {
        TableView<ScorerRow> table = newTable(rows, emptyMessage);
        table.getColumns().add(TableColumns.numeric("Position", row -> String.valueOf(row.getPosition())));
        table.getColumns().add(TableColumns.text("Player", ScorerRow::getPlayer));
        table.getColumns().add(TableColumns.text("Team", ScorerRow::getTeam));
        table.getColumns().add(TableColumns.numeric("Goals", row -> String.valueOf(row.getGoals())));
        table.getColumns().add(TableColumns.numeric("Penalty goals", row -> String.valueOf(row.getPenaltyGoals())));
        return section("Goals scored in matches; shoot-out penalties and own goals do not count.", table);
    }

    public static VBox fairPlay(ObservableList<FairPlayRow> rows, ObservableValue<String> emptyMessage) {
        TableView<FairPlayRow> table = newTable(rows, emptyMessage);
        table.getColumns().add(TableColumns.numeric("Position", row -> String.valueOf(row.getPosition())));
        table.getColumns().add(TableColumns.text("Team", FairPlayRow::getTeam));
        table.getColumns().add(TableColumns.numeric("Yellow cards", row -> String.valueOf(row.getYellowCards())));
        table.getColumns().add(TableColumns.numeric("Expulsions", row -> String.valueOf(row.getExpulsions())));
        table.getColumns().add(TableColumns.numeric("Fair play points", row -> String.valueOf(row.getPoints())));
        return section("Fewer points means a cleaner team. Yellow card: 1 point; "
                + "second yellow: 1 more; direct red card: 4.", table);
    }

    public static VBox minutesPlayed(ObservableList<MinutesRow> rows, ObservableValue<String> emptyMessage) {
        TableView<MinutesRow> table = newTable(rows, emptyMessage);
        table.getColumns().add(TableColumns.numeric("Position", row -> String.valueOf(row.getPosition())));
        table.getColumns().add(TableColumns.text("Player", MinutesRow::getPlayer));
        table.getColumns().add(TableColumns.text("Team", MinutesRow::getTeam));
        table.getColumns().add(TableColumns.text("Playing position", MinutesRow::getPlayerPosition));
        table.getColumns().add(TableColumns.numeric("Matches", row -> String.valueOf(row.getMatchesPlayed())));
        table.getColumns().add(TableColumns.numeric("Minutes", row -> String.valueOf(row.getMinutesPlayed())));
        return section("Players who took part in at least one match, from most to fewest minutes.", table);
    }

    public static VBox referees(
            ObservableList<RefereeRow> rows,
            ObservableValue<String> emptyMessage,
            ObservableValue<String> averageYears) {
        TableView<RefereeRow> table = newTable(rows, emptyMessage);
        table.getColumns().add(TableColumns.numeric("Position", row -> String.valueOf(row.getPosition())));
        table.getColumns().add(TableColumns.text("Referee", RefereeRow::getReferee));
        table.getColumns().add(TableColumns.text("Country", RefereeRow::getCountry));
        table.getColumns().add(TableColumns.numeric("Matches", row -> String.valueOf(row.getMatchesOfficiated())));
        table.getColumns().add(TableColumns.numeric("Years refereeing", row -> String.valueOf(row.getYears())));

        Label average = new Label();
        average.getStyleClass().add("action-message");
        average.textProperty().bind(averageYears);

        VBox section = section("Referees ranked by matches officiated in the tournament.", table);
        section.getChildren().add(average);
        return section;
    }

    private static <T> TableView<T> newTable(ObservableList<T> rows, ObservableValue<String> emptyMessage) {
        TableView<T> table = new TableView<>(rows);
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(TABLE_HEIGHT);

        Label placeholder = new Label();
        placeholder.getStyleClass().add("table-placeholder");
        placeholder.textProperty().bind(emptyMessage);
        table.setPlaceholder(placeholder);
        return table;
    }

    private static VBox section(String hintText, TableView<?> table) {
        Label hint = new Label(hintText);
        hint.getStyleClass().add("action-hint");
        hint.setWrapText(true);

        VBox section = new VBox(hint, table);
        section.getStyleClass().add("page-section");
        return section;
    }
}