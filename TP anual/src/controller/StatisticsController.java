package controller;

import model.competition.FairPlayEntry;
import model.competition.FairPlayRankingService;
import model.competition.PlayerReportEntry;
import model.competition.PlayerReportService;
import model.competition.RefereeReportEntry;
import model.competition.RefereeReportService;
import model.match.Match;
import model.people.Referee;
import model.team.Team;
import ui.fx.FairPlayRow;
import ui.fx.MinutesRow;
import ui.fx.RefereeRow;
import ui.fx.StatisticsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class StatisticsController {

    private static final String NO_DATA_MESSAGE = "No tournament data imported yet";

    private final TournamentSession session;
    private final StatisticsViewModel viewModel;
    private final FairPlayRankingService fairPlayRankingService;
    private final PlayerReportService playerReportService;
    private final RefereeReportService refereeReportService;

    public StatisticsController(TournamentSession session, StatisticsViewModel viewModel) {
        this.session = Objects.requireNonNull(session, "The tournament session is required");
        this.viewModel = Objects.requireNonNull(viewModel, "The view model is required");
        this.fairPlayRankingService = new FairPlayRankingService();
        this.playerReportService = new PlayerReportService();
        this.refereeReportService = new RefereeReportService();
    }

    public void refresh() {
        List<Team> teams = session.getTeams();
        List<Referee> referees = session.getReferees();
        List<Match> matches = session.getMatches();

        viewModel.setFairPlayRows(toFairPlayRows(fairPlayRankingService.computeRanking(teams, matches)));
        viewModel.setMinutesRows(toMinutesRows(playerReportService.computeMinutesRanking(teams, matches)));
        viewModel.setRefereeRows(toRefereeRows(refereeReportService.computeRanking(referees, matches)));
        viewModel.setRefereeAverage(referees.isEmpty() ? "" : "Average years refereeing: "
                + String.format(Locale.US, "%.1f", refereeReportService.averageYears(referees)));
        viewModel.setStatus(teams.isEmpty() ? NO_DATA_MESSAGE : "No matches played yet");
    }

    private List<FairPlayRow> toFairPlayRows(List<FairPlayEntry> ranking) {
        List<FairPlayRow> rows = new ArrayList<>();
        for (FairPlayEntry entry : ranking) {
            rows.add(new FairPlayRow(
                    entry.getPosition(),
                    entry.getTeam().getName(),
                    entry.getYellowCards(),
                    entry.getExpulsions(),
                    entry.getPoints()));
        }
        return rows;
    }

    private List<MinutesRow> toMinutesRows(List<PlayerReportEntry> ranking) {
        List<MinutesRow> rows = new ArrayList<>();
        for (int index = 0; index < ranking.size(); index++) {
            PlayerReportEntry entry = ranking.get(index);
            rows.add(new MinutesRow(
                    index + 1,
                    entry.getPlayer().getName(),
                    entry.getTeam().getName(),
                    entry.getPlayer().getPosition().getLabel(),
                    entry.getMatchesPlayed(),
                    entry.getMinutesPlayed()));
        }
        return rows;
    }

    private List<RefereeRow> toRefereeRows(List<RefereeReportEntry> ranking) {
        List<RefereeRow> rows = new ArrayList<>();
        for (RefereeReportEntry entry : ranking) {
            Referee referee = entry.getReferee();
            rows.add(new RefereeRow(
                    entry.getPosition(),
                    referee.getName(),
                    referee.getCountry().getCountryName(),
                    entry.getMatchesOfficiated(),
                    referee.getYears()));
        }
        return rows;
    }
}