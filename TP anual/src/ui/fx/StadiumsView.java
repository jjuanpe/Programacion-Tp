package ui.fx;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import model.competition.Championship;
import model.venue.Stadium;

/** Registered venues and their availability for the knockout stage. */
public class StadiumsView extends VBox {
    private final Label summary = new Label();
    private final TableView<Stadium> table = new TableView<>();

    public StadiumsView() {
        getStyleClass().add("page");
        Label title = new Label("Stadiums");
        title.getStyleClass().add("section-title");
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getColumns().add(TableColumns.text("Stadium", Stadium::getName));
        table.getColumns().add(TableColumns.text("City", stadium -> stadium.getCity().getName()));
        table.getColumns().add(TableColumns.numeric("Capacity", stadium -> String.valueOf(stadium.getCapacity())));
        table.getColumns().add(TableColumns.text("Knockout", stadium ->
                stadium.getCity().getStadiums().contains(stadium) ? "Available" : "Used"));
        getChildren().addAll(title, summary, table);
        refresh(null);
    }

    public void refresh(Championship championship) {
        if (championship == null) {
            summary.setText("Run the group draw to load cities and stadiums from PostgreSQL.");
            table.getItems().clear();
            return;
        }
        summary.setText(championship.getCities().size() + " cities available for knockout; "
                + championship.getStadiums().size() + " registered stadiums");
        table.getItems().setAll(championship.getStadiums());
        table.refresh();
    }
}
