package ui.fx;

import viewmodel.ScorerRow;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ScorerPreview extends VBox {

    private static final int VISIBLE_ROWS = 5;

    private final VBox rowsBox = new VBox();

    public ScorerPreview(ObservableList<ScorerRow> rows, Runnable onOpen) {
        getStyleClass().add("scorer-preview");

        Label heading = new Label("TOP SCORERS");
        heading.getStyleClass().add("card-kicker");

        rowsBox.getStyleClass().add("scorer-rows");
        rows.addListener((ListChangeListener<ScorerRow>) change -> render(rows));
        render(rows);

        getChildren().addAll(heading, rowsBox);
        getStyleClass().add("clickable");
        setOnMouseClicked(event -> onOpen.run());
    }

    private void render(ObservableList<ScorerRow> rows) {
        rowsBox.getChildren().clear();
        if (rows.isEmpty()) {
            Label empty = new Label("No goals yet. Play some matches!");
            empty.getStyleClass().add("table-placeholder");
            rowsBox.getChildren().add(empty);
        }
        int shown = Math.min(VISIBLE_ROWS, rows.size());
        for (int index = 0; index < shown; index++) {
            rowsBox.getChildren().add(buildRow(rows.get(index)));
        }
    }

    private HBox buildRow(ScorerRow scorer) {
        Label rank = new Label(String.valueOf(scorer.getPosition()));
        rank.getStyleClass().add("scorer-rank");

        Label player = new Label(scorer.getPlayer());
        player.getStyleClass().add("scorer-player");
        Label team = new Label(scorer.getTeam());
        team.getStyleClass().add("action-hint");
        VBox names = new VBox(player, team);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label goals = new Label(String.valueOf(scorer.getGoals()));
        goals.getStyleClass().add("scorer-goals");

        HBox row = new HBox(rank, names, spacer, goals);
        row.getStyleClass().add("scorer-row");
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }
}
