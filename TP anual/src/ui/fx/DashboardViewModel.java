package ui.fx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class DashboardViewModel {

    private final IntegerProperty teams = new SimpleIntegerProperty(this, "teams");
    private final IntegerProperty matchesPlayed = new SimpleIntegerProperty(this, "matchesPlayed");
    private final IntegerProperty goals = new SimpleIntegerProperty(this, "goals");

    private final ObjectProperty<TournamentStage> currentStage =
            new SimpleObjectProperty<>(this, "currentStage");

    private final StringProperty stageLabel = new SimpleStringProperty(this, "stageLabel", "");

    public IntegerProperty teamsProperty() {
        return teams;
    }

    public int getTeams() {
        return teams.get();
    }

    public void setTeams(int value) {
        teams.set(value);
    }

    public IntegerProperty matchesPlayedProperty() {
        return matchesPlayed;
    }

    public int getMatchesPlayed() {
        return matchesPlayed.get();
    }

    public void setMatchesPlayed(int value) {
        matchesPlayed.set(value);
    }

    public IntegerProperty goalsProperty() {
        return goals;
    }

    public int getGoals() {
        return goals.get();
    }

    public void setGoals(int value) {
        goals.set(value);
    }

    public ObjectProperty<TournamentStage> currentStageProperty() {
        return currentStage;
    }

    public TournamentStage getCurrentStage() {
        return currentStage.get();
    }

    public void setCurrentStage(TournamentStage value) {
        currentStage.set(value);
    }

    public StringProperty stageLabelProperty() {
        return stageLabel;
    }

    public void setStageLabel(String text) {
        stageLabel.set(text);
    }
}
