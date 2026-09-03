package ui.fx;

/**
 * Opciones de navegacion del sidebar.
 * Es un enum puramente de interfaz: no conoce ninguna clase del dominio,
 * asi la barra lateral queda desacoplada de la logica del campeonato.
 */
public enum NavItem {

    DASHBOARD("Dashboard"),
    TOURNAMENT("Tournament"),
    TEAMS("Teams"),
    GROUP_STAGE("Group Stage"),
    MATCHES("Matches"),
    KNOCKOUT_STAGE("Knockout Stage"),
    PEOPLE("People"),
    STADIUMS("Stadiums"),
    STATISTICS("Statistics"),
    REPORTS("Reports");

    private final String label;

    NavItem(String label) {
        this.label = label;
    }

    /** Texto visible en la interfaz (siempre en ingles). */
    public String getLabel() {
        return label;
    }
}
