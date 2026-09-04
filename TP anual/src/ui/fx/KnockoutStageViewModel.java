package ui.fx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Estado observable de la pagina Knockout Stage: solo los partidos de la
 * fase eliminatoria (cuartos, semis y final), en cualquier estado.
 *
 * El filtro por fase lo aplica la vista sobre esta lista; el ViewModel
 * siempre guarda el conjunto completo de partidos de eliminatorias.
 */
public class KnockoutStageViewModel {

    private final ObservableList<MatchDetail> matches = FXCollections.observableArrayList();

    /*
     * Igual que en MatchesViewModel: el envoltorio de solo lectura se guarda
     * en un campo para que el listener debil que engancha
     * unmodifiableObservableList no lo pierda el recolector de basura.
     */
    private final ObservableList<MatchDetail> readOnlyMatches =
            FXCollections.unmodifiableObservableList(matches);

    private final StringProperty status =
            new SimpleStringProperty(this, "status", "No knockout matches yet");

    /** Solo lectura: la vista muestra los partidos, no los modifica. */
    public ObservableList<MatchDetail> getMatches() {
        return readOnlyMatches;
    }

    public void setMatches(List<MatchDetail> newMatches) {
        matches.setAll(newMatches);
    }

    /** Mensaje que se muestra cuando todavia no hay partidos de eliminatorias. */
    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String text) {
        status.set(text);
    }
}
