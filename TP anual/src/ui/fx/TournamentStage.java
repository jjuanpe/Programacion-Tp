package ui.fx;

/**
 * Etapas del torneo, en orden de avance.
 *
 * El orden de declaracion es significativo: es lo que permite deducir que
 * etapas quedaron completadas y cuales siguen pendientes a partir de la etapa
 * actual. Si en algun momento se agrega una etapa (por ejemplo octavos), va
 * insertada en la posicion que le corresponde.
 *
 * Igual que {@link NavItem}, es un enum de interfaz: no conoce ninguna clase
 * del dominio.
 */
public enum TournamentStage {

    GROUP_STAGE("Group Stage"),
    QUARTERFINALS("Quarterfinals"),
    SEMIFINALS("Semifinals"),
    FINAL("Final");

    private final String label;

    TournamentStage(String label) {
        this.label = label;
    }

    /** Texto visible en la interfaz (siempre en ingles). */
    public String getLabel() {
        return label;
    }
}
