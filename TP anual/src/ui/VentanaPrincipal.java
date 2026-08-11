package ui;

import modelo.Equipo;
import modelo.Jugador;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    private List<Equipo> equipos;
    private JList<Equipo> listaEquipos;
    private DefaultListModel<Equipo> modeloLista;
    private JTextArea areaDetalle;
    private JLabel labelResumen;

    public VentanaPrincipal(List<Equipo> equipos) {
        this.equipos = equipos;

        setTitle("Copa Internacional de Clubes - Equipos");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        armarInterfaz();
        cargarEquiposEnLista();
    }

    private void armarInterfaz() {
        setLayout(new BorderLayout(10, 10));

        // --- Panel izquierdo: lista de equipos ---
        modeloLista = new DefaultListModel<>();
        listaEquipos = new JList<>(modeloLista);
        listaEquipos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaEquipos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalleEquipoSeleccionado();
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaEquipos);
        scrollLista.setPreferredSize(new Dimension(260, 0));
        scrollLista.setBorder(BorderFactory.createTitledBorder("Equipos"));

        // --- Panel derecho: detalle del plantel ---
        areaDetalle = new JTextArea();
        areaDetalle.setEditable(false);
        areaDetalle.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scrollDetalle = new JScrollPane(areaDetalle);
        scrollDetalle.setBorder(BorderFactory.createTitledBorder("Plantel"));

        // --- Panel inferior: resumen (aprovecha getValoracionPromedioPlantel) ---
        labelResumen = new JLabel("Seleccione un equipo para ver su informacion.");
        labelResumen.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(scrollLista, BorderLayout.WEST);
        add(scrollDetalle, BorderLayout.CENTER);
        add(labelResumen, BorderLayout.SOUTH);
    }

    private void cargarEquiposEnLista() {
        for (Equipo e : equipos) {
            modeloLista.addElement(e);
        }
        if (!equipos.isEmpty()) {
            listaEquipos.setSelectedIndex(0);
        }
    }

    private void mostrarDetalleEquipoSeleccionado() {
        Equipo seleccionado = listaEquipos.getSelectedValue();
        if (seleccionado == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append(seleccionado).append("\n");
        sb.append("=".repeat(70)).append("\n\n");

        for (Jugador j : seleccionado.getPlantel()) {
            sb.append(j).append("\n");
        }

        areaDetalle.setText(sb.toString());
        areaDetalle.setCaretPosition(0);

        labelResumen.setText(String.format(
                "Plantel: %d jugadores | Edad promedio: %.1f años | Valoracion promedio: %.1f",
                seleccionado.getPlantel().size(),
                seleccionado.getEdadPromedioPlantel(),
                seleccionado.getValoracionPromedioPlantel()));
    }
}
