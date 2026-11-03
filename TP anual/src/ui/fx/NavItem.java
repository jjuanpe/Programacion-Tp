package ui.fx;

public enum NavItem {

    DASHBOARD("Dashboard"),
    TOURNAMENT("Tournament"),
    TEAMS("Teams"),
    GROUP_STAGE("Group Stage"),
    MATCHES("Matches"),
    PEOPLE("People"),
    STADIUMS("Stadiums");

    private final String label;

    NavItem(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
