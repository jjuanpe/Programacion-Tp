package ui.fx;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Pagina Group Stage: las cuatro zonas, cada una con su tabla de posiciones,
 * su fixture y sus clasificados.
 *
 * La vista no calcula nada: recibe las zonas ya armadas y las dibuja. Cuando
 * el controlador las rehace (por ejemplo despues del sorteo o de jugar la fase
 * de grupos), se vuelven a dibujar solas.
 */
public class GroupStageView extends VBox {

    private static final String QUALIFIED_STYLE_CLASS = "qualified-row";

    private final VBox zonesBox = new VBox();

    public GroupStageView(
            ObservableList<GroupZone> zones,
            ObservableValue<String> emptyMessage,
            BooleanProperty canDraw,
            Runnable onDraw) {
        getStyleClass().add("page");

        Label title = new Label("Group Stage");
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

        getChildren().addAll(title, buildActions(canDraw, onDraw), emptyLabel, zonesBox);
    }

    private HBox buildActions(BooleanProperty canDraw, Runnable onDraw) {
        Button drawButton = new Button("Run group draw");
        drawButton.getStyleClass().addAll("action-button", "secondary-button");
        drawButton.disableProperty().bind(canDraw.not());
        drawButton.setOnAction(event -> onDraw.run());

        Label note = new Label("Highlighted rows qualify for the quarter-finals.");
        note.getStyleClass().add("action-hint");

        HBox actions = new HBox(drawButton, note);
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

        Label teams = new Label("Teams: " + zone.getTeams());
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
        table.setPrefHeight(rowsHeight(zone.getStandings().size()));

        // Los puestos que clasifican se marcan con una clase CSS en la fila.
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
        table.setPrefHeight(rowsHeight(zone.getFixture().size()));

        table.getColumns().add(TableColumns.text("Date", FixtureRow::getDate));
        table.getColumns().add(TableColumns.text("Home", FixtureRow::getHomeTeam));
        table.getColumns().add(TableColumns.numeric("Score", FixtureRow::getScore));
        table.getColumns().add(TableColumns.text("Away", FixtureRow::getAwayTeam));
        table.getColumns().add(TableColumns.text("Status", FixtureRow::getStatus));
        table.getColumns().add(TableColumns.text("Referee", FixtureRow::getReferee));
        return table;
    }

    /** Alto justo para las filas: las tablas de zona no necesitan scroll propio. */
    private double rowsHeight(int rowCount) {
        return 34.0 * rowCount + 52.0;
    }
}
