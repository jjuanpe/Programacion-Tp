package ui.fx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class TeamsViewModel {

    private static final String EMPTY_MESSAGE = "No teams imported yet";

    private final ObservableList<TeamRow> rows = FXCollections.observableArrayList();
    private final ObservableList<TeamDetail> teams = FXCollections.observableArrayList();

    private final ObservableList<TeamRow> readOnlyRows =
            FXCollections.unmodifiableObservableList(rows);
    private final ObservableList<TeamDetail> readOnlyTeams =
            FXCollections.unmodifiableObservableList(teams);

    private final StringProperty status = new SimpleStringProperty(this, "status", EMPTY_MESSAGE);

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

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String text) {
        status.set(text);
    }
}
