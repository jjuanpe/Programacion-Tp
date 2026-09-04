package ui.fx;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Predicate;

/**
 * Pagina Knockout Stage: resumen de los partidos de la fase eliminatoria
 * (cuartos, semis y final), con un filtro por fase.
 *
 * Igual que Matches, es de solo consulta: no simula nada, solo muestra los
 * partidos ya jugados o pendientes de la llave. Al elegir uno se ve su
 * detalle: alineaciones, incidencias y el global de la llave cuando
 * corresponde.
 */
public class KnockoutStageView extends VBox {

    private static final String ALL_PHASES = "All phases";
    private static final List<String> PHASE_FILTERS =
            List.of(ALL_PHASES, "Quarter-final", "Semi-final", "Final");

    private final FilteredList<MatchDetail> filteredMatches;

    private final Label detailTitle = new Label();
    private final Label detailSubtitle = new Label();
    private final Label homeTeamName = new Label();
    private final Label awayTeamName = new Label();
    private final TableView<LineupRow> homeLineup = new TableView<>();
    private final TableView<LineupRow> awayLineup = new TableView<>();
    private final TableView<IncidenceRow> incidences = new TableView<>();

    public KnockoutStageView(ObservableList<MatchDetail> matches, ObservableValue<String> emptyMessage) {
        getStyleClass().add("page");
        this.filteredMatches = new FilteredList<>(matches, ignored -> true);

        Label title = new Label("Knockout Stage");
        title.getStyleClass().add("section-title");

        getChildren().addAll(
                title,
                buildHint(),
                buildFilterRow(),
                buildSubtitle("Matches"),
                buildMatchesTable(emptyMessage),
                buildSubtitle("Match detail"),
                buildDetailPanel());
    }

    /** Donde se juegan los partidos, para quien venga a buscar ese boton aca. */
    private Label buildHint() {
        Label hint = new Label(
                "Knockout matches are played by phase from the Tournament page. This page is read-only.");
        hint.getStyleClass().add("action-hint");
        return hint;
    }

