package ui.fx;

public enum TournamentStage {

    GROUP_STAGE("Group Stage"),
    QUARTERFINALS("Quarterfinals"),
    SEMIFINALS("Semifinals"),
    FINAL("Final");

    private final String label;

    TournamentStage(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
