package ui.fx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Estado observable de la pagina Matches: todos los partidos del torneo,
 * sin importar la fase.
 */
public class MatchesViewModel {

    private final ObservableList<MatchDetail> matches = FXCollections.observableArrayList();

    /*
     * El envoltorio de solo lectura se guarda en un campo a proposito.
     * FXCollections.unmodifiableObservableList engancha un listener DEBIL a la
     * lista original: si nadie sostiene el envoltorio, el recolector se lo
     * lleva y la vista deja de enterarse de los cambios, en silencio y para
     * siempre.
     */
    private final ObservableList<MatchDetail> readOnlyMatches =
            FXCollections.unmodifiableObservableList(matches);

    private final StringProperty status =
            new SimpleStringProperty(this, "status", "No matches yet");

    /** Solo lectura: la vista muestra los partidos, no los modifica. */
    public ObservableList<MatchDetail> getMatches() {
        return readOnlyMatches;
    }

    public void setMatches(List<MatchDetail> newMatches) {
        matches.setAll(newMatches);
    }

    /** Mensaje que se muestra cuando todavia no hay partidos. */
    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String text) {
        status.set(text);
    }
}
