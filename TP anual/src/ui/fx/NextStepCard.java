package ui.fx;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class NextStepCard extends VBox {

    public NextStepCard(ObservableValue<String> nextStep, ObservableValue<String> champion, Runnable onGo) {
        getStyleClass().add("next-step-card");

        Label heading = new Label("NEXT STEP");
        heading.getStyleClass().add("card-kicker");

        Label step = new Label();
        step.getStyleClass().add("next-step-text");
        step.setWrapText(true);
        step.textProperty().bind(nextStep);

        Button go = new Button("Go to Tournament");
        go.getStyleClass().addAll("action-button", "primary-button");
        go.setOnAction(event -> onGo.run());

        Label trophy = new Label();
        trophy.getStyleClass().add("champion-banner");
        trophy.textProperty().bind(javafx.beans.binding.Bindings.concat("Champion: ", champion));
        trophy.visibleProperty().bind(champion.map(name -> !name.isEmpty()));
        trophy.managedProperty().bind(trophy.visibleProperty());

        getChildren().addAll(heading, step, go, trophy);
    }
}
