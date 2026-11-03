package model.competition;

import model.match.Match;
import model.people.Referee;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RefereeReportService {

    private static final Comparator<RefereeReportEntry> RANKING_CRITERIA =
            Comparator.comparingInt(RefereeReportEntry::getMatchesOfficiated).reversed()
                    .thenComparing(entry -> entry.getReferee().getYears(), Comparator.reverseOrder())
                    .thenComparing(entry -> entry.getReferee().getName(), String.CASE_INSENSITIVE_ORDER);

    public List<RefereeReportEntry> computeRanking(List<Referee> referees, List<? extends Match> matches) {
        Objects.requireNonNull(referees, "The referee list is required");
        Objects.requireNonNull(matches, "The match list is required");

        Map<Referee, RefereeReportEntry> entries = new LinkedHashMap<>();
        for (Referee referee : referees) {
            entries.put(referee, new RefereeReportEntry(referee));
        }
        for (Match match : matches) {
            RefereeReportEntry entry = entries.get(match.getReferee());
            if (match.isPlayed() && entry != null) {
                entry.registerMatch();
            }
        }

        List<RefereeReportEntry> ranking = new ArrayList<>(entries.values());
        ranking.sort(RANKING_CRITERIA);
        for (int index = 0; index < ranking.size(); index++) {
            ranking.get(index).setPosition(index + 1);
        }
        return ranking;
    }

    public double averageYears(List<Referee> referees) {
        Objects.requireNonNull(referees, "The referee list is required");
        double average = 0;
        if (!referees.isEmpty()) {
            int total = 0;
            for (Referee referee : referees) {
                total += referee.getYears();
            }
            average = (double) total / referees.size();
        }
        return average;
    }
}