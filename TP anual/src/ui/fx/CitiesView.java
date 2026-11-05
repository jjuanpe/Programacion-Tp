package ui.fx;

import controller.CitiesController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.venue.City;
import model.team.Country;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

public class CitiesView extends VBox {
    private final Label summary = new Label();
    private final TableView<City> table = new TableView<>();
    private final Button add = new Button("Agregar ciudad");
    private final Button edit = new Button("Editar seleccionada");
    private final Button delete = new Button("Eliminar seleccionada");
    private final Button reload = new Button("Actualizar listado");
    private CitiesController controller;
    private boolean busy;

    public CitiesView() {
        getStyleClass().add("page");
        Label title = new Label("Ciudades");
        title.getStyleClass().add("section-title");
        Label note = new Label("Solo se pueden eliminar ciudades sin estadios asignados. "
                + "Los cambios del catálogo se aplican a futuros sorteos.");
        note.setWrapText(true);
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getColumns().add(TableColumns.numeric("ID", city -> city.getId().toString()));
        table.getColumns().add(TableColumns.text("Ciudad", City::getName));
        table.getColumns().add(TableColumns.text("País", city -> city.getCountry().getCountryName()));
        table.setPlaceholder(new Label("No hay ciudades cargadas."));
        add.setOnAction(event -> showCityDialog(null));
        edit.setOnAction(event -> showCityDialog(table.getSelectionModel().getSelectedItem()));
        delete.setOnAction(event -> deleteSelected());
        reload.setOnAction(event -> refresh());
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateButtons());
        getChildren().addAll(title, note, new HBox(10, add, edit, delete, reload), summary, table);
        updateButtons();
    }

    public void setController(CitiesController controller) {
        this.controller = controller;
        updateButtons();
    }

    public void refresh() {
        if (controller != null && !busy) {
            runDatabaseAction(controller::getRegisteredCities, cities -> {
                table.getItems().setAll(cities);
                summary.setText(new StringBuilder().append(cities.size()).append(" ciudades registradas").toString());
            });
        }
    }

    private void showCityDialog(City selected) {
        boolean editing = selected != null;
        Dialog<ButtonType> dialog = new Dialog<>();
        if (editing) {
            dialog.setTitle("Editar ciudad");
        } else {
            dialog.setTitle("Agregar ciudad");
        }
        dialog.initOwner(getScene().getWindow());
        TextField id = new TextField();
        TextField name = new TextField();
        TextField country = new TextField();
        if (editing) {
            id.setText(selected.getId().toString());
            id.setEditable(false);
            name.setText(selected.getName());
            country.setText(selected.getCountry().getCountryName());
        }
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("ID"), id);
        form.addRow(1, new Label("Nombre"), name);
        form.addRow(2, new Label("País"), country);
        dialog.getDialogPane().setContent(form);
        ButtonType save = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(save, cancel);
        Button submit = (Button) dialog.getDialogPane().lookupButton(save);
        submit.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            try {
                if (name.getText().isBlank() || country.getText().isBlank()) {
                    throw new IllegalArgumentException("El nombre y el país son obligatorios.");
                }
                long cityId = Long.parseLong(id.getText().trim());
                if (cityId <= 0) {
                    throw new IllegalArgumentException("El ID debe ser un número entero positivo.");
                }
                City city = new City(cityId, name.getText(), new Country(country.getText()));
                submit.setDisable(true);
                runDatabaseAction(() -> {
                    if (editing) {
                        controller.updateCity(city.getId(), city.getName(), city.getCountry().getCountryName());
                    } else {
                        controller.addCity(city.getId(), city.getName(), city.getCountry().getCountryName());
                    }
                    return city;
                }, saved -> {
                    dialog.setResult(save);
                    dialog.close();
                    refresh();
                }, () -> submit.setDisable(false));
            } catch (NumberFormatException exception) {
                showError("El ID debe ser un número entero positivo.");
            } catch (IllegalArgumentException exception) {
                showError(exception.getMessage());
            }
        });
        dialog.showAndWait();
    }

    private void deleteSelected() {
        City city = table.getSelectionModel().getSelectedItem();
        if (city != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    new StringBuilder("¿Eliminar la ciudad ").append(city.getName())
                            .append(" (ID ").append(city.getId()).append(")?").toString(),
                    ButtonType.OK, ButtonType.CANCEL);
            confirm.initOwner(getScene().getWindow());
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                runDatabaseAction(() -> {
                    controller.deleteCity(city);
                    return city;
                }, removed -> refresh());
            }
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
        add.setDisable(controller == null || busy);
        reload.setDisable(controller == null || busy);
        edit.setDisable(controller == null || busy || table.getSelectionModel().getSelectedItem() == null);
        delete.setDisable(controller == null || busy || table.getSelectionModel().getSelectedItem() == null);
    }

    private void showError(String message) {
        String errorMessage;
        if (message == null) {
            errorMessage = "No se pudo completar la operación en la base de datos.";
        } else {
            errorMessage = message;
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("No se pudo completar la operación");
        TextArea details = new TextArea(errorMessage);
        details.setEditable(false);
        details.setWrapText(true);
        details.setPrefColumnCount(55);
        details.setPrefRowCount(10);
        details.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        alert.getDialogPane().setContent(details);
        alert.setResizable(true);
        alert.initOwner(getScene().getWindow());
        alert.showAndWait();
    }
}
