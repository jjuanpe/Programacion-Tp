package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class KnockoutViewModel {

    private final ObservableList<QualifiedRow> qualified = FXCollections.observableArrayList();
    private final ObservableList<BracketTie> quarterFinals = FXCollections.observableArrayList();
    private final ObservableList<BracketTie> semiFinals = FXCollections.observableArrayList();
    private final ObservableList<BracketTie> finals = FXCollections.observableArrayList();

    private final StringProperty champion = new SimpleStringProperty(this, "champion", "");
    private final StringProperty status = new SimpleStringProperty(this, "status", "");

    public ObservableList<QualifiedRow> getQualified() { return qualified; }

    public ObservableList<BracketTie> getQuarterFinals() { return quarterFinals; }

    public ObservableList<BracketTie> getSemiFinals() { return semiFinals; }

    public ObservableList<BracketTie> getFinals() { return finals; }

    public void setBracket(
            List<QualifiedRow> qualifiedRows,
            List<BracketTie> quarterFinalTies,
            List<BracketTie> semiFinalTies,
            List<BracketTie> finalTies) {
        qualified.setAll(qualifiedRows);
        quarterFinals.setAll(quarterFinalTies);
        semiFinals.setAll(semiFinalTies);
        finals.setAll(finalTies);
    }

    public StringProperty championProperty() { return champion; }

    public void setChampion(String text) { champion.set(text); }

    public StringProperty statusProperty() { return status; }

    public void setStatus(String text) { status.set(text); }
}
