package ui.fx;

import controller.TournamentSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.venue.City;
import model.venue.Stadium;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

public class StadiumsView extends VBox {
    private final Label summary = new Label();
    private final TableView<Stadium> table = new TableView<>();
    private final Button add = new Button("Add stadium");
    private final Button edit = new Button("Editar seleccionado");
    private final Button delete = new Button("Delete selected");
    private final Button reload = new Button("Refresh");
    private TournamentSession session;
    private boolean busy;

    public StadiumsView() {
        getStyleClass().add("page");
        Label title = new Label("Stadiums");
        title.getStyleClass().add("section-title");
        Label note = new Label("Database catalogue. Changes apply to future group draws; existing tournament venues are preserved.");
        note.setWrapText(true);
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getColumns().add(TableColumns.numeric("ID", stadium -> stadium.getId().toString()));
        table.getColumns().add(TableColumns.text("Stadium", Stadium::getName));
        table.getColumns().add(TableColumns.text("City", stadium -> stadium.getCity().getName()));
        table.getColumns().add(TableColumns.numeric("Capacity", stadium -> String.valueOf(stadium.getCapacity())));
        table.setPlaceholder(new Label("No stadiums loaded."));
        add.setOnAction(event -> runDatabaseAction(session::getRegisteredCities, cities -> showStadiumDialog(cities, null)));
        edit.setOnAction(event -> {
            Stadium selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                runDatabaseAction(session::getRegisteredCities, cities -> showStadiumDialog(cities, selected));
            }
        });
        delete.setOnAction(event -> deleteSelected());
        reload.setOnAction(event -> refresh());
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateButtons());
        getChildren().addAll(title, note, new HBox(10, add, edit, delete, reload), summary, table);
        updateButtons();
    }

    public void setSession(TournamentSession session) {
        this.session = session;
        updateButtons();
    }

    public void refresh() {
        if (session != null && !busy) {
            runDatabaseAction(session::getRegisteredStadiums, stadiums -> {
                table.getItems().setAll(stadiums);
                summary.setText(stadiums.size() + " registered stadiums");
            });
        }
    }

    private void showStadiumDialog(List<City> cities, Stadium selected) {
        if (cities.isEmpty()) {
            showError("No cities are registered in the database.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        boolean editing = selected != null;
        if (editing) {
            dialog.setTitle("Editar estadio");
        } else {
            dialog.setTitle("Agregar estadio");
        }
        dialog.initOwner(getScene().getWindow());
        TextField id = new TextField();
        TextField name = new TextField();
        TextField capacity = new TextField();
        ComboBox<City> city = new ComboBox<>();
        city.getItems().setAll(cities);
        if (editing) {
            id.setText(selected.getId().toString());
            name.setText(selected.getName());
            capacity.setText(String.valueOf(selected.getCapacity()));
            int index = 0;
            City selectedCity = null;
            while (index < cities.size() && selectedCity == null) {
                City candidate = cities.get(index);
                if (candidate.getId().equals(selected.getCity().getId())) {
                    selectedCity = candidate;
                }
                index++;
            }
            city.setValue(selectedCity);
        }
        city.setConverter(new StringConverter<>() {
            @Override public String toString(City value) {
                String description;
                if (value == null) {
                    description = "";
                } else {
                    description = value.getName() + " (ID " + value.getId() + ")";
                }
                return description;
            }
            @Override public City fromString(String value) { return null; }
        });
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("ID"), id);
        form.addRow(1, new Label("Name"), name);
        form.addRow(2, new Label("City"), city);
        form.addRow(3, new Label("Capacity"), capacity);
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button submit = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        submit.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            try {
                long stadiumId = Long.parseLong(id.getText().trim());
                int stadiumCapacity = Integer.parseInt(capacity.getText().trim());
                String stadiumName = name.getText();
                City stadiumCity = city.getValue();
                submit.setDisable(true);
                runDatabaseAction(() -> {
                    if (editing) {
                        session.updateStadium(selected.getId(), stadiumId, stadiumName, stadiumCity, stadiumCapacity);
                    } else {
                        session.addStadium(stadiumId, stadiumName, stadiumCity, stadiumCapacity);
                    }
                    return stadiumId;
                }, saved -> {
                    dialog.setResult(ButtonType.OK);
                    dialog.close();
                    refresh();
                }, () -> submit.setDisable(false));
            } catch (NumberFormatException exception) {
                showError("El ID y la capacidad deben ser números enteros positivos.");
            } catch (IllegalArgumentException exception) {
                showError(exception.getMessage());
            }
        });
        dialog.showAndWait();
    }

    private void deleteSelected() {
        Stadium stadium = table.getSelectionModel().getSelectedItem();
        if (stadium == null) {
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + stadium.getName() + " (ID " + stadium.getId() + ") from the database?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.initOwner(getScene().getWindow());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            runDatabaseAction(() -> {
                session.deleteStadium(stadium);
                return stadium;
            }, removed -> refresh());
        }
    }

    private <T> void runDatabaseAction(Callable<T> action, Consumer<T> onSuccess) {
        runDatabaseAction(action, onSuccess, () -> { });
    }

    private <T> void runDatabaseAction(Callable<T> action, Consumer<T> onSuccess, Runnable onFailure) {
        busy = true;
        updateButtons();
        CompletableFuture.supplyAsync(() -> {
            try {
                return action.call();
            } catch (Exception exception) {
                throw new CompletionException(exception);
            }
        }).whenComplete((value, failure) -> Platform.runLater(() -> {
            busy = false;
            updateButtons();
            if (failure == null) {
                onSuccess.accept(value);
            } else {
                onFailure.run();
                Throwable cause;
                if (failure.getCause() == null) {
                    cause = failure;
                } else {
                    cause = failure.getCause();
                }
                showError(cause.getMessage());
            }
        }));
    }

    private void updateButtons() {
        add.setDisable(session == null || busy);
        reload.setDisable(session == null || busy);
        edit.setDisable(session == null || busy || table.getSelectionModel().getSelectedItem() == null);
        delete.setDisable(session == null || busy || table.getSelectionModel().getSelectedItem() == null);
    }

    private void showError(String message) {
        String errorMessage;
        if (message == null) {
            errorMessage = "Database operation failed.";
        } else {
            errorMessage = message;
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("No se pudo completar la operación");
        TextArea details = new TextArea(errorMessage);
        details.setEditable(false);
        details.setWrapText(true);
        details.setPrefColumnCount(55);
        details.setPrefRowCount(6);
        alert.getDialogPane().setContent(details);
        alert.setResizable(true);
        alert.initOwner(getScene().getWindow());
        alert.showAndWait();
    }
}
