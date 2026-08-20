package model.competition;

import model.team.Team;

public class StandingEntry {

    private final Team team;
    private int played;
    private int won;
    private int drawn;
    private int lost;
    private int goalsFor;
    private int goalsAgainst;

    public StandingEntry(Team team) {
        this.team = team;
    }

    void registerWin(int gf, int ga) {
        played++; won++; goalsFor += gf; goalsAgainst += ga;
    }

    void registerDraw(int gf, int ga) {
        played++; drawn++; goalsFor += gf; goalsAgainst += ga;
    }

    void registerLoss(int gf, int ga) {
        played++; lost++; goalsFor += gf; goalsAgainst += ga;
    }

    public Team getTeam() { return team; }
    public int getPlayed() { return played; }
    public int getWon() { return won; }
    public int getDrawn() { return drawn; }
    public int getLost() { return lost; }
    public int getGoalsFor() { return goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }
    public int getGoalDifference() { return goalsFor - goalsAgainst; }
    public int getPoints() { return won * 3 + drawn; }
}
