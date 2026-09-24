package ui.fx;

/**
 * Etapas del torneo, en orden de avance.
 *
 * El orden de declaracion es significativo: es lo que permite deducir que
 * etapas quedaron completadas y cuales siguen pendientes a partir de la etapa
 * actual. Si en algun momento se agrega una etapa (por ejemplo octavos), va
 * insertada en la posicion que le corresponde.
 *
 * No hay una etapa separada para las semifinales: TournamentSession juega
 * toda la eliminatoria (cuartos, semis y final) en una sola llamada
 * sincronica, sin pausas entre medio, asi que el torneo nunca queda "a
 * mitad" de la eliminatoria de forma observable. Agregar esa etapa acá sin
 * que exista un estado real que la represente la dejaria inalcanzable, y el
 * stepper saltearia visualmente de Quarterfinals a Final. Si en un proximo
 * sprint la eliminatoria pasa a jugarse por pasos (ligado a la persistencia
 * del torneo), esta etapa se puede reintroducir junto con el estado nuevo
 * que la respalde.
 *
 * Igual que {@link NavItem}, es un enum de interfaz: no conoce ninguna clase
 * del dominio.
 */
public enum TournamentStage {

    GROUP_STAGE("Group Stage"),
    QUARTERFINALS("Quarterfinals"),
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
