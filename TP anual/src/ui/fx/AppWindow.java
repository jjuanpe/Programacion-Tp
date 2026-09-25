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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

public class AppWindow extends Application {

    private static final String STYLESHEET = "/ui/fx/styles.css";

    private final TournamentSession session = TournamentSession.loadOrCreate();

    private final DashboardViewModel dashboardViewModel = new DashboardViewModel();
    private final DashboardController dashboardController =
            new DashboardController(session, dashboardViewModel);

    private final TournamentViewModel tournamentViewModel = new TournamentViewModel();
    private final TournamentController tournamentController =
            new TournamentController(session, tournamentViewModel);

    private final TeamsViewModel teamsViewModel = new TeamsViewModel();
    private final TeamsController teamsController =
            new TeamsController(session, teamsViewModel);

    private final GroupStageViewModel groupStageViewModel = new GroupStageViewModel();
    private final GroupStageController groupStageController =
            new GroupStageController(session, groupStageViewModel);

    private final MatchesViewModel matchesViewModel = new MatchesViewModel();
    private final MatchesController matchesController =
            new MatchesController(session, matchesViewModel);

    private final PlayersViewModel playersViewModel = new PlayersViewModel();
    private final PlayersController playersController =
            new PlayersController(session, playersViewModel);

    private final Map<NavItem, Node> pages = new EnumMap<>(NavItem.class);
    private final ScrollPane pageArea = new ScrollPane();
    private final StadiumsView stadiumsView = new StadiumsView();

    @Override
    public void start(Stage stage) {
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
