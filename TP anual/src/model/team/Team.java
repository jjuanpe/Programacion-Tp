package model.team;

import model.people.HeadCoach;
import model.people.Player;
import model.people.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class Team {
    private final String name;
    private final Country country;
    private final int ranking;
    private final List<Player> players;
    private HeadCoach headCoach;

    public Team(String name, Country country, int ranking) {
        if (ranking <= 0) {
            throw new IllegalArgumentException("The team ranking must be positive");
        }
        this.name = Objects.requireNonNull(name, "The team name is required");
        this.country = Objects.requireNonNull(country, "The team country is required");
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
        return List.copyOf(players);
    }

    public HeadCoach getHeadCoach() {
        return headCoach;
    }

    public void setCoach(HeadCoach headCoach) {
        this.headCoach = Objects.requireNonNull(headCoach, "The head coach is required");
    }

    public void addPlayer(Player player) {
        Objects.requireNonNull(player, "The player is required");
        if (players.contains(player)) {
            throw new IllegalArgumentException("A player cannot be added twice to a team");
        }
        players.add(player);
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
