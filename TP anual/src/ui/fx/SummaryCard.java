package ui.fx;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SummaryCard extends VBox {

    public SummaryCard(String title, ObservableValue<String> value, Icon icon, String colorStyleClass) {
        getStyleClass().addAll("summary-card", colorStyleClass);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("summary-card-title");

        StackPane bubble = new StackPane(Icons.of(icon, 22));
        bubble.getStyleClass().add("summary-card-icon");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox top = new HBox(titleLabel, spacer, bubble);
        top.setAlignment(Pos.CENTER_LEFT);

        Label valueLabel = new Label();
        valueLabel.getStyleClass().add("summary-card-value");
        Animations.countUp(valueLabel, value);

        getChildren().addAll(top, valueLabel);
        Animations.hoverLift(this);
    }
}
