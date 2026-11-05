package ui.fx;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Animaciones reutilizables de la interfaz. Todas son cortas y suaves:
 * dan vida a la pantalla sin demorar al usuario.
 */
public final class Animations {

    private static final Duration ENTER = Duration.millis(380);
    private static final Duration STAGGER = Duration.millis(70);

    private Animations() {
    }

    /** Entrada de una pagina: aparece desde abajo con un fundido. */
    public static void enter(Node node) {
        enter(node, Duration.ZERO);
    }

    public static void enter(Node node, Duration delay) {
        node.setOpacity(0);
        node.setTranslateY(18);

        FadeTransition fade = new FadeTransition(ENTER, node);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(ENTER, node);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition both = new ParallelTransition(fade, slide);
        both.setDelay(delay);
        both.play();
    }

    /** Los hijos de un contenedor entran uno detras de otro, en cascada. */
    public static void cascade(Pane container) {
        Duration delay = Duration.ZERO;
        for (Node child : container.getChildren()) {
            enter(child, delay);
            delay = delay.add(STAGGER);
        }
    }

    /** Al pasar el mouse la tarjeta se eleva levemente. */
    public static void hoverLift(Node node) {
        ScaleTransition grow = new ScaleTransition(Duration.millis(160), node);
        TranslateTransition rise = new TranslateTransition(Duration.millis(160), node);
        node.setOnMouseEntered(event -> {
            grow.setToX(1.025);
            grow.setToY(1.025);
            rise.setToY(-3);
            grow.playFromStart();
            rise.playFromStart();
        });
        node.setOnMouseExited(event -> {
            grow.setToX(1);
            grow.setToY(1);
            rise.setToY(0);
            grow.playFromStart();
            rise.playFromStart();
        });
    }

    /** Al pasar el mouse el elemento se corre unos pixeles a la derecha. */
    public static void hoverNudge(Node node) {
        TranslateTransition move = new TranslateTransition(Duration.millis(140), node);
        node.setOnMouseEntered(event -> {
            move.setToX(5);
            move.playFromStart();
        });
        node.setOnMouseExited(event -> {
            move.setToX(0);
            move.playFromStart();
        });
    }

    /** Latido continuo (se usa en la etapa actual del torneo). */
    public static Animation pulse(Node node) {
        ScaleTransition beat = new ScaleTransition(Duration.millis(850), node);
        beat.setFromX(1);
        beat.setFromY(1);
        beat.setToX(1.35);
        beat.setToY(1.35);
        beat.setAutoReverse(true);
        beat.setCycleCount(Animation.INDEFINITE);
        beat.setInterpolator(Interpolator.EASE_BOTH);
        beat.play();
        return beat;
    }

    /** Respiracion suave, para banners destacados. */
    public static void breathe(Node node) {
        ScaleTransition beat = new ScaleTransition(Duration.seconds(1.6), node);
        beat.setToX(1.03);
        beat.setToY(1.03);
        beat.setAutoReverse(true);
        beat.setCycleCount(Animation.INDEFINITE);
        beat.setInterpolator(Interpolator.EASE_BOTH);
        beat.play();
    }

    /** Giro continuo, como una pelota rodando. */
    public static void spin(Node node, Duration lap) {
        RotateTransition rotate = new RotateTransition(lap, node);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();
    }

    /**
     * Muestra un numero contando desde el valor anterior hasta el nuevo.
     * Si el texto no es un numero, se muestra tal cual.
     */
    public static void countUp(Label label, ObservableValue<String> value) {
        SimpleIntegerProperty shown = new SimpleIntegerProperty();
        shown.addListener((observable, previous, current) -> label.setText(String.valueOf(current.intValue())));
        Timeline[] running = new Timeline[1];

        Runnable update = () -> {
            String text = value.getValue();
            Integer target = parse(text);
            if (running[0] != null) {
                running[0].stop();
            }
            if (target == null) {
                label.setText(text);
            } else {
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.millis(900), new KeyValue(shown, target, Interpolator.EASE_OUT)));
                running[0] = timeline;
                label.setText(String.valueOf(shown.get()));
                timeline.play();
            }
        };
        value.addListener((observable, previous, current) -> update.run());
        update.run();
    }

    private static Integer parse(String text) {
        Integer number;
        try {
            number = text == null ? null : Integer.valueOf(text.trim());
        } catch (NumberFormatException exception) {
            number = null;
        }
        return number;
    }
}
