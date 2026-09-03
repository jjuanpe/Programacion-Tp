package ui.fx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Estado observable del reporte de goleadores.
 *
 * La vista se ata a esta lista y se redibuja sola cuando el controlador la
 * completa. Igual que {@link DashboardViewModel}, no importa ninguna clase de
 * {@code model}: solo maneja filas ya armadas.
 */
public class TopScorersViewModel {

    private final ObservableList<ScorerRow> rows = FXCollections.observableArrayList();

    /*
     * El envoltorio de solo lectura se guarda en un campo a proposito.
     * FXCollections.unmodifiableObservableList engancha un listener DEBIL a la
     * lista original: si nadie sostiene el envoltorio, el recolector se lo
     * lleva y la vista deja de enterarse de los cambios, en silencio y para
     * siempre. Devolver uno nuevo en cada llamada provocaba justamente eso.
     */
    private final ObservableList<ScorerRow> readOnlyRows =
            FXCollections.unmodifiableObservableList(rows);

    /** Mensaje que se muestra cuando la tabla esta vacia. */
    private final StringProperty status =
            new SimpleStringProperty(this, "status", "No report generated yet");

    /** Solo lectura: la vista muestra las filas, no las modifica. */
    public ObservableList<ScorerRow> getRows() {
        return readOnlyRows;
    }

    public void setRows(List<ScorerRow> newRows) {
        rows.setAll(newRows);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String text) {
        status.set(text);
    }
}
