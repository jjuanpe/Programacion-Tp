package ui.fx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class MatchesViewModel {

    private final ObservableList<MatchDetail> matches = FXCollections.observableArrayList();

    private final ObservableList<MatchDetail> readOnlyMatches =
            FXCollections.unmodifiableObservableList(matches);

    private final StringProperty status =
            new SimpleStringProperty(this, "status", "No matches yet");

    public ObservableList<MatchDetail> getMatches() {
        return readOnlyMatches;
    }

    public void setMatches(List<MatchDetail> newMatches) {
        matches.setAll(newMatches);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String text) {
        status.set(text);
    }
}
