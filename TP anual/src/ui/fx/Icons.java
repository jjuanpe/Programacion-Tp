package ui.fx;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

/**
 * Fabrica de iconos. Cada icono es una Region cuya forma es el trazo SVG,
 * asi el color se define desde la hoja de estilos con -fx-background-color.
 */
public final class Icons {

    private static final double DEFAULT_SIZE = 16;

    private Icons() {
    }

    public static Region of(Icon icon) {
        return of(icon, DEFAULT_SIZE);
    }

    public static Region of(Icon icon, double size) {
        SVGPath shape = new SVGPath();
        shape.setContent(icon.getPath());

        Region region = new Region();
        region.setShape(shape);
        region.getStyleClass().add("icon");
        region.setMinSize(size, size);
        region.setPrefSize(size, size);
        region.setMaxSize(size, size);
        return region;
    }

    /** Agrega el icono a la izquierda del texto de un boton o etiqueta. */
    public static <T extends Labeled> T with(T labeled, Icon icon) {
        labeled.setGraphic(of(icon));
        labeled.setContentDisplay(ContentDisplay.LEFT);
        labeled.setGraphicTextGap(8);
        return labeled;
    }

    /** Version para titulos de seccion: el icono va dentro de una burbuja de color. */
    public static <T extends Labeled> T titled(T labeled, Icon icon) {
        Region glyph = of(icon, 15);
        javafx.scene.layout.StackPane bubble = new javafx.scene.layout.StackPane(glyph);
        bubble.getStyleClass().add("icon-bubble");
        labeled.setGraphic(bubble);
        labeled.setContentDisplay(ContentDisplay.LEFT);
        labeled.setGraphicTextGap(10);
        return labeled;
    }
}
