package ui.fx;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.function.Consumer;

public class TournamentView extends VBox {

    private static final String DATA_DIRECTORY = "docs";

    private final Consumer<String> onImport;

    public TournamentView(
            TournamentViewModel viewModel,
            Consumer<String> onImport,
            Runnable onDraw,
            Runnable onAdvance) {
        this.onImport = onImport;
        getStyleClass().add("page");

        Label title = new Label("Tournament");
        title.getStyleClass().add("section-title");

        getChildren().addAll(
                title,
                buildStatusPanel(viewModel),
                buildActions(viewModel, onDraw, onAdvance));
    }

    private VBox buildStatusPanel(TournamentViewModel viewModel) {
        VBox panel = new VBox(
                buildStatusRow("Status", viewModel.stateProperty()),
                buildStatusRow("Teams loaded", viewModel.teamsProperty().asString()),
                buildStatusRow("Referees loaded", viewModel.refereesProperty().asString()),
                buildStatusRow("Groups drawn", viewModel.zonesProperty().asString()),
                buildStatusRow("Matches played", Bindings.concat(
                        viewModel.matchesPlayedProperty().asString(),
                        " / ",
                        viewModel.totalMatchesProperty().asString())),
                buildStatusRow("Data warnings", viewModel.warningsProperty().asString()));
        panel.getStyleClass().add("panel");
        return panel;
    }

    private HBox buildStatusRow(String label, ObservableValue<String> value) {
        Label name = new Label(label);
        name.getStyleClass().add("status-row-label");

        Label content = new Label();
        content.getStyleClass().add("status-row-value");
        content.textProperty().bind(value);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(name, spacer, content);
        row.getStyleClass().add("status-row");
        return row;
    }

    private VBox buildActions(TournamentViewModel viewModel, Runnable onDraw, Runnable onAdvance) {
        Button importButton = buildButton("Import data", "secondary-button");
        importButton.disableProperty().bind(viewModel.canImportProperty().not());
        importButton.setOnAction(event -> chooseDataFile());

        Button drawButton = buildButton("Run group draw", "secondary-button");
        drawButton.disableProperty().bind(viewModel.canDrawProperty().not());
        drawButton.setOnAction(event -> onDraw.run());

        Button advanceButton = buildButton("", "primary-button");
        advanceButton.textProperty().bind(viewModel.advanceLabelProperty());
        advanceButton.disableProperty().bind(viewModel.canAdvanceProperty().not());
        advanceButton.setOnAction(event -> onAdvance.run());

        Button saveButton = buildButton("Save state", "secondary-button");
        saveButton.disableProperty().bind(viewModel.canSaveProperty().not());

        HBox buttons = new HBox(importButton, drawButton, advanceButton, saveButton);
        buttons.getStyleClass().add("action-row");

        Label message = new Label();
        message.getStyleClass().add("action-message");
        message.textProperty().bind(viewModel.messageProperty());

        Label hint = new Label("Saving the tournament state is not implemented yet.");
        hint.getStyleClass().add("action-hint");

        VBox actions = new VBox(buttons, message, hint);
        actions.getStyleClass().add("actions");
        return actions;
    }

    private Button buildButton(String text, String variantStyleClass) {
        Button button = new Button(text);
        button.getStyleClass().addAll("action-button", variantStyleClass);
        return button;
    }

    private void chooseDataFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import tournament data");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files", "*.json"));

        File dataDirectory = new File(DATA_DIRECTORY);
        if (dataDirectory.isDirectory()) {
            chooser.setInitialDirectory(dataDirectory);
        }

        Window window = getScene() == null ? null : getScene().getWindow();
        File chosen = chooser.showOpenDialog(window);
        if (chosen != null) {
            onImport.accept(chosen.getPath());
        }
    }
}
