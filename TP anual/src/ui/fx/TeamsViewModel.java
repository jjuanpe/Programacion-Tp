package ui.fx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Estado observable de la pagina Teams.
 *
 * Guarda los dos listados: el resumen alfabetico con el rendimiento de cada
 * equipo, y las fichas completas que se muestran al seleccionar uno.
 */
public class TeamsViewModel {

    private static final String EMPTY_MESSAGE = "No teams imported yet";

    private final ObservableList<TeamRow> rows = FXCollections.observableArrayList();
    private final ObservableList<TeamDetail> teams = FXCollections.observableArrayList();

    /*
     * El envoltorio de solo lectura se guarda en un campo a proposito.
     * FXCollections.unmodifiableObservableList engancha un listener DEBIL a la
     * lista original: si nadie sostiene el envoltorio, el recolector se lo
     * lleva y la vista deja de enterarse de los cambios, en silencio y para
     * siempre. Devolver uno nuevo en cada llamada provocaba justamente eso.
     */
    private final ObservableList<TeamRow> readOnlyRows =
            FXCollections.unmodifiableObservableList(rows);
    private final ObservableList<TeamDetail> readOnlyTeams =
            FXCollections.unmodifiableObservableList(teams);

    private final StringProperty status = new SimpleStringProperty(this, "status", EMPTY_MESSAGE);

    /** Solo lectura: la vista muestra los listados, no los modifica. */
    public ObservableList<TeamRow> getRows() {
        return readOnlyRows;
    }

    public ObservableList<TeamDetail> getTeams() {
        return readOnlyTeams;
    }

    public void setRows(List<TeamRow> newRows) {
        rows.setAll(newRows);
    }

    public void setTeams(List<TeamDetail> newTeams) {
        teams.setAll(newTeams);
    }

    /** Mensaje que se muestra cuando todavia no hay equipos. */
    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String text) {
        status.set(text);
    }
}
