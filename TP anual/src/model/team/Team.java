package model.team;

import model.people.HeadCoach;
import model.people.Player;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String name;
    private Country country;
    private int ranking;
    private List<Player> players;
    private HeadCoach headCoach;

    public Team(String name, Country country, int ranking, HeadCoach headCoach) {
        this.name = name;
        this.country = country;
        this.ranking = ranking;
        this.players = new ArrayList<>();
        this.headCoach = headCoach;
    }

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

    void addPlayer(Player p){}
}
