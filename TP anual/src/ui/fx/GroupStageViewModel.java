package ui.fx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Estado observable de la pagina Group Stage: las cuatro zonas con su tabla de
 * posiciones y su fixture.
 */
public class GroupStageViewModel {

    private static final String EMPTY_MESSAGE = "No group draw yet";

    private final ObservableList<GroupZone> zones = FXCollections.observableArrayList();

    /*
     * El envoltorio de solo lectura se guarda en un campo a proposito.
     * FXCollections.unmodifiableObservableList engancha un listener DEBIL a la
     * lista original: si nadie sostiene el envoltorio, el recolector se lo
     * lleva y la vista deja de enterarse de los cambios, en silencio y para
     * siempre. Devolver uno nuevo en cada llamada provocaba justamente eso.
     */
    private final ObservableList<GroupZone> readOnlyZones =
            FXCollections.unmodifiableObservableList(zones);

    private final StringProperty status = new SimpleStringProperty(this, "status", EMPTY_MESSAGE);

    /** Solo lectura: la vista muestra las zonas, no las modifica. */
    public ObservableList<GroupZone> getZones() {
        return readOnlyZones;
    }

    public void setZones(List<GroupZone> newZones) {
        zones.setAll(newZones);
    }

    /** Mensaje que se muestra mientras no hay sorteo. */
    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String text) {
        status.set(text);
    }
}
