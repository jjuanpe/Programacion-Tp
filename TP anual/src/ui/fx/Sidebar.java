package ui.fx;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Barra lateral izquierda de la aplicacion.
 *
 * Es un componente de interfaz autonomo: solo sabe dibujarse y avisar
 * (mediante {@link #setOnSelect(Consumer)}) que opcion eligio el usuario.
 * No instancia ni consulta clases del modelo, de modo que la logica del
 * dominio puede cambiar sin tocar esta clase.
 *
 * Por ahora la navegacion no esta implementada: los botones solo se ven
 * y la seleccion visual queda fija en Dashboard.
 */
public class Sidebar extends VBox {

    private static final String STYLE_ACTIVE = "active";

    private final Map<NavItem, Button> buttons = new EnumMap<>(NavItem.class);

    /** Punto de enganche para la futura navegacion (todavia sin uso). */
    private Consumer<NavItem> onSelect;

    private NavItem selected;

    public Sidebar() {
        getStyleClass().add("sidebar");

        getChildren().addAll(buildHeader(), buildNav(), buildSpacer(), buildFooter());

        select(NavItem.DASHBOARD);
    }

    /** Titulo de la aplicacion, en dos lineas. */
    private VBox buildHeader() {
        Label line1 = new Label("Copa Internacional");
        line1.getStyleClass().add("app-title");

        Label line2 = new Label("de Clubes");
        line2.getStyleClass().add("app-title");

        VBox header = new VBox(line1, line2);
        header.getStyleClass().add("sidebar-header");
        return header;
    }

    /** Lista de opciones. Un boton por cada valor de {@link NavItem}. */
    private VBox buildNav() {
        VBox nav = new VBox();
        nav.getStyleClass().add("sidebar-nav");

        for (NavItem item : NavItem.values()) {
            Button button = new Button(item.getLabel());
            button.getStyleClass().add("nav-button");
            button.setMaxWidth(Double.MAX_VALUE);
            button.setOnAction(event -> select(item));

            buttons.put(item, button);
            nav.getChildren().add(button);
        }
        return nav;
    }

    /** Empuja el pie hacia el fondo del sidebar. */
    private Region buildSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    /** Pie con los datos de la materia. */
    private VBox buildFooter() {
        Label institution = new Label("UFASTA");
        institution.getStyleClass().add("footer-title");

        Label course = new Label("Programacion B \u00B7 2026");
        course.getStyleClass().add("footer-subtitle");

        VBox footer = new VBox(institution, course);
        footer.getStyleClass().add("sidebar-footer");
        return footer;
    }

    /** Marca visualmente una opcion y avisa al oyente, si hay alguno. */
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

    /**
     * Registra quien debe reaccionar a los cambios de seccion.
     * Todavia nadie lo usa: es la costura prevista para conectar
     * el sidebar con el resto de la aplicacion.
     */
    public void setOnSelect(Consumer<NavItem> onSelect) {
        this.onSelect = onSelect;
    }
}
