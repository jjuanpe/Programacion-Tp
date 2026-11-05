package ui.fx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public class Sidebar extends VBox {

    private static final String STYLE_ACTIVE = "active";

    private final Map<NavItem, Button> buttons = new EnumMap<>(NavItem.class);

    private Consumer<NavItem> onSelect;

    private NavItem selected;

    public Sidebar() {
        getStyleClass().add("sidebar");

        getChildren().addAll(buildHeader(), buildNav(), buildSpacer(), buildFooter());

        select(NavItem.DASHBOARD);
    }

    private VBox buildHeader() {
        Region ball = Icons.of(Icon.BALL, 30);
        StackPane logo = new StackPane(ball);
        logo.getStyleClass().add("sidebar-logo");
        Animations.spin(ball, Duration.seconds(9));

        Label line1 = new Label("International");
        line1.getStyleClass().add("app-title-small");

        Label line2 = new Label("Club Cup");
        line2.getStyleClass().add("app-title");

        VBox names = new VBox(line1, line2);
        names.setAlignment(Pos.CENTER_LEFT);

        HBox brand = new HBox(12, logo, names);
        brand.setAlignment(Pos.CENTER_LEFT);

        VBox header = new VBox(brand);
        header.getStyleClass().add("sidebar-header");
        return header;
    }

    private VBox buildNav() {
        VBox nav = new VBox();
        nav.getStyleClass().add("sidebar-nav");

        for (NavItem item : NavItem.values()) {
            Button button = Icons.with(new Button(item.getLabel()), item.getIcon());
            button.setGraphicTextGap(14);
            button.getStyleClass().add("nav-button");
            Animations.hoverNudge(button);
            button.setMaxWidth(Double.MAX_VALUE);
            button.setOnAction(event -> select(item));

            buttons.put(item, button);
            nav.getChildren().add(button);
        }
        return nav;
    }

    private Region buildSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private VBox buildFooter() {
        Label institution = Icons.with(new Label("UFASTA"), Icon.FLAG);
        institution.getStyleClass().add("footer-title");

        Label course = new Label("Programacion B \u00B7 2026");
        course.getStyleClass().add("footer-subtitle");

        VBox footer = new VBox(institution, course);
        footer.getStyleClass().add("sidebar-footer");
        return footer;
    }

    public void select(NavItem item) {
        if (selected != null) {
            buttons.get(selected).getStyleClass().remove(STYLE_ACTIVE);
        }
        selected = item;
        buttons.get(item).getStyleClass().add(STYLE_ACTIVE);

        if (onSelect != null) {
            onSelect.accept(item);
        }
    }

    public NavItem getSelected() {
        return selected;
    }

    public void setOnSelect(Consumer<NavItem> onSelect) {
        this.onSelect = onSelect;
    }
}
