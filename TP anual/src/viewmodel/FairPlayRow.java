package viewmodel;

public final class FairPlayRow {

    private final int position;
    private final String team;
    private final int yellowCards;
    private final int expulsions;
    private final int points;

    public FairPlayRow(int position, String team, int yellowCards, int expulsions, int points) {
        this.position = position;
        this.team = team;
        this.yellowCards = yellowCards;
        this.expulsions = expulsions;
        this.points = points;
    }

    public int getPosition() { return position; }

    public String getTeam() { return team; }

    public int getYellowCards() { return yellowCards; }

    public int getExpulsions() { return expulsions; }

    public int getPoints() { return points; }
}