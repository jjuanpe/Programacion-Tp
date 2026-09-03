package ui.fx;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Pagina todavia no implementada.
 *
 * Existe para que la navegacion funcione de punta a punta desde ahora: cada
 * opcion del sidebar lleva a algun lado, aunque el contenido llegue despues.
 * No muestra datos de ejemplo: dice lo que va a mostrar y nada mas.
 */
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
