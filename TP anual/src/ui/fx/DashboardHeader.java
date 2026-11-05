package ui.fx;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardHeader extends HBox {

    private final Label subtitle;

    public DashboardHeader(ObservableValue<String> subtitleText) {
        getStyleClass().add("dashboard-header");
        setAlignment(Pos.CENTER_LEFT);

        this.subtitle = new Label();
        this.subtitle.textProperty().bind(subtitleText);
        this.subtitle.getStyleClass().add("dashboard-subtitle");

        getChildren().add(buildTitles());
    }

    private VBox buildTitles() {
        Label title = new Label("International Club Cup");
        title.getStyleClass().add("dashboard-title");

        VBox titles = new VBox(title, subtitle);
        titles.getStyleClass().add("dashboard-titles");
        return titles;
    }

    /** En el Dashboard el encabezado lleva la foto de fondo (ver .home-header en styles.css). */
    public void showPage(NavItem item) {
        getStyleClass().remove("home-header");
        if (item == NavItem.DASHBOARD) {
            getStyleClass().add("home-header");
        }
    }
}
