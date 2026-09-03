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

    private final StringProperty status = new SimpleStringProperty(this, "status", EMPTY_MESSAGE);

    /** Solo lectura: la vista muestra los listados, no los modifica. */
    public ObservableList<TeamRow> getRows() {
        return FXCollections.unmodifiableObservableList(rows);
    }

    public ObservableList<TeamDetail> getTeams() {
        return FXCollections.unmodifiableObservableList(teams);
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
