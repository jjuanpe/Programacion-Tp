package ui.fx;

public final class BracketTie {

    private final String title;
    private final String teamA;
    private final String teamB;
    private final String score;
    private final String winner;
    private final String criteria;

    public BracketTie(String title, String teamA, String teamB, String score, String winner, String criteria) {
        this.title = title;
        this.teamA = teamA;
        this.teamB = teamB;
        this.score = score;
        this.winner = winner;
        this.criteria = criteria;
    }

    public static BracketTie pending(String title, String teamA, String teamB) {
        return new BracketTie(title, teamA, teamB, "Not played yet", "", "");
    }

    public String getTitle() { return title; }

    public String getTeamA() { return teamA; }

    public String getTeamB() { return teamB; }

    public String getScore() { return score; }

    public String getWinner() { return winner; }

    public String getCriteria() { return criteria; }

    public boolean isPlayed() { return !winner.isEmpty(); }
}

