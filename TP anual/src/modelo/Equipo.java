package modelo;

import java.util.ArrayList;
import java.util.List;

public class Equipo {
    private String nombre;
    private Pais pais;
    private int ranking;
    private DT dt;
    private List<Jugador> plantel;

    public Equipo(String nombre, Pais pais, int ranking, DT dt) {
        this.nombre = nombre;
        this.pais = pais;
        this.ranking = ranking;
        this.dt = dt;
        this.plantel = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public Pais getPais() {
        return pais;
    }

    public int getRanking() {
        return ranking;
    }

    public DT getDt() {
        return dt;
    }

    public List<Jugador> getPlantel() {
        return plantel;
    }

    public void agregarJugador(Jugador j) {
        plantel.add(j);
    }

    // suma cuantos jugadores hay de cada subtipo, sin usar instanceof:
    // se apoya en el metodo POLIMORFICO getValoracion() de cada Jugador
    public double getValoracionPromedioPlantel() {
        if (plantel.isEmpty()) return 0;
        double suma = 0;
        for (Jugador j : plantel) {
            suma += j.getValoracion();
        }
        return suma / plantel.size();
    }

    // edad promedio del plantel (se usa en el reporte VI del enunciado)
    public double getEdadPromedioPlantel() {
        if (plantel.isEmpty()) return 0;
        int suma = 0;
        for (Jugador j : plantel) {
            suma += j.getEdad();
        }
        return (double) suma / plantel.size();
    }

    @Override
    public String toString() {
        return nombre + " (" + pais + ") - Ranking: " + ranking
                + " - DT: " + dt.getNombreCompleto();
    }
}
