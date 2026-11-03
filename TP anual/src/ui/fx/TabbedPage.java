package ui.fx;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;

public class TabbedPage extends VBox {

    private static final String STYLE_ACTIVE = "active";

    private final Map<String, Node> tabs;
    private final Map<String, Button> tabButtons = new LinkedHashMap<>();
    private final StackPane content = new StackPane();

    public TabbedPage(String title, Map<String, Node> tabs) {
        if (tabs.isEmpty()) {
            throw new IllegalArgumentException("A tabbed page needs at least one tab");
        }
        this.tabs = new LinkedHashMap<>(tabs);
        getStyleClass().add("page");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");

        getChildren().addAll(titleLabel, buildTabRow(), content);
        selectTab(this.tabs.keySet().iterator().next());
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

    public void selectTab(String tabName) {
        Node selected = tabs.get(tabName);
        if (selected == null) {
            throw new IllegalArgumentException("Unknown tab: " + tabName);
        }
        for (Button button : tabButtons.values()) {
            button.getStyleClass().remove(STYLE_ACTIVE);
        }
        tabButtons.get(tabName).getStyleClass().add(STYLE_ACTIVE);
        content.getChildren().setAll(selected);
    }
}