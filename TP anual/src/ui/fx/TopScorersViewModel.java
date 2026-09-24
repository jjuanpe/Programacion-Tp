package ui.fx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class TopScorersViewModel {

    private final ObservableList<ScorerRow> rows = FXCollections.observableArrayList();

    private final ObservableList<ScorerRow> readOnlyRows =
            FXCollections.unmodifiableObservableList(rows);

    private final StringProperty status =
            new SimpleStringProperty(this, "status", "No report generated yet");

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
