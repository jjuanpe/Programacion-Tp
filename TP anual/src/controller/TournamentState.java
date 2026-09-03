package controller;

/**
 * En que punto de su ciclo de vida esta el campeonato.
 *
 * El estado no se guarda en ningun campo: se deduce de lo que hay cargado y
 * jugado (ver {@link TournamentSession#getState()}), asi no puede quedar
 * desincronizado con los datos reales.
 */
public enum TournamentState {

    /** Todavia no se importo ningun archivo de datos. */
    EMPTY("No data imported"),

    /** Datos importados; falta el sorteo de grupos. */
    DATA_LOADED("Data imported - group draw pending"),

    /** Sorteo hecho; la fase de grupos todavia no se jugo. */
    GROUPS_DRAWN("Group stage pending"),

    /** Fase de grupos jugada; falta la fase eliminatoria. */
    GROUP_STAGE_PLAYED("Knockout stage pending"),

    /** Campeonato terminado: hay campeon. */
    FINISHED("Tournament finished");

    private final String label;

    TournamentState(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
