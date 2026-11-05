package ui.fx;

import viewmodel.GroupZone;
import viewmodel.StandingRow;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class ZoneLeaders extends VBox {

    private static final int LEADERS_SHOWN = 2;

    private final FlowPane cards = new FlowPane(14, 14);

    public ZoneLeaders(ObservableList<GroupZone> zones, Runnable onOpen) {
        getStyleClass().add("tournament-progress");

        Label title = new Label("Zone Leaders");
        title.getStyleClass().add("section-title");

        zones.addListener((ListChangeListener<GroupZone>) change -> render(zones, onOpen));
        render(zones, onOpen);
        getChildren().addAll(title, cards);
    }

    private void render(ObservableList<GroupZone> zones, Runnable onOpen) {
        cards.getChildren().clear();
        for (GroupZone zone : zones) {
            cards.getChildren().add(buildCard(zone, onOpen));
        }
        if (zones.isEmpty()) {
            Label empty = new Label("The zones appear after the group draw.");
            empty.getStyleClass().add("table-placeholder");
            cards.getChildren().add(empty);
        }
    }

    private VBox buildCard(GroupZone zone, Runnable onOpen) {
        Label name = new Label(zone.getName());
        name.getStyleClass().add("card-kicker");

        VBox card = new VBox(name);
        card.getStyleClass().addAll("zone-leader-card", "clickable");
        List<StandingRow> standings = zone.getStandings();
        int shown = Math.min(LEADERS_SHOWN, standings.size());
        for (int index = 0; index < shown; index++) {
            card.getChildren().add(buildLine(standings.get(index)));
        }
        card.setOnMouseClicked(event -> onOpen.run());
        return card;
    }

    private HBox buildLine(StandingRow row) {
        Label position = new Label(String.valueOf(row.getPosition()));
        position.getStyleClass().add("scorer-rank");
        Label team = new Label(row.getTeam());
        team.getStyleClass().add("scorer-player");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label points = new Label(new StringBuilder().append(row.getPoints()).append(" pts").toString());
        points.getStyleClass().add("action-hint");

        HBox line = new HBox(position, team, spacer, points);
        line.getStyleClass().add("scorer-row");
        line.setAlignment(Pos.CENTER_LEFT);
        return line;
    }
}
