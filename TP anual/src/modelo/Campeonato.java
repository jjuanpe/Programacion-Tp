package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Campeonato {
    private String nombre;
    private List<Zona> zonas;

    public Campeonato(String nombre) {
        this.nombre = nombre;
        this.zonas = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public List<Zona> getZonas() {
        return zonas;
    }

    /**
     * Sorteo equilibrado de 16 equipos en 4 zonas de 4, evitando que una
     * misma zona concentre varios equipos de ranking similar.
     *
     * Estrategia ("sistema de bombos", igual al usado en los mundiales reales):
     * 1) Se ordenan los 16 equipos por ranking.
     * 2) Se arman 4 "bombos" de 4 equipos cada uno (bombo 1 = los 4 mejor
     *    rankeados, bombo 4 = los 4 peor rankeados).
     * 3) Cada bombo se mezcla al azar, y se reparte UN equipo de cada bombo
     *    a cada una de las 4 zonas -> ninguna zona puede quedar con 4
     *    equipos del mismo nivel de ranking.
     */
    public void sortearZonas(List<Equipo> equipos) {
        if (equipos.size() != 16) {
            throw new IllegalArgumentException(
                    "Se requieren exactamente 16 equipos para el sorteo (se recibieron "
                            + equipos.size() + ")");
        }

        List<Equipo> ordenadosPorRanking = new ArrayList<>(equipos);
        ordenadosPorRanking.sort(Comparator.comparingInt(Equipo::getRanking));

        List<List<Equipo>> bombos = new ArrayList<>();
        for (int b = 0; b < 4; b++) {
            List<Equipo> bombo = new ArrayList<>(
                    ordenadosPorRanking.subList(b * 4, b * 4 + 4));
            Collections.shuffle(bombo);
            bombos.add(bombo);
        }

        zonas.clear();
        for (int z = 0; z < 4; z++) {
            Zona zona = new Zona("Zona " + (z + 1));
            for (List<Equipo> bombo : bombos) {
                zona.agregarEquipo(bombo.get(z));
            }
            zona.generarPartidos();
            zonas.add(zona);
        }
    }
}
