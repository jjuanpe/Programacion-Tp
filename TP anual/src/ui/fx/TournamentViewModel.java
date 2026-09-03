package ui.fx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Estado observable de la pagina Tournament.
 *
 * Guarda tanto los datos del campeonato como que acciones estan habilitadas.
 * La vista solo ata botones y etiquetas a estas propiedades: no decide nada
 * sobre el ciclo de vida del torneo.
 */
public class TournamentViewModel {

    private final StringProperty state = new SimpleStringProperty(this, "state", "");
    private final StringProperty message = new SimpleStringProperty(this, "message", "");
    private final StringProperty advanceLabel =
            new SimpleStringProperty(this, "advanceLabel", "Start tournament");

    private final IntegerProperty teams = new SimpleIntegerProperty(this, "teams");
    private final IntegerProperty referees = new SimpleIntegerProperty(this, "referees");
    private final IntegerProperty zones = new SimpleIntegerProperty(this, "zones");
    private final IntegerProperty matchesPlayed = new SimpleIntegerProperty(this, "matchesPlayed");
    private final IntegerProperty totalMatches = new SimpleIntegerProperty(this, "totalMatches");
    private final IntegerProperty warnings = new SimpleIntegerProperty(this, "warnings");

    private final BooleanProperty busy = new SimpleBooleanProperty(this, "busy");
    private final BooleanProperty canImport = new SimpleBooleanProperty(this, "canImport", true);
    private final BooleanProperty canDraw = new SimpleBooleanProperty(this, "canDraw");
    private final BooleanProperty canAdvance = new SimpleBooleanProperty(this, "canAdvance");
    private final BooleanProperty canSave = new SimpleBooleanProperty(this, "canSave");

    public StringProperty stateProperty() { return state; }

    public void setState(String value) { state.set(value); }

    /** Resultado de la ultima accion, o el aviso de que hay una en curso. */
    public StringProperty messageProperty() { return message; }

    public void setMessage(String value) { message.set(value); }

    /** Texto del boton de avance: cambia segun lo que toque hacer. */
    public StringProperty advanceLabelProperty() { return advanceLabel; }

    public void setAdvanceLabel(String value) { advanceLabel.set(value); }

    public IntegerProperty teamsProperty() { return teams; }

    public void setTeams(int value) { teams.set(value); }

    public IntegerProperty refereesProperty() { return referees; }

    public void setReferees(int value) { referees.set(value); }

    public IntegerProperty zonesProperty() { return zones; }

    public void setZones(int value) { zones.set(value); }

    public IntegerProperty matchesPlayedProperty() { return matchesPlayed; }

    public void setMatchesPlayed(int value) { matchesPlayed.set(value); }

    public IntegerProperty totalMatchesProperty() { return totalMatches; }

    public void setTotalMatches(int value) { totalMatches.set(value); }

    public IntegerProperty warningsProperty() { return warnings; }

    public void setWarnings(int value) { warnings.set(value); }

    public BooleanProperty busyProperty() { return busy; }

    public boolean isBusy() { return busy.get(); }

    public void setBusy(boolean value) { busy.set(value); }

    public BooleanProperty canImportProperty() { return canImport; }

    public void setCanImport(boolean value) { canImport.set(value); }

    public BooleanProperty canDrawProperty() { return canDraw; }

    public void setCanDraw(boolean value) { canDraw.set(value); }

    public BooleanProperty canAdvanceProperty() { return canAdvance; }

    public void setCanAdvance(boolean value) { canAdvance.set(value); }

    public BooleanProperty canSaveProperty() { return canSave; }

    public void setCanSave(boolean value) { canSave.set(value); }
}
