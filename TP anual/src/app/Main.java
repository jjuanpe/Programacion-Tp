package app;

import modelo.*;
import ui.VentanaPrincipal;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static LocalDate f(String fechaDDMMYYYY) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(fechaDDMMYYYY, formato);
    }

    public static void main(String[] args) {
        List<Equipo> equipos = new ArrayList<>();
        equipos.add(crearRiverPlate());
        equipos.add(crearMunchi());

        // Swing debe correr en su propio hilo (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(equipos);
            ventana.setVisible(true);
        });
    }

    private static Equipo crearRiverPlate() {
        DT dt = new DT("Gallardo Marcelo", f("18/07/1976"), "DNI", 25000000,
                new Pais("Argentina"), 14);

        Equipo equipo = new Equipo("River Plate", new Pais("Argentina"), 8, dt);

        equipo.agregarJugador(new Arquero(
                "Armani Franco", f("16/10/1986"), "DNI", 32500000,
                310, 2,
                90, 78, 88, 82, 85, 70,
                220, 30, 6));

        equipo.agregarJugador(new Arquero(
                "Rossi Jeremias", f("18/03/2001"), "DNI", 44000000,
                45, 0,
                80, 74, 79, 75, 72, 68,
                40, 5, 1));

        equipo.agregarJugador(new JugadorDeCampo(
                "Diaz Milton", f("28/01/1994"), "DNI", 37500000,
                180, 4,
                PosicionCampo.DEFENSOR,
                84, 80, 75, 70, 55, 62, 78, 88,
                12, 0, 0, 20));

        equipo.agregarJugador(new JugadorDeCampo(
                "Martinez Lucas", f("16/11/1996"), "DNI", 39500000,
                140, 5,
                PosicionCampo.DEFENSOR,
                80, 76, 73, 85, 50, 58, 74, 85,
                8, 0, 0, 15));

        equipo.agregarJugador(new JugadorDeCampo(
                "Perez Enzo", f("22/02/1993"), "DNI", 36000000,
                290, 8,
                PosicionCampo.MEDIOCAMPISTA,
                78, 74, 88, 76, 82, 79, 90, 92,
                35, 5, 3, 60));

        equipo.agregarJugador(new JugadorDeCampo(
                "De la Cruz Nicolas", f("11/12/1997"), "DNI", 41000000,
                150, 6,
                PosicionCampo.MEDIOCAMPISTA,
                72, 90, 86, 65, 78, 80, 87, 88,
                22, 2, 1, 35));

        equipo.agregarJugador(new JugadorDeCampo(
                "Borja Miguel", f("04/01/1996"), "PAS", 96000001,
                220, 5,
                PosicionCampo.DELANTERO,
                55, 88, 79, 84, 90, 86, 68, 75,
                85, 15, 11, 30));

        equipo.agregarJugador(new JugadorDeCampo(
                "Colidio Ezequiel", f("28/02/1999"), "DNI", 40500000,
                160, 4,
                PosicionCampo.DELANTERO,
                58, 91, 82, 70, 84, 83, 70, 78,
                48, 8, 6, 25));

        return equipo;
    }

    private static Equipo crearMunchi() {
        DT dt = new DT("Triumffen Otto", f("06/06/1945"), "DU", 212119,
                new Pais("Alemania"), 19);

        Equipo equipo = new Equipo("Munchi", new Pais("Alemania"), 1, dt);

        equipo.agregarJugador(new Arquero(
                "Atajen Karl", f("30/11/1985"), "DU", 21211,
                257, 1,
                93, 91, 75, 84, 80, 67,
                278, 8, 1));

        equipo.agregarJugador(new JugadorDeCampo(
                "Denfenden Otto", f("30/11/1995"), "DU", 21213,
                208, 1,
                PosicionCampo.DEFENSOR,
                77, 65, 73, 82, 66, 65, 75, 86,
                23, 0, 0, 11));

        equipo.agregarJugador(new JugadorDeCampo(
                "Pataduren Ludwig", f("30/11/2005"), "PAS", 21216,
                330, 3,
                PosicionCampo.DEFENSOR,
                68, 95, 93, 70, 73, 92, 87, 91,
                40, 0, 0, 73));

        equipo.agregarJugador(new JugadorDeCampo(
                "Robben Gunter", f("17/05/1994"), "PAS", 21219,
                344, 3,
                PosicionCampo.MEDIOCAMPISTA,
                69, 86, 58, 63, 71, 84, 61, 92,
                40, 4, 3, 37));

        equipo.agregarJugador(new JugadorDeCampo(
                "Implakablen Matteus", f("01/03/1994"), "DU", 212116,
                339, 0,
                PosicionCampo.DELANTERO,
                77, 75, 61, 94, 84, 98, 67, 90,
                126, 26, 20, 61));

        return equipo;
    }
}
