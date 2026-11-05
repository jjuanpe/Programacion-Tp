package ui.fx;

public enum NavItem {

    DASHBOARD("Dashboard", Icon.HOME),
    TOURNAMENT("Tournament", Icon.TROPHY),
    TEAMS("Teams", Icon.TEAMS),
    GROUP_STAGE("Group Stage", Icon.GRID),
    MATCHES("Matches", Icon.BALL),
    KNOCKOUT_STAGE("Knockout Stage", Icon.BRACKET),
    PEOPLE("People", Icon.PERSON),
    STADIUMS("Stadiums", Icon.STADIUM),
    CITIES("Cities", Icon.CITY),
    STATISTICS("Statistics", Icon.CHART),
    REPORTS("Reports", Icon.DOCUMENT);

    private final String label;
    private final Icon icon;

    NavItem(String label, Icon icon) {
        this.label = label;
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }

    public String getLabel() {
        return label;
    }
}
