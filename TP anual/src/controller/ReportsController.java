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
            viewModel.setStatus(cards.size() + " identification cards saved to " + output.getName());
        } catch (IOException | RuntimeException exception) {
            viewModel.setStatus("The identification cards could not be generated: " + exception.getMessage());
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
                        "Team: " + team.getName(),
                        "Nationality: " + coach.getCountry().getCountryName(),
                        "Titles won: " + coach.getTitlesWon())));
            }
            for (Player player : team.getPlayers()) {
                cards.add(toCard(player, List.of(
                        "Team: " + team.getName() + " (" + team.getCountry().getCountryName() + ")",
                        "Position: " + player.getPosition().getLabel(),
                        "Rating: " + player.getAverage())));
            }
        }
        for (Referee referee : session.getReferees()) {
            cards.add(toCard(referee, List.of(
                    "Nationality: " + referee.getCountry().getCountryName(),
                    "Years refereeing: " + referee.getYears())));
        }
        return cards;
    }

    private IdCard toCard(Person person, List<String> details) {
        String document = person.getDocumentType().name() + " " + person.getDocument();
        String barcode = person.getDocumentType().name() + "-" + person.getDocument();
        return new IdCard(person.getName(), person.getRole(), document, person.getBirthDate(), details, barcode);
    }
}