package ui.fx;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Encabezado superior del area central.
 *
 * Muestra el titulo del torneo, la fase en curso y el estado de guardado.
 * Igual que {@link Sidebar}, es un componente de interfaz puro: la fase le
 * llega como valor observable y no conoce ninguna clase del dominio.
 */
public class DashboardHeader extends HBox {

    private final Label subtitle;
    private final Label status;

    public DashboardHeader(ObservableValue<String> subtitleText) {
        getStyleClass().add("dashboard-header");
        setAlignment(Pos.CENTER_LEFT);

        this.subtitle = new Label();
        this.subtitle.textProperty().bind(subtitleText);
        this.subtitle.getStyleClass().add("dashboard-subtitle");

        this.status = new Label("Saved");
        this.status.getStyleClass().add("status-label");

        getChildren().addAll(buildTitles(), buildSpacer(), buildStatus());
    }

    /** Titulo principal y subtitulo, apilados a la izquierda. */
    private VBox buildTitles() {
        Label title = new Label("Copa Internacional de Clubes");
        title.getStyleClass().add("dashboard-title");

        VBox titles = new VBox(title, subtitle);
        titles.getStyleClass().add("dashboard-titles");
        return titles;
    }

    /** Separa el bloque de titulos del estado y hace que el encabezado ocupe todo el ancho. */
    private Region buildSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    /** Indicador verde + texto de estado, alineado a la derecha. */
    private HBox buildStatus() {
        Region dot = new Region();
        dot.getStyleClass().add("status-dot");

        HBox badge = new HBox(dot, status);
        badge.getStyleClass().add("status-badge");
        badge.setAlignment(Pos.CENTER);
        badge.setMinWidth(Region.USE_PREF_SIZE);
        return badge;
    }

    /** Texto del indicador de estado. */
    public void setStatus(String text) {
        status.setText(text);
    }
}
