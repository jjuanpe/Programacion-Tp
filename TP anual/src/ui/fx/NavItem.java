package ui.fx;

public enum NavItem {

    DASHBOARD("Dashboard", "\uD83C\uDFE0"),
    TOURNAMENT("Tournament", "\uD83C\uDFC6"),
    TEAMS("Teams", "\uD83D\uDC65"),
    GROUP_STAGE("Group Stage", "\uD83D\uDDC2"),
    MATCHES("Matches", "\u26BD"),
    KNOCKOUT_STAGE("Knockout Stage", "\u2694"),
    PEOPLE("People", "\uD83E\uDDD1"),
    STADIUMS("Stadiums", "\uD83C\uDFDF"),
    CITIES("Cities", "\uD83C\uDFD9"),
    STATISTICS("Statistics", "\uD83D\uDCCA"),
    REPORTS("Reports", "\uD83D\uDCC4");

    private final String label;
    private final String icon;

    NavItem(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public String getLabel() {
        return label;
    }
}
