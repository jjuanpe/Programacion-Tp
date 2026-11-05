package ui.fx;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Knockout bracket: qualified teams of each zone, quarter-finals, semi-finals and the final.
 */
public class KnockoutView extends VBox {

    private static final String STYLE_WINNER = "bracket-winner";

    public KnockoutView(KnockoutViewModel viewModel) {
        getStyleClass().add("page");

        Label title = new Label("Knockout Stage");
        title.getStyleClass().add("section-title");

        Label status = new Label();
        status.getStyleClass().add("placeholder-note");
        status.textProperty().bind(viewModel.statusProperty());
        status.visibleProperty().bind(viewModel.statusProperty().isNotEmpty());
        status.managedProperty().bind(status.visibleProperty());

        HBox bracket = new HBox(
                buildQualifiedColumn(viewModel.getQualified()),
                buildTieColumn("Quarter-finals", viewModel.getQuarterFinals()),
                buildTieColumn("Semi-finals", viewModel.getSemiFinals()),
                buildTieColumn("Final", viewModel.getFinals()));
        bracket.getStyleClass().add("bracket");

        getChildren().addAll(title, status, buildChampionBanner(viewModel.championProperty()), bracket);
    }

    private Label buildChampionBanner(ObservableValue<String> champion) {
        Label banner = new Label();
        banner.getStyleClass().add("champion-banner");
        banner.textProperty().bind(champion);
        banner.visibleProperty().bind(banner.textProperty().isNotEmpty());
        banner.managedProperty().bind(banner.visibleProperty());
        return banner;
    }

    private VBox buildQualifiedColumn(ObservableList<QualifiedRow> rows) {
        VBox cards = new VBox();
        cards.getStyleClass().add("bracket-cards");
        Runnable render = () -> {
            cards.getChildren().clear();
            for (QualifiedRow row : rows) {
                cards.getChildren().add(buildCard(row.getZone(),
                        new Label(new StringBuilder("1. ").append(row.getFirst()).toString()),
                        new Label(new StringBuilder("2. ").append(row.getSecond()).toString())));
            }
        };
        rows.addListener((ListChangeListener<QualifiedRow>) change -> render.run());
        render.run();
        return buildColumn("Qualified", cards);
    }

    private VBox buildTieColumn(String title, ObservableList<BracketTie> ties) {
        VBox cards = new VBox();
        cards.getStyleClass().add("bracket-cards");
        Runnable render = () -> {
            cards.getChildren().clear();
            for (BracketTie tie : ties) {
                cards.getChildren().add(buildTieCard(tie));
            }
        };
        ties.addListener((ListChangeListener<BracketTie>) change -> render.run());
        render.run();
        return buildColumn(title, cards);
    }

    private VBox buildColumn(String title, VBox cards) {
        Label heading = new Label(title);
        heading.getStyleClass().add("subsection-title");
        VBox column = new VBox(heading, cards);
        column.getStyleClass().add("bracket-column");
        HBox.setHgrow(column, Priority.ALWAYS);
        return column;
    }

    private VBox buildTieCard(BracketTie tie) {
        Label teamA = new Label(tie.getTeamA());
        Label teamB = new Label(tie.getTeamB());
        if (tie.isPlayed()) {
            (tie.getWinner().equals(tie.getTeamA()) ? teamA : teamB).getStyleClass().add(STYLE_WINNER);
        }
        Label score = new Label(tie.getScore());
        score.getStyleClass().add("action-hint");
        score.setWrapText(true);

        VBox card = buildCard(tie.getTitle(), teamA, teamB, score);
        if (tie.isPlayed()) {
            Label criteria = new Label(new StringBuilder("Decided by: ").append(tie.getCriteria()).toString());
            criteria.getStyleClass().add("action-hint");
            card.getChildren().add(criteria);
        }
        return card;
    }

    private VBox buildCard(String title, Label... lines) {
        Label heading = new Label(title);
        heading.getStyleClass().add("bracket-card-title");
        VBox card = new VBox(heading);
        card.getChildren().addAll(lines);
        card.getStyleClass().add("bracket-card");
        return card;
    }
}