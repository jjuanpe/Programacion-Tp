package ui.fx;

import controller.DashboardController;
import controller.GroupStageController;
import controller.KnockoutStageController;
import controller.MatchesController;
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

/**
 * Ventana principal de la aplicacion de escritorio.
 *
 * Oficia de punto de composicion: crea la sesion del campeonato, los ViewModel
 * y los controladores, y los ata a las vistas. Tambien resuelve la navegacion:
 * cada opcion del sidebar muestra una pagina distinta en el area central.
 *
 * Al abrirse no hay ningun campeonato cargado. El torneo arranca desde la
 * pagina Tournament, con acciones explicitas del usuario.
 */
public class AppWindow extends Application {

    private static final String STYLESHEET = "/ui/fx/styles.css";

    private final TournamentSession session = new TournamentSession();

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

    private final KnockoutStageViewModel knockoutStageViewModel = new KnockoutStageViewModel();
    private final KnockoutStageController knockoutStageController =
            new KnockoutStageController(session, knockoutStageViewModel);

    private final Map<NavItem, Node> pages = new EnumMap<>(NavItem.class);
    private final ScrollPane pageArea = new ScrollPane();

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

        // Cada vez que el torneo avanza, las paginas que muestran datos se rehacen.
        tournamentController.setOnStateChanged(this::refreshPages);
        tournamentController.refresh();
        refreshPages();
        showPage(sidebar.getSelected());
    }

    /** Vuelve a leer el campeonato en todas las paginas que muestran datos. */
    private void refreshPages() {
        dashboardController.refresh();
        teamsController.refresh();
        groupStageController.refresh();
        matchesController.refresh();
        knockoutStageController.refresh();
    }

    /** Area central: encabezado fijo arriba y la pagina activa debajo. */
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

    /** Muestra la pagina de una opcion del sidebar. */
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
                tournamentController::advance));

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
        pages.put(NavItem.KNOCKOUT_STAGE, new KnockoutStageView(
                knockoutStageViewModel.getMatches(),
                knockoutStageViewModel.statusProperty()));
        putPlaceholder(NavItem.PEOPLE,
                "Players, coaches and referees.");
        putPlaceholder(NavItem.STADIUMS,
                "Registered stadiums and cities.");
        putPlaceholder(NavItem.STATISTICS,
                "Tournament statistics.");
        putPlaceholder(NavItem.REPORTS,
                "Catalogue of the reports required by the assignment. Pick one and generate it.");
    }

    private void putPlaceholder(NavItem item, String description) {
        pages.put(item, new PlaceholderPage(item.getLabel(), description));
    }

    /**
     * Dashboard: solo el panorama general del campeonato. El detalle (rankings,
     * fixtures, planteles) va en las paginas correspondientes.
     */
    private VBox buildDashboardPage() {
        VBox body = new VBox(
                buildSummaryRow(),
                new TournamentProgress(dashboardViewModel.currentStageProperty()));
        body.getStyleClass().add("dashboard-body");
        return body;
    }

    /**
     * Fila de tarjetas de resumen. Cada tarjeta se ata a una propiedad del
     * ViewModel: no hay ningun valor escrito en la vista.
     */
    private HBox buildSummaryRow() {
        HBox row = new HBox(
                new SummaryCard("Teams", dashboardViewModel.teamsProperty().asString()),
                new SummaryCard("Matches Played", dashboardViewModel.matchesPlayedProperty().asString()),
                new SummaryCard("Goals", dashboardViewModel.goalsProperty().asString()));
        row.getStyleClass().add("summary-row");

        // Todas crecen por igual, asi mantienen el mismo ancho al redimensionar.
        for (Node card : row.getChildren()) {
            HBox.setHgrow(card, Priority.ALWAYS);
        }
        return row;
    }

    /**
     * Busca la hoja de estilos en el classpath y, si el IDE todavia no la
     * copio a la carpeta de salida, cae al archivo dentro de src.
     */
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
