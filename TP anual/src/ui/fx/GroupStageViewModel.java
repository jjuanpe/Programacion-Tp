package ui.fx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class GroupStageViewModel {

    private static final String EMPTY_MESSAGE = "No group draw yet";

    private final ObservableList<GroupZone> zones = FXCollections.observableArrayList();

    private final ObservableList<GroupZone> readOnlyZones =
            FXCollections.unmodifiableObservableList(zones);

    private final StringProperty status = new SimpleStringProperty(this, "status", EMPTY_MESSAGE);

    public ObservableList<GroupZone> getZones() {
        return readOnlyZones;
    }

    public void setZones(List<GroupZone> newZones) {
        zones.setAll(newZones);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String text) {
        status.set(text);
    }
}
