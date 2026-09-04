package ui.fx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Estado observable de la pagina Players: el listado completo de jugadores
 * del campeonato, con sus estadisticas. El filtro por posicion lo aplica la
 * vista sobre esta lista.
 */
public class PlayersViewModel {

    private static final String EMPTY_MESSAGE = "No players imported yet";

    private final ObservableList<PlayerStatsRow> players = FXCollections.observableArrayList();

    /*
     * El envoltorio de solo lectura se guarda en un campo a proposito.
     * FXCollections.unmodifiableObservableList engancha un listener DEBIL a la
     * lista original: si nadie sostiene el envoltorio, el recolector se lo
     * lleva y la vista deja de enterarse de los cambios, en silencio y para
     * siempre.
     */
    private final ObservableList<PlayerStatsRow> readOnlyPlayers =
            FXCollections.unmodifiableObservableList(players);

    private final StringProperty status = new SimpleStringProperty(this, "status", EMPTY_MESSAGE);

    /** Solo lectura: la vista muestra los jugadores, no los modifica. */
    public ObservableList<PlayerStatsRow> getPlayers() {
        return readOnlyPlayers;
    }

    public void setPlayers(List<PlayerStatsRow> newPlayers) {
        players.setAll(newPlayers);
    }

    /** Mensaje que se muestra cuando todavia no hay jugadores. */
    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String text) {
        status.set(text);
    }
}
