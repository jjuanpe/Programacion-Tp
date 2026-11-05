package controller;

import model.people.HeadCoach;
import model.people.Person;
import model.people.Player;
import model.people.Referee;
import model.team.Team;
import report.IdCard;
import report.IdCardPdfExporter;
import ui.fx.ReportsViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ReportsController {

    private static final File PHOTOS_FOLDER = new File("photos");

    private final TournamentSession session;
    private final ReportsViewModel viewModel;
    private final IdCardPdfExporter idCardExporter;

    public ReportsController(TournamentSession session, ReportsViewModel viewModel) {
        this.session = Objects.requireNonNull(session, "The tournament session is required");
        this.viewModel = Objects.requireNonNull(viewModel, "The view model is required");
        this.idCardExporter = new IdCardPdfExporter(PHOTOS_FOLDER);
    }

    public void exportIdCards(File output) {
        Objects.requireNonNull(output, "The output file is required");
        try {
            List<IdCard> cards = buildIdCards();
            idCardExporter.export(cards, output);
            viewModel.setStatus(new StringBuilder().append(cards.size())
                    .append(" identification cards saved to ").append(output.getName()).toString());
        } catch (IOException | RuntimeException exception) {
            viewModel.setStatus(new StringBuilder("The identification cards could not be generated: ")
                    .append(exception.getMessage()).toString());
        }
    }

    private List<IdCard> buildIdCards() {
        List<Team> teams = new ArrayList<>(session.getTeams());
        teams.sort(Comparator.comparing(Team::getName, String.CASE_INSENSITIVE_ORDER));

        List<IdCard> cards = new ArrayList<>();
        for (Team team : teams) {
            HeadCoach coach = team.getHeadCoach();
            if (coach != null) {
                cards.add(toCard(coach, List.of(
                        labelled("Team", team.getName()),
                        labelled("Nationality", coach.getCountry().getCountryName()),
                        labelled("Titles won", coach.getTitlesWon()))));
            }
            for (Player player : team.getPlayers()) {
                cards.add(toCard(player, List.of(
                        labelled("Team", new StringBuilder(team.getName()).append(" (")
                                .append(team.getCountry().getCountryName()).append(')')),
                        labelled("Position", player.getPosition().getLabel()),
                        labelled("Rating", player.getAverage()))));
            }
        }
        for (Referee referee : session.getReferees()) {
            cards.add(toCard(referee, List.of(
                    labelled("Nationality", referee.getCountry().getCountryName()),
                    labelled("Years refereeing", referee.getYears()))));
        }
        return cards;
    }

    private IdCard toCard(Person person, List<String> details) {
        String document = new StringBuilder(person.getDocumentType().name()).append(' ')
                .append(person.getDocument()).toString();
        String barcode = new StringBuilder(person.getDocumentType().name()).append('-')
                .append(person.getDocument()).toString();
        return new IdCard(person.getName(), person.getRole(), document, person.getBirthDate(), details, barcode);
    }

    private String labelled(String label, Object value) {
        return new StringBuilder(label).append(": ").append(value).toString();
    }
}
