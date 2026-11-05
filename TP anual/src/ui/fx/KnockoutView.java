package ui.fx;

import viewmodel.BracketTie;
import viewmodel.KnockoutViewModel;
import viewmodel.QualifiedRow;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

public class KnockoutView extends VBox {

    private static final String STYLE_WINNER = "bracket-winner";
    private static final double CARD_WIDTH = 150;
    private static final double CARD_HEIGHT = 138;
    private static final double COLUMN_GAP = 34;
    private static final double ROW_GAP = 14;
    private static final double LABEL_HEIGHT = 22;
    private static final int COLUMN_COUNT = 5;

    private final KnockoutViewModel viewModel;
    private final Runnable onOpenMatches;
    private final Pane board = new Pane();

    public KnockoutView(KnockoutViewModel viewModel, Runnable onOpenMatches) {
        this.viewModel = viewModel;
        this.onOpenMatches = onOpenMatches;
        getStyleClass().add("page");

        Label title = Icons.titled(new Label("Knockout Stage"), Icon.BRACKET);
        title.getStyleClass().add("section-title");

        Label status = new Label();
        status.getStyleClass().add("placeholder-note");
        status.textProperty().bind(viewModel.statusProperty());
        status.visibleProperty().bind(viewModel.statusProperty().isNotEmpty());
        status.managedProperty().bind(status.visibleProperty());

        board.getStyleClass().add("bracket-board");
        ScrollPane scroll = new ScrollPane(board);
        scroll.getStyleClass().add("bracket-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setFitToHeight(true);

        Label qualifiedTitle = new Label("Qualified teams");
        qualifiedTitle.getStyleClass().add("subsection-title");

        getChildren().addAll(title, status, buildChampionBanner(viewModel.championProperty()),
                scroll, qualifiedTitle, buildQualifiedCards(viewModel.getQualified()));

        ListChangeListener<BracketTie> redraw = change -> drawBracket();
        viewModel.getQuarterFinals().addListener(redraw);
        viewModel.getSemiFinals().addListener(redraw);
        viewModel.getFinals().addListener(redraw);
        drawBracket();
    }

    private Label buildChampionBanner(ObservableValue<String> champion) {
        Label banner = Icons.with(new Label(), Icon.TROPHY);
        banner.getStyleClass().add("champion-banner");
        banner.textProperty().bind(champion);
        Animations.breathe(banner);
        banner.visibleProperty().bind(banner.textProperty().isNotEmpty());
        banner.managedProperty().bind(banner.visibleProperty());
        return banner;
    }

    private FlowPane buildQualifiedCards(ObservableList<QualifiedRow> rows) {
        FlowPane cards = new FlowPane(10, 10);
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
        return cards;
    }

    /*
     * Layout in 5 columns: quarter-finals 1-2 | semi-final 1 | final | semi-final 2 | quarter-finals 3-4.
     * Each card is placed by hand and the connector lines are plain Line nodes.
     */
    private void drawBracket() {
        board.getChildren().clear();
        List<BracketTie> quarters = viewModel.getQuarterFinals();
        List<BracketTie> semis = viewModel.getSemiFinals();
        List<BracketTie> finals = viewModel.getFinals();
        if (quarters.size() == 4 && semis.size() == 2 && finals.size() == 1) {
            double step = CARD_HEIGHT + ROW_GAP;
            double top = LABEL_HEIGHT;
            double[] quarterY = {top, top + step, top + 2 * step, top + 3 * step};
            double[] semiY = {(quarterY[0] + quarterY[1]) / 2, (quarterY[2] + quarterY[3]) / 2};
            double finalY = (semiY[0] + semiY[1]) / 2;

            List<Node> nodes = new ArrayList<>();
            addRoundLabel(nodes, "QUARTER-FINALS", 0);
            addRoundLabel(nodes, "SEMI-FINALS", 1);
            addRoundLabel(nodes, "FINAL", 2);
            addRoundLabel(nodes, "SEMI-FINALS", 3);
            addRoundLabel(nodes, "QUARTER-FINALS", 4);

            placeCard(nodes, quarters.get(0), 0, quarterY[0], false);
            placeCard(nodes, quarters.get(1), 0, quarterY[1], false);
            placeCard(nodes, semis.get(0), 1, semiY[0], false);
            placeCard(nodes, finals.get(0), 2, finalY, true);
            placeCard(nodes, semis.get(1), 3, semiY[1], false);
            placeCard(nodes, quarters.get(2), 4, quarterY[2], false);
            placeCard(nodes, quarters.get(3), 4, quarterY[3], false);

            // lines go first so the cards are drawn above them
            List<Node> lines = new ArrayList<>();
            connect(lines, 0, quarterY[0], 1, semiY[0], quarters.get(0));
            connect(lines, 0, quarterY[1], 1, semiY[0], quarters.get(1));
            connect(lines, 1, semiY[0], 2, finalY, semis.get(0));
            connect(lines, 4, quarterY[2], 3, semiY[1], quarters.get(2));
            connect(lines, 4, quarterY[3], 3, semiY[1], quarters.get(3));
            connect(lines, 3, semiY[1], 2, finalY, semis.get(1));
            board.getChildren().addAll(lines);
            board.getChildren().addAll(nodes);

            board.setMinWidth(COLUMN_COUNT * CARD_WIDTH + (COLUMN_COUNT - 1) * COLUMN_GAP + 12);
            board.setMinHeight(top + 4 * step);
        }
    }

    private double columnX(int column) {
        return column * (CARD_WIDTH + COLUMN_GAP);
    }

    private void addRoundLabel(List<Node> nodes, String text, int column) {
        Label label = new Label(text);
        label.getStyleClass().add("bracket-round-label");
        label.setLayoutX(columnX(column));
        label.setLayoutY(0);
        nodes.add(label);
    }

    private void placeCard(List<Node> nodes, BracketTie tie, int column, double y, boolean isFinal) {
        VBox card = buildTieCard(tie);
        card.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        card.setMinSize(CARD_WIDTH, CARD_HEIGHT);
        card.setMaxSize(CARD_WIDTH, CARD_HEIGHT);
        card.setLayoutX(columnX(column));
        card.setLayoutY(y);
        if (isFinal) {
            card.getStyleClass().add("bracket-final-card");
        }
        nodes.add(card);
    }

    // Elbow line from the middle of one card to the middle of the next round's card.
    private void connect(List<Node> lines, int fromColumn, double fromY, int toColumn, double toY, BracketTie tie) {
        boolean forward = toColumn > fromColumn;
        double startX = forward ? columnX(fromColumn) + CARD_WIDTH : columnX(fromColumn);
        double endX = forward ? columnX(toColumn) : columnX(toColumn) + CARD_WIDTH;
        double midX = (startX + endX) / 2;
        double startY = fromY + CARD_HEIGHT / 2;
        double endY = toY + CARD_HEIGHT / 2;
        String extra = tie.isPlayed() ? "winner-line" : "";
        lines.add(line(startX, startY, midX, startY, extra));
        lines.add(line(midX, startY, midX, endY, extra));
        lines.add(line(midX, endY, endX, endY, extra));
    }

    private Line line(double x1, double y1, double x2, double y2, String extraStyle) {
        Line line = new Line(x1, y1, x2, y2);
        line.getStyleClass().add("bracket-line");
        if (!extraStyle.isEmpty()) {
            line.getStyleClass().add(extraStyle);
        }
        return line;
    }

    private VBox buildTieCard(BracketTie tie) {
        Label teamA = new Label(tie.getTeamA());
        Label teamB = new Label(tie.getTeamB());
        teamA.getStyleClass().add("bracket-team");
        teamB.getStyleClass().add("bracket-team");
        if (tie.isPlayed()) {
            (tie.getWinner().equals(tie.getTeamA()) ? teamA : teamB).getStyleClass().add(STYLE_WINNER);
        }
        Label score = new Label(tie.getScore());
        score.getStyleClass().add("action-hint");
        score.setWrapText(true);

        VBox card = buildCard(tie.getTitle(), teamA, teamB, score);
        StringBuilder detail = new StringBuilder(tie.getScore());
        if (tie.isPlayed()) {
            detail.append("\nDecided by: ").append(tie.getCriteria());
        }
        Tooltip.install(card, new Tooltip(detail.toString()));
        card.getStyleClass().add("clickable");
        card.setOnMouseClicked(event -> onOpenMatches.run());
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
