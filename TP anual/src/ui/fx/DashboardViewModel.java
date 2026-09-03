package ui.fx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Estado observable del Dashboard.
 *
 * Es la costura entre la interfaz y el dominio: la vista se ata a estas
 * propiedades y se redibuja sola cada vez que cambian, sin saber de donde
 * salen los numeros. Mas adelante un controlador va a completarlas a partir
 * del campeonato real (equipos cargados, partidos jugados, goles convertidos,
 * etapa en curso).
 *
 * Esta clase no importa ninguna clase de {@code model} a proposito: asi el
 * dominio puede cambiar sin arrastrar cambios en la vista, y la vista se puede
 * probar sin levantar un campeonato entero.
 */
public class DashboardViewModel {

    private final IntegerProperty teams = new SimpleIntegerProperty(this, "teams");
    private final IntegerProperty matchesPlayed = new SimpleIntegerProperty(this, "matchesPlayed");
    private final IntegerProperty goals = new SimpleIntegerProperty(this, "goals");

    /** Etapa en curso. {@code null} mientras el campeonato no arranco. */
    private final ObjectProperty<TournamentStage> currentStage =
            new SimpleObjectProperty<>(this, "currentStage");

    /** Texto de contexto del encabezado: la fase en curso, o el estado de la carga. */
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
