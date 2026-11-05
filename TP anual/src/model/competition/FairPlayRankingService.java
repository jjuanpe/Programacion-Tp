package model.competition;

import model.event.Incidence;
import model.match.Match;
import model.people.Player;
import model.team.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FairPlayRankingService {

    private static final Comparator<FairPlayEntry> RANKING_CRITERIA =
            Comparator.comparingInt(FairPlayEntry::getPoints)
                    .thenComparingInt(FairPlayEntry::getExpulsions)
                    .thenComparing(entry -> entry.getTeam().getName(), String.CASE_INSENSITIVE_ORDER);

    private final FairPlayCalculator fairPlayCalculator;

    public FairPlayRankingService() {
        this(new FairPlayCalculator());
    }

    public FairPlayRankingService(FairPlayCalculator fairPlayCalculator) {
        this.fairPlayCalculator = Objects.requireNonNull(fairPlayCalculator, "The calculator is required");
    }

    public List<FairPlayEntry> computeRanking(List<Team> teams, List<? extends Match> matches) {
        Objects.requireNonNull(teams, "The team list is required");
        Objects.requireNonNull(matches, "The match list is required");

        Map<Team, FairPlayEntry> entries = new LinkedHashMap<>();
        Map<Player, FairPlayEntry> entriesByPlayer = new HashMap<>();
        for (Team team : teams) {
            FairPlayEntry entry = new FairPlayEntry(team);
            entries.put(team, entry);
            for (Player player : team.getPlayers()) {
                entriesByPlayer.put(player, entry);
            }
        }

        for (Match match : matches) {
            if (match.isPlayed()) {
                countCards(match, entriesByPlayer);
            }
        }
        for (FairPlayEntry entry : entries.values()) {
            entry.setPoints(fairPlayCalculator.calculate(entry.getTeam(), matches));
        }

        List<FairPlayEntry> ranking = new ArrayList<>(entries.values());
        ranking.sort(RANKING_CRITERIA);
        for (int index = 0; index < ranking.size(); index++) {
            ranking.get(index).setPosition(index + 1);
        }
        return ranking;
    }

    private void countCards(Match match, Map<Player, FairPlayEntry> entriesByPlayer) {
        for (Incidence incidence : match.getIncidences()) {
            incidence.registerCardIn(entriesByPlayer);
        }
    }
}