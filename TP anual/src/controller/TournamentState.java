package controller;

public enum TournamentState {

    EMPTY("No data imported"),

    DATA_LOADED("Data imported - group draw pending"),

    GROUPS_DRAWN("Group stage pending"),

    GROUP_STAGE_PLAYED("Quarterfinals pending"),

    QUARTER_FINALS_PLAYED("Semifinals pending"),

    SEMI_FINALS_PLAYED("Final pending"),

    FINISHED("Tournament finished");

    private final String label;

    TournamentState(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
