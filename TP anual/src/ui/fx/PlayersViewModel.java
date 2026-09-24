package ui.fx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class PlayersViewModel {

    private static final String EMPTY_MESSAGE = "No players imported yet";

    private final ObservableList<PlayerStatsRow> players = FXCollections.observableArrayList();

    private final ObservableList<PlayerStatsRow> readOnlyPlayers =
            FXCollections.unmodifiableObservableList(players);

    private final StringProperty status = new SimpleStringProperty(this, "status", EMPTY_MESSAGE);

    public ObservableList<PlayerStatsRow> getPlayers() {
        return readOnlyPlayers;
    }

    public void setPlayers(List<PlayerStatsRow> newPlayers) {
        players.setAll(newPlayers);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String text) {
        status.set(text);
    }
}
