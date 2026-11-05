package ui.fx;

import viewmodel.FixtureRow;
import viewmodel.GroupZone;
import viewmodel.StandingRow;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GroupStageView extends VBox {

    private static final double ROW_HEIGHT = 34;
    // encabezado de columnas (34) + padding de la tabla (8 arriba y abajo) + bordes
    private static final double TABLE_CHROME_HEIGHT = 52;

    private static final String QUALIFIED_STYLE_CLASS = "qualified-row";

    private final VBox zonesBox = new VBox();

    public GroupStageView(
            ObservableList<GroupZone> zones,
            ObservableValue<String> emptyMessage) {
        getStyleClass().add("page");

        Label title = Icons.titled(new Label("Group Stage"), Icon.GRID);
        title.getStyleClass().add("section-title");

        Label emptyLabel = new Label();
        emptyLabel.getStyleClass().add("placeholder-note");
        emptyLabel.textProperty().bind(emptyMessage);
        emptyLabel.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> emptyMessage.getValue() != null && !emptyMessage.getValue().isEmpty(),
                emptyMessage));
        emptyLabel.managedProperty().bind(emptyLabel.visibleProperty());

        zonesBox.getStyleClass().add("zones");
        zones.addListener((ListChangeListener<GroupZone>) change -> rebuild(zones));
        rebuild(zones);

        getChildren().addAll(title, buildActions(), emptyLabel, zonesBox);
    }

    private HBox buildActions() {
        Label note = new Label("Highlighted rows qualify for the quarter-finals.");
        note.getStyleClass().add("action-hint");

        HBox actions = new HBox(note);
        actions.getStyleClass().add("action-row");
        actions.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return actions;
    }

    private void rebuild(ObservableList<GroupZone> zones) {
        zonesBox.getChildren().clear();
        for (GroupZone zone : zones) {
            zonesBox.getChildren().add(buildZonePanel(zone));
        }
    }

    private VBox buildZonePanel(GroupZone zone) {
        Label name = new Label(zone.getName());
        name.getStyleClass().add("detail-title");

        Label teams = new Label(new StringBuilder("Teams: ").append(zone.getTeams()).toString());
        teams.getStyleClass().add("zone-teams");
        teams.setWrapText(true);

        Label qualified = new Label(zone.getQualifiedTeams());
        qualified.getStyleClass().add("qualified-note");

        VBox panel = new VBox(
                name,
                teams,
                buildSubtitle("Standings"),
                buildStandingsTable(zone),
                qualified,
                buildSubtitle("Fixture"),
                buildFixtureTable(zone));
        panel.getStyleClass().addAll("panel", "zone-panel");
        return panel;
    }

    private Label buildSubtitle(String text) {
        Label subtitle = new Label(text);
        subtitle.getStyleClass().add("subsection-title");
        return subtitle;
    }

    private TableView<StandingRow> buildStandingsTable(GroupZone zone) {
        TableView<StandingRow> table = new TableView<>();
        table.getItems().setAll(zone.getStandings());
        table.getStyleClass().addAll("data-table", "standings-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        fitToRows(table, zone.getStandings().size());

        table.setRowFactory(ignored -> buildStandingRow());

        table.getColumns().add(TableColumns.numeric("#", row -> String.valueOf(row.getPosition())));
        table.getColumns().add(TableColumns.text("Team", StandingRow::getTeam));
        table.getColumns().add(TableColumns.numeric("P", row -> String.valueOf(row.getPlayed())));
        table.getColumns().add(TableColumns.numeric("W", row -> String.valueOf(row.getWon())));
        table.getColumns().add(TableColumns.numeric("D", row -> String.valueOf(row.getDrawn())));
        table.getColumns().add(TableColumns.numeric("L", row -> String.valueOf(row.getLost())));
        table.getColumns().add(TableColumns.numeric("GF", row -> String.valueOf(row.getGoalsFor())));
        table.getColumns().add(TableColumns.numeric("GA", row -> String.valueOf(row.getGoalsAgainst())));
        table.getColumns().add(TableColumns.numeric("GD", row -> String.valueOf(row.getGoalDifference())));
        table.getColumns().add(TableColumns.numeric("Pts", row -> String.valueOf(row.getPoints())));
        return table;
    }

    private TableRow<StandingRow> buildStandingRow() {
        return new TableRow<>() {
            @Override
            protected void updateItem(StandingRow item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove(QUALIFIED_STYLE_CLASS);
                if (!empty && item != null && item.isQualified()) {
                    getStyleClass().add(QUALIFIED_STYLE_CLASS);
                }
            }
        };
    }

    private TableView<FixtureRow> buildFixtureTable(GroupZone zone) {
        TableView<FixtureRow> table = new TableView<>();
        table.getItems().setAll(zone.getFixture());
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        fitToRows(table, zone.getFixture().size());

        table.getColumns().add(TableColumns.text("Date", FixtureRow::getDate));
        table.getColumns().add(TableColumns.text("Home", FixtureRow::getHomeTeam));
        table.getColumns().add(TableColumns.numeric("Score", FixtureRow::getScore));
        table.getColumns().add(TableColumns.text("Away", FixtureRow::getAwayTeam));
        table.getColumns().add(TableColumns.text("Status", FixtureRow::getStatus));
        table.getColumns().add(TableColumns.text("Referee", FixtureRow::getReferee));
        table.getColumns().add(TableColumns.text("Stadium", FixtureRow::getStadium));
        return table;
    }

    /*
     * La tabla mide exactamente lo que ocupan sus filas, sin filas vacias abajo.
     * Se fijan min y max porque la hoja de estilos le da a .data-table una altura
     * preferida fija que pisa a setPrefHeight.
     */
    private void fitToRows(TableView<?> table, int rowCount) {
        table.setFixedCellSize(ROW_HEIGHT);
        double height = ROW_HEIGHT * rowCount + TABLE_CHROME_HEIGHT;
        table.setMinHeight(height);
        table.setMaxHeight(height);
    }
}
