package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class StatisticsViewModel {

    private final ObservableList<FairPlayRow> fairPlayRows = FXCollections.observableArrayList();
    private final ObservableList<FairPlayRow> readOnlyFairPlayRows =
            FXCollections.unmodifiableObservableList(fairPlayRows);
    private final ObservableList<MinutesRow> minutesRows = FXCollections.observableArrayList();
    private final ObservableList<MinutesRow> readOnlyMinutesRows =
            FXCollections.unmodifiableObservableList(minutesRows);
    private final ObservableList<RefereeRow> refereeRows = FXCollections.observableArrayList();
    private final ObservableList<RefereeRow> readOnlyRefereeRows =
            FXCollections.unmodifiableObservableList(refereeRows);

    private final StringProperty status = new SimpleStringProperty(this, "status", "");
    private final StringProperty refereeAverage = new SimpleStringProperty(this, "refereeAverage", "");

    public ObservableList<FairPlayRow> getFairPlayRows() {
        return readOnlyFairPlayRows;
    }

    public ObservableList<MinutesRow> getMinutesRows() {
        return readOnlyMinutesRows;
    }

    public ObservableList<RefereeRow> getRefereeRows() {
        return readOnlyRefereeRows;
    }

    public void setFairPlayRows(List<FairPlayRow> rows) {
        fairPlayRows.setAll(rows);
    }

    public void setMinutesRows(List<MinutesRow> rows) {
        minutesRows.setAll(rows);
    }

    public void setRefereeRows(List<RefereeRow> rows) {
        refereeRows.setAll(rows);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String text) {
        status.set(text);
    }

    public StringProperty refereeAverageProperty() {
        return refereeAverage;
    }

    public void setRefereeAverage(String text) {
        refereeAverage.set(text);
    }
}