package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReportsViewModel {

    private final StringProperty status = new SimpleStringProperty(this, "status", "");

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String text) {
        status.set(text);
    }
}