package model;

import java.util.List;

public class Team {
    private String name;
    private Country country;
    private int ranking;
    private List<Player> players;
    private HeadCoach headCoach;

    public String getName() {
        return name;
    }

    public Country getCountry() {
        return country;
    }

    public int getRanking() {
        return ranking;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public HeadCoach getHeadCoach() {
        return headCoach;
    }
}
