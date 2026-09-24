package ui.fx;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SummaryCard extends VBox {

    public SummaryCard(String title, ObservableValue<String> value) {
        getStyleClass().add("summary-card");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("summary-card-title");

        Label valueLabel = new Label();
        valueLabel.getStyleClass().add("summary-card-value");
        valueLabel.textProperty().bind(value);

        getChildren().addAll(titleLabel, valueLabel);
    }
}
