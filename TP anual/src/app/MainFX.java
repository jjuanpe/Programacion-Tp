package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainFX extends Application {

    static LocalDate f(String fechaDDMMYYYY) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(fechaDDMMYYYY, formato);
    }

    @Override
    public void start(Stage stage) {
        List<Equipo> equipos = crear16Equipos();

        Campeonato campeonato = new Campeonato("Copa Internacional de Clubes");
        campeonato.sortearZonas(equipos);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        for (Zona zona : campeonato.getZonas()) {
            Tab tab = new Tab(zona.getNombre());
            tab.setContent(armarContenidoZona(zona));
            tabPane.getTabs().add(tab);
        }

        Scene scene = new Scene(tabPane, 720, 480);
        stage.setTitle(campeonato.getNombre() + " - Fase de Grupos (sorteo)");
        stage.setScene(scene);
        stage.show();
    }

    private VBox armarContenidoZona(Zona zona) {
        VBox vbox = new VBox(12);
        vbox.setPadding(new Insets(15));

        Label tituloEquipos = new Label("Equipos de la zona:");
        tituloEquipos.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ListView<String> listaEquipos = new ListView<>();
        for (Equipo e : zona.getEquipos()) {
            listaEquipos.getItems().add(
                    e.getNombre() + "  (" + e.getPais() + " - Ranking: " + e.getRanking() + ")");
        }
        listaEquipos.setPrefHeight(110);

        Label tituloPartidos = new Label("Fixture (todos contra todos, una sola vez):");
        tituloPartidos.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ListView<String> listaPartidos = new ListView<>();
        for (PartidoZona p : zona.getPartidos()) {
            listaPartidos.getItems().add(p.toString());
        }

        vbox.getChildren().addAll(tituloEquipos, listaEquipos, tituloPartidos, listaPartidos);
        return vbox;
    }

    // -----------------------------------------------------------------
    // Datos de ejemplo para poder correr el sorteo (16 equipos requeridos)
    // -----------------------------------------------------------------
    private List<Equipo> crear16Equipos() {
        List<Equipo> equipos = new ArrayList<>();

        equipos.add(crearRiverPlate());
        equipos.add(equipoSimple("Bayern Munich", "Alemania", 1, "Kompany Vincent"));
        equipos.add(equipoSimple("Real Madrid", "España", 2, "Alonso Xabi"));
        equipos.add(equipoSimple("Manchester City", "Inglaterra", 3, "Guardiola Pep"));
        equipos.add(equipoSimple("Al Hilal", "Arabia Saudita", 9, "Inzaghi Simone"));
        equipos.add(equipoSimple("Flamengo", "Brasil", 6, "Filipe Luis"));
        equipos.add(equipoSimple("Boca Juniors", "Argentina", 11, "Úbeda Miguel Ángel"));
        equipos.add(equipoSimple("Juventus", "Italia", 7, "Tudor Igor"));
        equipos.add(equipoSimple("Paris Saint-Germain", "Francia", 4, "Luis Enrique"));
        equipos.add(equipoSimple("Ajax", "Países Bajos", 13, "Farioli Francesco"));
        equipos.add(equipoSimple("Urawa Red Diamonds", "Japón", 15, "Shoji Maciel"));
        equipos.add(equipoSimple("Al Ahly", "Egipto", 12, "Cardoso Marcel"));
        equipos.add(equipoSimple("Monterrey", "México", 10, "Berizzo Eduardo"));
        equipos.add(equipoSimple("Inter Miami", "Estados Unidos", 14, "Mascherano Javier"));
        equipos.add(equipoSimple("Fenerbahce", "Turquía", 8, "Mourinho Jose"));
        equipos.add(equipoSimple("LDU Quito", "Ecuador", 16, "Pellegrino Pablo"));

        return equipos;
    }

    // Equipo "liviano": alcanza con nombre/pais/ranking/DT para el sorteo
    // y el fixture. El plantel completo se carga en detalle solo para los
    // equipos que ya desarrollamos en la Fase 1/2 (ver crearRiverPlate()).
    private Equipo equipoSimple(String nombre, String pais, int ranking, String nombreDT) {
        DT dt = new DT(nombreDT, f("01/01/1975"), "PAS", 0, new Pais(pais), 0);
        return new Equipo(nombre, new Pais(pais), ranking, dt);
    }

    private Equipo crearRiverPlate() {
        DT dt = new DT("Gallardo Marcelo", f("18/07/1976"), "DNI", 25000000,
                new Pais("Argentina"), 14);

        Equipo equipo = new Equipo("River Plate", new Pais("Argentina"), 5, dt);

        equipo.agregarJugador(new Arquero(
                "Armani Franco", f("16/10/1986"), "DNI", 32500000,
                310, 2,
                90, 78, 88, 82, 85, 70,
                220, 30, 6));

        equipo.agregarJugador(new JugadorDeCampo(
                "Perez Enzo", f("22/02/1993"), "DNI", 36000000,
                290, 8,
                PosicionCampo.MEDIOCAMPISTA,
                78, 74, 88, 76, 82, 79, 90, 92,
                35, 5, 3, 60));

        equipo.agregarJugador(new JugadorDeCampo(
                "Borja Miguel", f("04/01/1996"), "PAS", 96000001,
                220, 5,
                PosicionCampo.DELANTERO,
                55, 88, 79, 84, 90, 86, 68, 75,
                85, 15, 11, 30));

        return equipo;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
