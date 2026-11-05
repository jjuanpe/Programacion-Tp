package ui.fx;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.function.Consumer;

public class ReportsView extends VBox {

    private final Consumer<NavItem> openPage;
    private final Consumer<File> exportIdCards;

    public ReportsView(ReportsViewModel viewModel, Consumer<NavItem> openPage, Consumer<File> exportIdCards) {
        this.openPage = openPage;
        this.exportIdCards = exportIdCards;
        getStyleClass().add("page");

        Label title = new Label("Reports");
        title.getStyleClass().add("section-title");

        Label status = new Label();
        status.getStyleClass().add("action-message");
        status.textProperty().bind(viewModel.statusProperty());

        VBox catalogue = new VBox(
                buildPdfRow(),
                buildPageRow("II. Top scorers", "Goals in matches (not shoot-outs) and how many were penalties. "
                        + "Statistics > Top scorers.", NavItem.STATISTICS),
                buildPageRow("III. Fair play ranking", "Cards and fair play points by team. "
                        + "Statistics > Fair play.", NavItem.STATISTICS),
                buildPageRow("IV. Most minutes played", "Matches and minutes of each player. "
                        + "Statistics > Minutes played.", NavItem.STATISTICS),
                buildPageRow("V. Bracket", "Qualified teams, quarter-finals, semi-finals, final and champion.",
                        NavItem.KNOCKOUT_STAGE),
                buildPageRow("VI. Teams", "Alphabetical list with ages, coach, goals and effectiveness.",
                        NavItem.TEAMS),
                buildPageRow("VII. Referees", "Matches officiated, years refereeing and their average. "
                        + "Statistics > Referees.", NavItem.STATISTICS),
                buildPageRow("VIII. Players by position", "Every player with a position filter. "
                        + "People > Players.", NavItem.PEOPLE));
        catalogue.getStyleClass().add("report-list");

        getChildren().addAll(title, catalogue, status);
    }

    private HBox buildPdfRow() {
        Button export = new Button("Export PDF");
        export.getStyleClass().addAll("action-button", "primary-button");
        export.setOnAction(event -> chooseFileAndExport());
        return buildRow("I. Identification cards",
                "Players, coaches and referees with personal data, role, photo and barcode. "
                        + "Photos are read from the \"photos\" folder (e.g. photos/DU-1234.jpg).", export);
    }

    private HBox buildPageRow(String name, String description, NavItem page) {
        Button open = new Button("Open");
        open.getStyleClass().addAll("action-button", "secondary-button");
        open.setOnAction(event -> openPage.accept(page));
        return buildRow(name, description, open);
    }

    private HBox buildRow(String name, String description, Button action) {
        int dot = name.indexOf(". ");
        Label badge = new Label(name.substring(0, dot));
        badge.getStyleClass().add("report-badge");
        Label nameLabel = new Label(name.substring(dot + 2));
        nameLabel.getStyleClass().add("report-name");
        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("action-hint");
        descriptionLabel.setWrapText(true);

        VBox text = new VBox(nameLabel, descriptionLabel);
        HBox.setHgrow(text, Priority.ALWAYS);

        action.setMinWidth(Region.USE_PREF_SIZE);
        HBox row = new HBox(badge, text, action);
        row.getStyleClass().add("report-row");
        return row;
    }

    private void chooseFileAndExport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save identification cards");
        chooser.setInitialFileName("identification-cards.pdf");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
        File output = chooser.showSaveDialog(getScene().getWindow());
        if (output != null) {
            exportIdCards.accept(output);
        }
    }
}