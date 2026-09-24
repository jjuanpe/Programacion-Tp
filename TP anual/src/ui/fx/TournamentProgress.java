package ui.fx;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class TournamentProgress extends VBox {

    private static final String STATE_COMPLETED = "completed";
    private static final String STATE_CURRENT = "current";
    private static final String STATE_PENDING = "pending";

    private final Map<TournamentStage, HBox> steps = new EnumMap<>(TournamentStage.class);
    private final List<Region> connectors = new ArrayList<>();

    public TournamentProgress(ObservableValue<TournamentStage> currentStage) {
        getStyleClass().add("tournament-progress");

        Label title = new Label("Tournament Progress");
        title.getStyleClass().add("section-title");

        getChildren().addAll(title, buildStepper());

        currentStage.addListener((observable, previous, current) -> refresh(current));
        refresh(currentStage.getValue());
    }

    private HBox buildStepper() {
        HBox stepper = new HBox();
        stepper.getStyleClass().add("stepper");
        stepper.setAlignment(Pos.CENTER_LEFT);

        for (TournamentStage stage : TournamentStage.values()) {
            if (!steps.isEmpty()) {
                stepper.getChildren().add(buildConnector());
            }
            HBox step = buildStep(stage);
            steps.put(stage, step);
            stepper.getChildren().add(step);
        }
        return stepper;
    }

    private HBox buildStep(TournamentStage stage) {
        Region dot = new Region();
        dot.getStyleClass().add("step-dot");

        Label label = new Label(stage.getLabel());
        label.getStyleClass().add("step-label");

        HBox step = new HBox(dot, label);
        step.getStyleClass().add("step");
        step.setAlignment(Pos.CENTER_LEFT);
        step.setMinWidth(Region.USE_PREF_SIZE);
        return step;
    }

    private Region buildConnector() {
        Region connector = new Region();
        connector.getStyleClass().add("step-connector");
        HBox.setHgrow(connector, Priority.ALWAYS);
        connectors.add(connector);
        return connector;
    }

    private void refresh(TournamentStage currentStage) {
        for (TournamentStage stage : TournamentStage.values()) {
            applyState(steps.get(stage), stateOf(stage, currentStage));
        }
        for (int index = 0; index < connectors.size(); index++) {
            boolean reached = currentStage != null && index < currentStage.ordinal();
            applyState(connectors.get(index), reached ? STATE_COMPLETED : STATE_PENDING);
        }
    }

    private String stateOf(TournamentStage stage, TournamentStage currentStage) {
        if (currentStage == null) {
            return STATE_PENDING;
        }
        if (stage == currentStage) {
            return STATE_CURRENT;
        }
        return stage.ordinal() < currentStage.ordinal() ? STATE_COMPLETED : STATE_PENDING;
    }

    private void applyState(Node node, String state) {
        node.getStyleClass().removeAll(STATE_COMPLETED, STATE_CURRENT, STATE_PENDING);
        node.getStyleClass().add(state);
    }
}
