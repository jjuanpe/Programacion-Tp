package modelo;

import java.util.ArrayList;
import java.util.List;

public class Zona {
    private String nombre;
    private List<Equipo> equipos;
    private List<PartidoZona> partidos;

    public Zona(String nombre) {
        this.nombre = nombre;
        this.equipos = new ArrayList<>();
        this.partidos = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public List<Equipo> getEquipos() {
        return equipos;
    }

    public List<PartidoZona> getPartidos() {
        return partidos;
    }

    public void agregarEquipo(Equipo e) {
        equipos.add(e);
    }

    // genera los enfrentamientos de "todos contra todos, una sola vez" (6 partidos
    // para 4 equipos: no hay ida y vuelta, segun el enunciado del campeonato)
    public void generarPartidos() {
        partidos.clear();
        for (int i = 0; i < equipos.size(); i++) {
            for (int j = i + 1; j < equipos.size(); j++) {
                partidos.add(new PartidoZona(equipos.get(i), equipos.get(j)));
            }
        }
    }
}
