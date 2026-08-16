package model.team;

import model.people.HeadCoach;
import model.people.Player;
import model.people.Position;

import java.util.ArrayList;
import java.util.List;



public class Team {
    private String name;
    private Country country;
    private int ranking;
    private List<Player> players;
    private HeadCoach headCoach;

    public Team(String name, Country country, int ranking) {
        this.name = name;
        this.country = country;
        this.ranking = ranking;
        this.players = new ArrayList<>();
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

    public void setCoach(HeadCoach headCoach) {
        this.headCoach = headCoach;
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public int countByPosition(Position position) {
        int count = 0;
        for (Player player : players) {
            if (player.getPosition() == position) {
                count++;
            }
        }
        return count;
    }

    public void validateSquad() {
        if (players.size() != 18) {
            throw new IllegalStateException(name + ": has " + players.size() + " players instead of 18");
        }
        for (Position position : Position.values()) {
            int expected = position.getRequiredPerSquad();
            int actual = countByPosition(position);
            if (actual != expected) {
                throw new IllegalStateException(
                        name + ": has " + actual + " " + position + "s instead of " + expected);
            }
        }
    }
}
