package ui.fx;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PlaceholderPage extends VBox {

    public PlaceholderPage(String sectionName, String description) {
        getStyleClass().add("page");

        Label title = new Label(sectionName);
        title.getStyleClass().add("section-title");

        Label note = new Label(description);
        note.getStyleClass().add("placeholder-note");
        note.setWrapText(true);

        VBox panel = new VBox(note);
        panel.getStyleClass().add("panel");

        getChildren().addAll(title, panel);
    }
}
