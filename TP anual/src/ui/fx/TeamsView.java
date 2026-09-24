package ui.fx;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class TeamsView extends VBox {

    private static final String STATE_VALID = "squad-valid";
    private static final String STATE_INVALID = "squad-invalid";

    private final Label detailName = new Label();
    private final Label countryValue = new Label();
    private final Label rankingValue = new Label();
    private final Label coachValue = new Label();
    private final Label coachAgeValue = new Label();
    private final Label coachCountryValue = new Label();
    private final Label squadStatus = new Label();
    private final TableView<PlayerRow> squadTable = new TableView<>();

    public TeamsView(
            ObservableList<TeamRow> rows,
            ObservableList<TeamDetail> teams,
            ObservableValue<String> emptyMessage) {
        getStyleClass().add("page");

        Label title = new Label("Teams");
        title.getStyleClass().add("section-title");

        getChildren().addAll(
                title,
                buildSubtitle("All teams"),
                buildSummaryTable(rows, emptyMessage),
                buildSubtitle("Team detail"),
                buildDetailSplit(teams, emptyMessage));
    }

    private Label buildSubtitle(String text) {
        Label subtitle = new Label(text);
        subtitle.getStyleClass().add("subsection-title");
        return subtitle;
    }

    private TableView<TeamRow> buildSummaryTable(
            ObservableList<TeamRow> rows,
            ObservableValue<String> emptyMessage) {
        TableView<TeamRow> table = new TableView<>(rows);
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(buildPlaceholder(emptyMessage));

        table.getColumns().add(TableColumns.text("Team", TeamRow::getTeam));
        table.getColumns().add(TableColumns.numeric("Avg. player age", TeamRow::getAveragePlayerAge));
        table.getColumns().add(TableColumns.numeric("Coach age", TeamRow::getCoachAge));
        table.getColumns().add(TableColumns.text("Coach nationality", TeamRow::getCoachNationality));
        table.getColumns().add(TableColumns.numeric("Goals for",
                row -> String.valueOf(row.getGoalsFor())));
        table.getColumns().add(TableColumns.numeric("Goals against",
                row -> String.valueOf(row.getGoalsAgainst())));
        table.getColumns().add(TableColumns.numeric("Effectiveness", TeamRow::getEffectiveness));
        return table;
    }

    private HBox buildDetailSplit(
            ObservableList<TeamDetail> teams,
            ObservableValue<String> emptyMessage) {
        ListView<TeamDetail> teamList = new ListView<>(teams);
        teamList.getStyleClass().add("team-list");
        teamList.setPlaceholder(buildPlaceholder(emptyMessage));
        teamList.getSelectionModel().selectedItemProperty()
                .addListener((observable, previous, selected) -> showDetail(selected));

        teams.addListener((ListChangeListener<TeamDetail>) change -> selectFirst(teamList));
        selectFirst(teamList);

        VBox detail = buildDetailPanel();
        HBox.setHgrow(detail, Priority.ALWAYS);

        HBox split = new HBox(teamList, detail);
        split.getStyleClass().add("split");
        return split;
    }

    private void selectFirst(ListView<TeamDetail> teamList) {
        if (teamList.getItems().isEmpty()) {
            showDetail(null);
        } else if (teamList.getSelectionModel().getSelectedItem() == null) {
            teamList.getSelectionModel().selectFirst();
        }
    }

    private VBox buildDetailPanel() {
        detailName.getStyleClass().add("detail-title");
        squadStatus.getStyleClass().add("squad-status");
        squadStatus.setWrapText(true);

        squadTable.getStyleClass().add("data-table");
        squadTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        squadTable.setPlaceholder(buildPlaceholder(null));
        squadTable.getColumns().add(TableColumns.text("Player", PlayerRow::getName));
        squadTable.getColumns().add(TableColumns.text("Position", PlayerRow::getPosition));
        squadTable.getColumns().add(TableColumns.numeric("Age",
                row -> String.valueOf(row.getAge())));
        squadTable.getColumns().add(TableColumns.numeric("Rating",
                row -> String.valueOf(row.getRating())));

        VBox panel = new VBox(
                detailName,
                buildDetailRow("Country", countryValue),
                buildDetailRow("Ranking", rankingValue),
                buildDetailRow("Head coach", coachValue),
                buildDetailRow("Coach age", coachAgeValue),
                buildDetailRow("Coach nationality", coachCountryValue),
                squadStatus,
                squadTable);
        panel.getStyleClass().addAll("panel", "team-detail");
        return panel;
    }

    private HBox buildDetailRow(String label, Label value) {
        Label name = new Label(label);
        name.getStyleClass().add("status-row-label");
        value.getStyleClass().add("status-row-value");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(name, spacer, value);
        row.getStyleClass().add("status-row");
        return row;
    }

    private void showDetail(TeamDetail detail) {
        boolean hasDetail = detail != null;

        detailName.setText(hasDetail ? detail.getName() : "No team selected");
        countryValue.setText(hasDetail ? detail.getCountry() : "");
        rankingValue.setText(hasDetail ? String.valueOf(detail.getRanking()) : "");
        coachValue.setText(hasDetail ? detail.getCoachName() : "");
        coachAgeValue.setText(hasDetail ? detail.getCoachAge() : "");
        coachCountryValue.setText(hasDetail ? detail.getCoachNationality() : "");
        squadStatus.setText(hasDetail ? detail.getSquadStatus() : "");
        squadTable.getItems().setAll(hasDetail ? detail.getSquad() : List.of());

        squadStatus.getStyleClass().removeAll(STATE_VALID, STATE_INVALID);
        if (hasDetail) {
            squadStatus.getStyleClass().add(detail.isSquadValid() ? STATE_VALID : STATE_INVALID);
        }
    }

    private Label buildPlaceholder(ObservableValue<String> emptyMessage) {
        Label placeholder = new Label();
        placeholder.getStyleClass().add("table-placeholder");
        if (emptyMessage == null) {
            placeholder.setText("");
        } else {
            placeholder.textProperty().bind(emptyMessage);
        }
        return placeholder;
    }
}
