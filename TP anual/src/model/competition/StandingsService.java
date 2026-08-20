package model.competition;

import model.match.GroupMatch;
import model.team.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StandingsService {
    private static final Comparator<StandingEntry> PRIMARY_CRITERIA =
            Comparator.comparingInt(StandingEntry::getPoints).reversed()
                    .thenComparing(Comparator.comparingInt(
                            StandingEntry::getGoalDifference).reversed())
                    .thenComparing(Comparator.comparingInt(
                            StandingEntry::getGoalsFor).reversed());

    private static final Comparator<StandingEntry> FAIR_PLAY_AND_RANKING =
            Comparator.comparingInt(StandingEntry::getFairPlayPoints)
                    .thenComparingInt(entry -> entry.getTeam().getRanking());

    private final FairPlayCalculator fairPlayCalculator;

    public StandingsService() {
        this(new FairPlayCalculator());
    }

    public StandingsService(FairPlayCalculator fairPlayCalculator) {
        this.fairPlayCalculator = Objects.requireNonNull(fairPlayCalculator);
    }

    public List<StandingEntry> computeStandings(Zone zone) {
        Objects.requireNonNull(zone, "The zone is required");
        Map<Team, StandingEntry> standings = new LinkedHashMap<>();
        for (Team team : zone.getTeams()) {
            standings.put(team, new StandingEntry(team));
        }

        for (GroupMatch match : zone.getGroupMatches()) {
            if (!match.isPlayed()) {
                continue;
            }

            Team homeTeam = match.getHomeTeam();
            Team awayTeam = match.getAwayTeam();
            int homeGoals = match.getHomeGoals();
            int awayGoals = match.getAwayGoals();

            StandingEntry homeEntry = standings.get(homeTeam);
            StandingEntry awayEntry = standings.get(awayTeam);

            if (homeGoals > awayGoals) {
                homeEntry.registerWin(homeGoals, awayGoals);
                awayEntry.registerLoss(awayGoals, homeGoals);
            } else if (homeGoals < awayGoals) {
                homeEntry.registerLoss(homeGoals, awayGoals);
                awayEntry.registerWin(awayGoals, homeGoals);
            } else {
                homeEntry.registerDraw(homeGoals, awayGoals);
                awayEntry.registerDraw(awayGoals, homeGoals);
            }
        }

        List<StandingEntry> result = new ArrayList<>(standings.values());
        for (StandingEntry entry : result) {
            entry.setFairPlayPoints(fairPlayCalculator.calculate(zone, entry.getTeam()));
        }
        result.sort(PRIMARY_CRITERIA);
        applyRemainingTieBreakers(result, zone);
        return result;
    }

    private void applyRemainingTieBreakers(List<StandingEntry> standings, Zone zone) {
        int groupStart = 0;
        while (groupStart < standings.size()) {
            int groupEnd = groupStart + 1;
            while (groupEnd < standings.size()
                    && hasSamePrimaryCriteria(standings.get(groupStart), standings.get(groupEnd))) {
                groupEnd++;
            }

            List<StandingEntry> tiedEntries = standings.subList(groupStart, groupEnd);
            if (tiedEntries.size() == 2) {
                int headToHeadOrder = compareHeadToHead(tiedEntries.get(0), tiedEntries.get(1), zone);
                if (headToHeadOrder > 0) {
                    StandingEntry firstEntry = tiedEntries.get(0);
                    tiedEntries.set(0, tiedEntries.get(1));
                    tiedEntries.set(1, firstEntry);
                } else if (headToHeadOrder == 0) {
                    tiedEntries.sort(FAIR_PLAY_AND_RANKING);
                }
            } else if (tiedEntries.size() > 2) {
                tiedEntries.sort(FAIR_PLAY_AND_RANKING);
            }
            groupStart = groupEnd;
        }
    }

    private boolean hasSamePrimaryCriteria(StandingEntry first, StandingEntry second) {
        return first.getPoints() == second.getPoints()
                && first.getGoalDifference() == second.getGoalDifference()
                && first.getGoalsFor() == second.getGoalsFor();
    }

    private int compareHeadToHead(
            StandingEntry first,
            StandingEntry second,
            Zone zone) {
        Team firstTeam = first.getTeam();
        Team secondTeam = second.getTeam();
        for (GroupMatch match : zone.getGroupMatches()) {
            if (!match.isPlayed() || !isMatchBetween(match, firstTeam, secondTeam)) {
                continue;
            }
            if (match.getHomeGoals() == match.getAwayGoals()) {
                return 0;
            }
            Team winner = match.getHomeGoals() > match.getAwayGoals()
                    ? match.getHomeTeam()
                    : match.getAwayTeam();
            return winner == firstTeam ? -1 : 1;
        }
        return 0;
    }

    private boolean isMatchBetween(GroupMatch match, Team firstTeam, Team secondTeam) {
        return match.getHomeTeam() == firstTeam && match.getAwayTeam() == secondTeam
                || match.getHomeTeam() == secondTeam && match.getAwayTeam() == firstTeam;
    }
}
