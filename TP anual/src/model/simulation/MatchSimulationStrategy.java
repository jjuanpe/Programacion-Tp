package model.simulation;

import model.match.Match;
import model.people.Player;

import java.util.Random;
import java.util.Set;

public interface MatchSimulationStrategy<T extends Match> {
    MatchSimulationResult simulate(T match, Set<Player> suspendedPlayers, Random random);
}
