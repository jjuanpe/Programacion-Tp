package ui.fx;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Tarjeta de resumen reutilizable: un titulo chico arriba y un valor grande
 * abajo.
 *
 * No conoce el origen del dato ni tiene ningun valor propio: recibe un valor
 * observable y se actualiza sola cada vez que ese valor cambia.
 */
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
