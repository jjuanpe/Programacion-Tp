package ui.fx;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Pagina People: Players, Coaches y Referees en pestanas.
 *
 * Por ahora solo Players tiene contenido real; Coaches y Referees quedan
 * como aviso de que todavia no se desarrollaron, para que la navegacion ya
 * muestre las tres secciones previstas.
 */
public class PeopleView extends VBox {

    private static final String STYLE_ACTIVE = "active";

    private final Map<String, Node> tabs = new LinkedHashMap<>();
    private final Map<String, Button> tabButtons = new LinkedHashMap<>();
    private final StackPane content = new StackPane();

    public PeopleView(Node playersContent) {
        getStyleClass().add("page");

        Label title = new Label("People");
        title.getStyleClass().add("section-title");

        tabs.put("Players", playersContent);
        tabs.put("Coaches", buildComingSoon("Coaches"));
        tabs.put("Referees", buildComingSoon("Referees"));

        getChildren().addAll(title, buildTabRow(), content);
        selectTab("Players");
    }

    private HBox buildTabRow() {
        HBox row = new HBox();
        row.getStyleClass().add("sub-tabs");
        for (String tabName : tabs.keySet()) {
            Button button = new Button(tabName);
            button.getStyleClass().add("sub-tab-button");
            button.setOnAction(event -> selectTab(tabName));
            tabButtons.put(tabName, button);
            row.getChildren().add(button);
        }
        return row;
    }

    private void selectTab(String tabName) {
        for (Button button : tabButtons.values()) {
            button.getStyleClass().remove(STYLE_ACTIVE);
        }
        tabButtons.get(tabName).getStyleClass().add(STYLE_ACTIVE);
        content.getChildren().setAll(tabs.get(tabName));
    }

    private VBox buildComingSoon(String sectionName) {
        Label note = new Label(sectionName + " page coming soon.");
        note.getStyleClass().add("placeholder-note");
        VBox panel = new VBox(note);
        panel.getStyleClass().add("panel");
        return panel;
    }
}
