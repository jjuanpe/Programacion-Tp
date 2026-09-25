package ui.fx;

import controller.DashboardController;
import controller.GroupStageController;
import controller.MatchesController;
import controller.PlayersController;
import controller.TeamsController;
import controller.TournamentController;
import controller.TournamentSession;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class AppWindow extends Application {

    private static final String STYLESHEET = "/ui/fx/styles.css";

    private TournamentSession session;

    private final DashboardViewModel dashboardViewModel = new DashboardViewModel();
    private DashboardController dashboardController;

    private final TournamentViewModel tournamentViewModel = new TournamentViewModel();
    private TournamentController tournamentController;

    private final TeamsViewModel teamsViewModel = new TeamsViewModel();
    private TeamsController teamsController;

    private final GroupStageViewModel groupStageViewModel = new GroupStageViewModel();
    private GroupStageController groupStageController;

    private final MatchesViewModel matchesViewModel = new MatchesViewModel();
    private MatchesController matchesController;

    private final PlayersViewModel playersViewModel = new PlayersViewModel();
    private PlayersController playersController;

    private final Map<NavItem, Node> pages = new EnumMap<>(NavItem.class);
    private final ScrollPane pageArea = new ScrollPane();
    private final StadiumsView stadiumsView = new StadiumsView();

    @Override
    public void start(Stage stage) {
        session = resolveSession();
        dashboardController = new DashboardController(session, dashboardViewModel);
        tournamentController = new TournamentController(session, tournamentViewModel);
        teamsController = new TeamsController(session, teamsViewModel);
        groupStageController = new GroupStageController(session, groupStageViewModel);
        matchesController = new MatchesController(session, matchesViewModel);
        playersController = new PlayersController(session, playersViewModel);

        Sidebar sidebar = new Sidebar();
        sidebar.setOnSelect(this::showPage);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-shell");
        root.setLeft(sidebar);
        root.setCenter(buildContent());

        Scene scene = new Scene(root, 1120, 720);
        scene.getStylesheets().add(loadStylesheet());

        stage.setTitle("Copa Internacional de Clubes");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();

        tournamentController.setOnStateChanged(this::refreshPages);
        tournamentController.refresh();
        refreshPages();
        showPage(sidebar.getSelected());
    }

    private TournamentSession resolveSession() {
        TournamentSession resolved;
        if (TournamentSession.hasSavedTournament() && userWantsToContinue()) {
            resolved = loadSavedSession();
        } else {
            TournamentSession.discardSavedTournament();
            resolved = new TournamentSession();
        }
        return resolved;
    }

    private boolean userWantsToContinue() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Copa Internacional de Clubes");
        alert.setHeaderText("A previous tournament was found.");
        alert.setContentText("Do you want to continue it, or start a new one?");

        ButtonType continueButton = new ButtonType("Continue previous");
        ButtonType newButton = new ButtonType("Start new");
        alert.getButtonTypes().setAll(continueButton, newButton);

        Optional<ButtonType> choice = alert.showAndWait();
        return choice.isPresent() && choice.get() == continueButton;
    }

    private TournamentSession loadSavedSession() {
        TournamentSession loaded;
        try {
            loaded = TournamentSession.load();
        } catch (IOException | ClassNotFoundException exception) {
            loaded = new TournamentSession();
        }
        return loaded;
    }

    private void refreshPages() {
        dashboardController.refresh();
        teamsController.refresh();
        groupStageController.refresh();
        matchesController.refresh();
        playersController.refresh();
        stadiumsView.refresh(session.getChampionship());
    }

    private BorderPane buildContent() {
        buildPages();

        pageArea.getStyleClass().add("content-scroll");
        pageArea.setFitToWidth(true);
        pageArea.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        BorderPane content = new BorderPane();
        content.getStyleClass().add("content-area");
        content.setTop(new DashboardHeader(dashboardViewModel.stageLabelProperty()));
        content.setCenter(pageArea);
        return content;
    }

    private void showPage(NavItem item) {
        pageArea.setContent(pages.get(item));
        pageArea.setVvalue(0);
    }

    private void buildPages() {
        pages.put(NavItem.DASHBOARD, buildDashboardPage());
        pages.put(NavItem.TOURNAMENT, new TournamentView(
                tournamentViewModel,
                tournamentController::importData,
                tournamentController::drawGroups,
                tournamentController::advance,
                tournamentController::save));

        pages.put(NavItem.TEAMS, new TeamsView(
                teamsViewModel.getRows(),
                teamsViewModel.getTeams(),
                teamsViewModel.statusProperty()));
        pages.put(NavItem.GROUP_STAGE, new GroupStageView(
                groupStageViewModel.getZones(),
                groupStageViewModel.statusProperty(),
                tournamentViewModel.canDrawProperty(),
                tournamentController::drawGroups));
        pages.put(NavItem.MATCHES, new MatchesView(
                matchesViewModel.getMatches(),
                matchesViewModel.statusProperty()));
        putPlaceholder(NavItem.KNOCKOUT_STAGE,
                "Bracket from the quarter-finals to the champion, with both legs, aggregate "
                        + "score and qualifying criteria.");
        pages.put(NavItem.PEOPLE, new PeopleView(new PlayersView(
                playersViewModel.getPlayers(),
                playersViewModel.statusProperty())));
        pages.put(NavItem.STADIUMS, stadiumsView);
        putPlaceholder(NavItem.STATISTICS,
                "Tournament statistics.");
        putPlaceholder(NavItem.REPORTS,
                "Catalogue of the reports required by the assignment. Pick one and generate it.");
    }

    private void putPlaceholder(NavItem item, String description) {
        pages.put(item, new PlaceholderPage(item.getLabel(), description));
    }

    private VBox buildDashboardPage() {
        VBox body = new VBox(
                buildSummaryRow(),
                new TournamentProgress(dashboardViewModel.currentStageProperty()));
        body.getStyleClass().add("dashboard-body");
        return body;
    }

    private HBox buildSummaryRow() {
        HBox row = new HBox(
                new SummaryCard("Teams", dashboardViewModel.teamsProperty().asString()),
                new SummaryCard("Matches Played", dashboardViewModel.matchesPlayedProperty().asString()),
                new SummaryCard("Goals", dashboardViewModel.goalsProperty().asString()));
        row.getStyleClass().add("summary-row");

        for (Node card : row.getChildren()) {
            HBox.setHgrow(card, Priority.ALWAYS);
        }
        return row;
    }

    private String loadStylesheet() {
        URL fromClasspath = AppWindow.class.getResource(STYLESHEET);
        if (fromClasspath != null) {
            return fromClasspath.toExternalForm();
        }
        File fromSources = new File("src" + STYLESHEET);
        if (fromSources.exists()) {
            return fromSources.toURI().toString();
        }
        throw new IllegalStateException("No se encontro la hoja de estilos: " + STYLESHEET);
    }
}
