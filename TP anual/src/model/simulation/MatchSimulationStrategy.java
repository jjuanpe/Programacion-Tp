package model.simulation;

import model.match.Match;
import model.people.Player;

import java.util.Random;
import java.util.Set;

// Strategy: the way to play a match can change without touching who calls it.
public interface MatchSimulationStrategy<T extends Match> {
    MatchSimulationResult simulate(T match, Set<Player> suspendedPlayers, Random random);
}
