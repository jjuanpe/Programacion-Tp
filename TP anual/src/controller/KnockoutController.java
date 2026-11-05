package controller;

import model.competition.KnockoutBracketService;
import model.competition.StandingEntry;
import model.competition.StandingsService;
import model.competition.Zone;
import model.event.Incidence;
import model.event.PenaltyExecuted;
import model.match.FirstLegMatch;
import model.match.FinalMatch;
import model.match.Match;
import model.match.SecondLegMatch;
import model.match.TieCriterion;
import model.simulation.FinalMatchReport;
import model.simulation.KnockoutStageResult;
import model.simulation.KnockoutTieReport;
import model.team.Team;
import ui.fx.BracketTie;
import ui.fx.KnockoutViewModel;
import ui.fx.QualifiedRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KnockoutController {

    private static final String[] QUARTER_FINAL_NAMES = {"I", "II", "III", "IV"};
    private static final String[] SEMI_FINAL_NAMES = {"V", "VI"};
    private static final String PENALTY_CRITERIA = TieCriterion.PENALTY_SHOOTOUT.getLabel();
    private static final String NO_DRAW_MESSAGE = "The bracket appears after the group draw.";

    private final TournamentSession session;
    private final KnockoutViewModel viewModel;
    private final StandingsService standingsService = new StandingsService();
    private final KnockoutBracketService bracketService = new KnockoutBracketService(standingsService);

    public KnockoutController(TournamentSession session, KnockoutViewModel viewModel) {
        this.session = Objects.requireNonNull(session, "The tournament session is required");
        this.viewModel = Objects.requireNonNull(viewModel, "The view model is required");
    }

    public void refresh() {
        List<Zone> zones = session.getZones();
        boolean groupStagePlayed = session.getState().compareTo(TournamentState.GROUP_STAGE_PLAYED) >= 0;
        List<KnockoutTieReport> quarterFinals = session.getQuarterFinalReports();
        List<KnockoutTieReport> semiFinals = session.getSemiFinalReports();
        KnockoutStageResult result = session.getKnockoutResult();

        if (zones.isEmpty()) {
            viewModel.setBracket(List.of(), List.of(), List.of(), List.of());
        } else {
            viewModel.setBracket(
                    buildQualified(zones, groupStagePlayed),
                    buildQuarterFinals(zones, groupStagePlayed, quarterFinals),
                    buildSemiFinals(quarterFinals, semiFinals),
                    buildFinal(semiFinals, result));
        }
        String champion = "";
        if (result != null) {
            champion = new StringBuilder("Champion: ").append(result.getChampion().getName()).toString();
        }
        viewModel.setChampion(champion);
        viewModel.setStatus(zones.isEmpty() ? NO_DRAW_MESSAGE : "");
    }

    private List<QualifiedRow> buildQualified(List<Zone> zones, boolean groupStagePlayed) {
        List<QualifiedRow> rows = new ArrayList<>();
        for (Zone zone : zones) {
            if (groupStagePlayed) {
                List<StandingEntry> standings = standingsService.computeStandings(zone);
                rows.add(new QualifiedRow(zone.getName(),
                        standings.get(0).getTeam().getName(), standings.get(1).getTeam().getName()));
            } else {
                rows.add(new QualifiedRow(zone.getName(), "To be decided", "To be decided"));
            }
        }
        return rows;
    }

    private List<BracketTie> buildQuarterFinals(
            List<Zone> zones, boolean groupStagePlayed, List<KnockoutTieReport> reports) {
        List<BracketTie> ties = new ArrayList<>();
        if (!reports.isEmpty()) {
            for (int index = 0; index < reports.size(); index++) {
                ties.add(toTie(quarterFinalTitle(index), reports.get(index)));
            }
        } else if (groupStagePlayed) {
            List<Team[]> pairings = bracketService.buildQuarterFinalPairings(zones);
            for (int index = 0; index < pairings.size(); index++) {
                ties.add(BracketTie.pending(quarterFinalTitle(index),
                        pairings.get(index)[0].getName(), pairings.get(index)[1].getName()));
            }
        } else {

            int[][] crossings = {{0, 3}, {1, 2}, {2, 0}, {3, 1}};
            for (int index = 0; index < crossings.length; index++) {
                ties.add(BracketTie.pending(quarterFinalTitle(index),
                        new StringBuilder("1st ").append(zones.get(crossings[index][0]).getName()).toString(),
                        new StringBuilder("2nd ").append(zones.get(crossings[index][1]).getName()).toString()));
            }
        }
        return ties;
    }

    private List<BracketTie> buildSemiFinals(
            List<KnockoutTieReport> quarterFinals, List<KnockoutTieReport> reports) {
        List<BracketTie> ties = new ArrayList<>();
        for (int index = 0; index < SEMI_FINAL_NAMES.length; index++) {
            String title = new StringBuilder("Semi-final ").append(SEMI_FINAL_NAMES[index]).toString();
            if (!reports.isEmpty()) {
                ties.add(toTie(title, reports.get(index)));
            } else {
                ties.add(BracketTie.pending(title,
                        winnerOrPlaceholder(quarterFinals, 2 * index, QUARTER_FINAL_NAMES),
                        winnerOrPlaceholder(quarterFinals, 2 * index + 1, QUARTER_FINAL_NAMES)));
            }
        }
        return ties;
    }

    private List<BracketTie> buildFinal(List<KnockoutTieReport> semiFinals, KnockoutStageResult result) {
        BracketTie tie;
        if (result != null) {
            FinalMatchReport report = result.getFinalMatchReport();
            FinalMatch finalMatch = report.getFinalMatch();
            String score = new StringBuilder()
                    .append(finalMatch.getHomeGoals()).append(" - ").append(finalMatch.getAwayGoals())
                    .append(penaltiesOf(finalMatch, report.getDecidingCriteria(),
                            finalMatch.getHomeTeam(), finalMatch.getAwayTeam()))
                    .toString();
            tie = new BracketTie("Final", finalMatch.getHomeTeam().getName(), finalMatch.getAwayTeam().getName(),
                    score, report.getChampion().getName(), report.getDecidingCriteria());
        } else {
            tie = BracketTie.pending("Final",
                    winnerOrPlaceholder(semiFinals, 0, SEMI_FINAL_NAMES),
                    winnerOrPlaceholder(semiFinals, 1, SEMI_FINAL_NAMES));
        }
        return List.of(tie);
    }

    private BracketTie toTie(String title, KnockoutTieReport report) {
        FirstLegMatch firstLeg = report.getFirstLeg();
        SecondLegMatch secondLeg = report.getSecondLeg();
        Team teamA = firstLeg.getHomeTeam();
        Team teamB = firstLeg.getAwayTeam();

        int goalsA = firstLeg.getHomeGoals() + secondLeg.getAwayGoals();
        int goalsB = firstLeg.getAwayGoals() + secondLeg.getHomeGoals();
        String score = new StringBuilder("1st leg ")
                .append(firstLeg.getHomeGoals()).append('-').append(firstLeg.getAwayGoals())
                .append(" / 2nd leg ").append(secondLeg.getAwayGoals()).append('-').append(secondLeg.getHomeGoals())
                .append(" / Aggregate ").append(goalsA).append('-').append(goalsB)
                .append(penaltiesOf(secondLeg, report.getDecidingCriteria(), teamA, teamB))
                .toString();
        return new BracketTie(title, teamA.getName(), teamB.getName(), score,
                report.getWinner().getName(), report.getDecidingCriteria());
    }

    private String penaltiesOf(Match match, String criteria, Team teamA, Team teamB) {
        String penalties = "";
        if (PENALTY_CRITERIA.equals(criteria)) {
            penalties = new StringBuilder(" (pens ")
                    .append(match.getShootoutGoals(teamA)).append('-')
                    .append(match.getShootoutGoals(teamB)).append(')').toString();
        }
        return penalties;
    }

    private String winnerOrPlaceholder(List<KnockoutTieReport> reports, int index, String[] names) {
        String winner;
        if (reports.isEmpty()) {
            winner = new StringBuilder("Winner of ").append(names[index]).toString();
        } else {
            winner = reports.get(index).getWinner().getName();
        }
        return winner;
    }

    private String quarterFinalTitle(int index) {
        return new StringBuilder("Quarter-final ").append(QUARTER_FINAL_NAMES[index]).toString();
    }
}