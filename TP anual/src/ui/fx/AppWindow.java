package ui.fx;

import viewmodel.DashboardViewModel;
import viewmodel.GroupStageViewModel;
import viewmodel.KnockoutViewModel;
import viewmodel.MatchesViewModel;
import viewmodel.PlayersViewModel;
import viewmodel.ReportsViewModel;
import viewmodel.StatisticsViewModel;
import viewmodel.TeamsViewModel;
import viewmodel.TopScorersViewModel;
import viewmodel.TournamentViewModel;

import controller.DashboardController;
import controller.GroupStageController;
import controller.MatchesController;
import controller.PlayersController;
import controller.TeamsController;
import controller.TournamentController;
import controller.TournamentSession;
import controller.TournamentStorage;
import controller.KnockoutController;
import controller.ReportsController;
import controller.CitiesController;
import controller.StadiumsController;
import controller.StatisticsController;
import controller.TopScorersController;
import java.util.LinkedHashMap;
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

    private final TopScorersViewModel topScorersViewModel = new TopScorersViewModel();
    private TopScorersController topScorersController;

    private final StatisticsViewModel statisticsViewModel = new StatisticsViewModel();
    private StatisticsController statisticsController;

    private final KnockoutViewModel knockoutViewModel = new KnockoutViewModel();
    private KnockoutController knockoutController;

    private final ReportsViewModel reportsViewModel = new ReportsViewModel();
    private ReportsController reportsController;

    private final Sidebar sidebar = new Sidebar();
    private DashboardHeader header;

    private final Map<NavItem, Node> pages = new EnumMap<>(NavItem.class);
    private final ScrollPane pageArea = new ScrollPane();
    private final StadiumsView stadiumsView = new StadiumsView();
    private final CitiesView citiesView = new CitiesView();

    @Override
    public void start(Stage stage) {
        session = resolveSession();
        stadiumsView.setController(new StadiumsController());
        citiesView.setController(new CitiesController());
        dashboardController = new DashboardController(session, dashboardViewModel);
        tournamentController = new TournamentController(session, tournamentViewModel);
        teamsController = new TeamsController(session, teamsViewModel);
        groupStageController = new GroupStageController(session, groupStageViewModel);
        matchesController = new MatchesController(session, matchesViewModel);
        playersController = new PlayersController(session, playersViewModel);
        topScorersController = new TopScorersController(topScorersViewModel);
        statisticsController = new StatisticsController(session, statisticsViewModel);
        knockoutController = new KnockoutController(session, knockoutViewModel);
        reportsController = new ReportsController(session, reportsViewModel);

        sidebar.setOnSelect(this::showPage);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-shell");
        root.setLeft(sidebar);
        root.setCenter(buildContent());

        Scene scene = new Scene(root, 1120, 720);
        scene.getStylesheets().add(loadStylesheet());

        stage.setTitle("International Club Cup");
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
        if (TournamentStorage.hasSavedTournament() && userWantsToContinue()) {
            resolved = loadSavedSession();
        } else {
            resolved = new TournamentSession();
        }
        return resolved;
    }

    private boolean userWantsToContinue() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("International Club Cup");
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
            loaded = TournamentStorage.load();
        } catch (IOException | ClassNotFoundException | ClassCastException exception) {
            showLoadError(exception);
            loaded = new TournamentSession();
        }
        return loaded;
    }

    private void showLoadError(Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("International Club Cup");
        alert.setHeaderText("The saved tournament could not be loaded.");
        alert.setContentText(new StringBuilder("A new tournament will start. Reason: ")
                .append(exception.getMessage()).toString());
        alert.showAndWait();
    }

    private void refreshPages() {
        dashboardController.refresh();
        teamsController.refresh();
        groupStageController.refresh();
        matchesController.refresh();
        playersController.refresh();
        topScorersController.show(session);
        statisticsController.refresh();
        knockoutController.refresh();

    }

    private BorderPane buildContent() {
        buildPages();

        pageArea.getStyleClass().add("content-scroll");
        pageArea.setFitToWidth(true);
        pageArea.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        BorderPane content = new BorderPane();
        content.getStyleClass().add("content-area");
        header = new DashboardHeader(dashboardViewModel.stageLabelProperty());
        content.setTop(header);
        content.setCenter(pageArea);
        return content;
    }

    private void showPage(NavItem item) {
        if (item == NavItem.STADIUMS) {
            stadiumsView.refresh();
        } else if (item == NavItem.CITIES) {
            citiesView.refresh();
        }
        Node page = pages.get(item);
        pageArea.setContent(page);
        pageArea.setVvalue(0);
        if (header != null) {
            header.showPage(item);
        }
        if (page instanceof VBox box && item == NavItem.DASHBOARD) {
            Animations.cascade(box);
        } else {
            Animations.enter(page);
        }
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
                groupStageViewModel.statusProperty()));
        pages.put(NavItem.MATCHES, new MatchesView(
                matchesViewModel.getMatches(),
                matchesViewModel.statusProperty()));
        pages.put(NavItem.KNOCKOUT_STAGE, new KnockoutView(knockoutViewModel, () -> sidebar.select(NavItem.MATCHES)));
        pages.put(NavItem.PEOPLE, buildPeoplePage());
        pages.put(NavItem.STADIUMS, stadiumsView);
        pages.put(NavItem.CITIES, citiesView);

        pages.put(NavItem.STATISTICS, buildStatisticsPage());
        pages.put(NavItem.REPORTS, new ReportsView(
                reportsViewModel,
                sidebar::select,
                reportsController::exportIdCards));
    }

    private TabbedPage buildPeoplePage() {
        Map<String, Node> tabs = new LinkedHashMap<>();
        tabs.put("Players", new PlayersView(
                playersViewModel.getPlayers(),
                playersViewModel.statusProperty()));
        return new TabbedPage("People", Icon.PERSON, tabs);
    }

    private TabbedPage buildStatisticsPage() {
        Map<String, Node> tabs = new LinkedHashMap<>();
        tabs.put("Top scorers", StatisticsTables.topScorers(
                topScorersViewModel.getRows(), topScorersViewModel.statusProperty()));
        tabs.put("Fair play", StatisticsTables.fairPlay(
                statisticsViewModel.getFairPlayRows(), statisticsViewModel.statusProperty()));
        tabs.put("Minutes played", StatisticsTables.minutesPlayed(
                statisticsViewModel.getMinutesRows(), statisticsViewModel.statusProperty()));
        tabs.put("Referees", StatisticsTables.referees(
                statisticsViewModel.getRefereeRows(),
                statisticsViewModel.statusProperty(),
                statisticsViewModel.refereeAverageProperty()));
        return new TabbedPage("Statistics", Icon.CHART, tabs);
    }

    private VBox buildDashboardPage() {
        NextStepCard nextStep = new NextStepCard(
                dashboardViewModel.nextStepProperty(),
                dashboardViewModel.championProperty(),
                () -> sidebar.select(NavItem.TOURNAMENT));
        ScorerPreview scorers = new ScorerPreview(
                topScorersViewModel.getRows(),
                () -> sidebar.select(NavItem.STATISTICS));
        HBox.setHgrow(nextStep, Priority.ALWAYS);
        HBox.setHgrow(scorers, Priority.ALWAYS);
        HBox lower = new HBox(nextStep, scorers);
        lower.getStyleClass().add("summary-row");

        VBox body = new VBox(
                buildSummaryRow(),
                new TournamentProgress(dashboardViewModel.currentStageProperty()),
                new ZoneLeaders(groupStageViewModel.getZones(), () -> sidebar.select(NavItem.GROUP_STAGE)),
                lower);
        body.getStyleClass().add("dashboard-body");
        return body;
    }

    private HBox buildSummaryRow() {
        HBox row = new HBox(
                new SummaryCard("Teams", dashboardViewModel.teamsProperty().asString(),
                        Icon.TEAMS, "tone-sky"),
                new SummaryCard("Matches Played", dashboardViewModel.matchesPlayedProperty().asString(),
                        Icon.BALL, "tone-green"),
                new SummaryCard("Goals", dashboardViewModel.goalsProperty().asString(),
                        Icon.FLAG, "tone-gold"));
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
        File fromSources = new File(new StringBuilder("src").append(STYLESHEET).toString());
        if (fromSources.exists()) {
            return fromSources.toURI().toString();
        }
        throw new IllegalStateException(new StringBuilder("Stylesheet not found: ")
                .append(STYLESHEET).toString());
    }
}
