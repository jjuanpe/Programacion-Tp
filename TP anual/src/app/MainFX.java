package app;

import javafx.application.Application;
import ui.fx.AppWindow;

/**
 * Punto de entrada de la aplicacion de escritorio (JavaFX).
 *
 * Esta clase NO extiende {@link Application} a proposito. Cuando la clase
 * principal extiende Application, la JVM exige que JavaFX venga como modulo
 * (--module-path) y aborta con "JavaFX runtime components are missing" si no
 * es asi. Lanzando desde una clase comun, la aplicacion arranca tanto con
 * --module-path como con los jar de JavaFX en el classpath.
 */
public class MainFX {

    public static void main(String[] args) {
        Application.launch(AppWindow.class, args);
    }
}
