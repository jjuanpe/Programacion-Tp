package ui.fx;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

/**
 * Seccion "Top Scorers": la tabla de goleadores del campeonato.
 *
 * La vista no calcula nada ni conoce el dominio: recibe la lista observable de
 * filas ya armadas y las muestra. Cuando el controlador la completa, la tabla
 * se actualiza sola.
 */
public class TopScorersView extends VBox {

    public TopScorersView(ObservableList<ScorerRow> rows, ObservableValue<String> emptyMessage) {
        getStyleClass().add("page");

        Label title = new Label("Top Scorers");
        title.getStyleClass().add("section-title");

        getChildren().addAll(title, buildTable(rows, emptyMessage));
    }

    private TableView<ScorerRow> buildTable(
            ObservableList<ScorerRow> rows,
            ObservableValue<String> emptyMessage) {
        TableView<ScorerRow> table = new TableView<>(rows);
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(buildPlaceholder(emptyMessage));

        table.getColumns().add(TableColumns.numeric("Position",
                row -> String.valueOf(row.getPosition())));
        table.getColumns().add(TableColumns.text("Player", ScorerRow::getPlayer));
        table.getColumns().add(TableColumns.text("Team", ScorerRow::getTeam));
        table.getColumns().add(TableColumns.numeric("Goals",
                row -> String.valueOf(row.getGoals())));
        table.getColumns().add(TableColumns.numeric("Penalty Goals",
                row -> String.valueOf(row.getPenaltyGoals())));
        return table;
    }

    private Label buildPlaceholder(ObservableValue<String> emptyMessage) {
        Label placeholder = new Label();
        placeholder.getStyleClass().add("table-placeholder");
        placeholder.textProperty().bind(emptyMessage);
        return placeholder;
    }
}