    private HBox buildFilterRow() {
        Label label = new Label("Phase:");
        label.getStyleClass().add("action-hint");

        ComboBox<String> filter = new ComboBox<>(FXCollections.observableArrayList(PHASE_FILTERS));
        filter.getStyleClass().add("filter-combo");
        filter.getSelectionModel().select(ALL_PHASES);
        filter.valueProperty().addListener((observable, previous, selected) ->
                filteredMatches.setPredicate(matchOfPhase(selected)));

        HBox row = new HBox(label, filter);
        row.getStyleClass().add("action-row");
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Predicate<MatchDetail> matchOfPhase(String selected) {
        return match -> ALL_PHASES.equals(selected) || match.getPhaseCategory().equals(selected);
    }

    private Label buildSubtitle(String text) {
        Label subtitle = new Label(text);
        subtitle.getStyleClass().add("subsection-title");
        return subtitle;
    }

    private TableView<MatchDetail> buildMatchesTable(ObservableValue<String> emptyMessage) {
        TableView<MatchDetail> table = new TableView<>(filteredMatches);
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(300);

        Label placeholder = new Label();
        placeholder.getStyleClass().add("table-placeholder");
        placeholder.textProperty().bind(emptyMessage);
        table.setPlaceholder(placeholder);

        table.getColumns().add(TableColumns.text("Phase", MatchDetail::getPhase));
        table.getColumns().add(TableColumns.text("Date", MatchDetail::getDate));
        table.getColumns().add(TableColumns.text("Home", MatchDetail::getHomeTeam));
        table.getColumns().add(TableColumns.numeric("Score", MatchDetail::getScore));
        table.getColumns().add(TableColumns.text("Away", MatchDetail::getAwayTeam));
        table.getColumns().add(TableColumns.text("Aggregate", MatchDetail::getAggregate));
        table.getColumns().add(TableColumns.text("Status", MatchDetail::getStatus));
        table.getColumns().add(TableColumns.text("Referee", MatchDetail::getReferee));

        table.getSelectionModel().selectedItemProperty()
                .addListener((observable, previous, selected) -> showDetail(selected));
        filteredMatches.addListener((ListChangeListener<MatchDetail>) change -> selectFirst(table));
        selectFirst(table);
        return table;
    }

    private void selectFirst(TableView<MatchDetail> table) {
        if (table.getItems().isEmpty()) {
            showDetail(null);
        } else if (!table.getItems().contains(table.getSelectionModel().getSelectedItem())) {
            table.getSelectionModel().selectFirst();
        }
    }

    private VBox buildDetailPanel() {
        detailTitle.getStyleClass().add("detail-title");
        detailSubtitle.getStyleClass().add("detail-subtitle");
        homeTeamName.getStyleClass().add("subsection-title");
        awayTeamName.getStyleClass().add("subsection-title");

        VBox home = new VBox(homeTeamName, buildLineupTable(homeLineup));
        VBox away = new VBox(awayTeamName, buildLineupTable(awayLineup));
        home.getStyleClass().add("lineup");
        away.getStyleClass().add("lineup");
        HBox.setHgrow(home, Priority.ALWAYS);
        HBox.setHgrow(away, Priority.ALWAYS);

        HBox lineups = new HBox(home, away);
        lineups.getStyleClass().add("split");

        VBox panel = new VBox(
                detailTitle,
                detailSubtitle,
                buildSubtitle("Line-ups"),
                lineups,
                buildSubtitle("Incidences"),
                buildIncidencesTable());
        panel.getStyleClass().addAll("panel", "match-detail");
        return panel;
    }

    private TableView<LineupRow> buildLineupTable(TableView<LineupRow> table) {
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(300);
        table.setPlaceholder(buildPlaceholder("Not played yet"));

        table.getColumns().add(TableColumns.text("Player", LineupRow::getPlayer));
        table.getColumns().add(TableColumns.text("Position", LineupRow::getPosition));
        table.getColumns().add(TableColumns.text("Role", LineupRow::getRole));
        table.getColumns().add(TableColumns.numeric("Min.", LineupRow::getMinutes));
        return table;
    }

    private TableView<IncidenceRow> buildIncidencesTable() {
        incidences.getStyleClass().add("data-table");
        incidences.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        incidences.setPrefHeight(260);
        incidences.setPlaceholder(buildPlaceholder("No incidences recorded"));

        incidences.getColumns().add(TableColumns.numeric("Minute", IncidenceRow::getMinute));
        incidences.getColumns().add(TableColumns.text("Type", IncidenceRow::getType));
        incidences.getColumns().add(TableColumns.text("Detail", IncidenceRow::getDetail));
        return incidences;
    }

    private Label buildPlaceholder(String text) {
        Label placeholder = new Label(text);
        placeholder.getStyleClass().add("table-placeholder");
        return placeholder;
    }

    /** Muestra el partido elegido, o el detalle vacio si no hay ninguno. */
    private void showDetail(MatchDetail match) {
        boolean hasMatch = match != null;

        detailTitle.setText(hasMatch ? match.getTitle() : "No match selected");
        detailSubtitle.setText(hasMatch ? describe(match) : "");
        homeTeamName.setText(hasMatch ? match.getHomeTeam() : "");
        awayTeamName.setText(hasMatch ? match.getAwayTeam() : "");

        homeLineup.getItems().setAll(hasMatch ? match.getHomeLineup() : List.of());
        awayLineup.getItems().setAll(hasMatch ? match.getAwayLineup() : List.of());
        incidences.getItems().setAll(hasMatch ? match.getIncidences() : List.of());
    }

    /** Linea de contexto: fase, fecha, sede, arbitro y global de la llave. */
    private String describe(MatchDetail match) {
        StringBuilder description = new StringBuilder();
        description.append(match.getPhase())
                .append("  |  ").append(match.getDate())
                .append("  |  Stadium: ").append(match.getStadium())
                .append("  |  Referee: ").append(match.getReferee());
        if (!match.getAggregate().isEmpty()) {
            description.append("  |  ").append(match.getAggregate());
        }
        return description.toString();
    }
}
