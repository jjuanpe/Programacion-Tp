package modelo;

import java.time.LocalDate;

public class Arquero extends Jugador {
    private int reflejos;
    private int juegoAereo;
    private int ubicacion;
    private int achique;
    private int seguridadManos;
    private int juegoPies;

    private int golesRecibidos;
    private int penalesRecibidos;
    private int penalesAtajados;

    public Arquero(String nombreCompleto, LocalDate fechaNacimiento,
                   String tipoDocumento, long numeroDocumento,
                   int partidosJugados, int expulsiones,
                   int reflejos, int juegoAereo, int ubicacion, int achique,
                   int seguridadManos, int juegoPies,
                   int golesRecibidos, int penalesRecibidos, int penalesAtajados) {
        super(nombreCompleto, fechaNacimiento, tipoDocumento, numeroDocumento,
                partidosJugados, expulsiones);
        this.reflejos = reflejos;
        this.juegoAereo = juegoAereo;
        this.ubicacion = ubicacion;
        this.achique = achique;
        this.seguridadManos = seguridadManos;
        this.juegoPies = juegoPies;
        this.golesRecibidos = golesRecibidos;
        this.penalesRecibidos = penalesRecibidos;
        this.penalesAtajados = penalesAtajados;
    }

    public int getReflejos() { return reflejos; }
    public int getJuegoAereo() { return juegoAereo; }
    public int getUbicacion() { return ubicacion; }
    public int getAchique() { return achique; }
    public int getSeguridadManos() { return seguridadManos; }
    public int getJuegoPies() { return juegoPies; }
    public int getGolesRecibidos() { return golesRecibidos; }
    public int getPenalesRecibidos() { return penalesRecibidos; }
    public int getPenalesAtajados() { return penalesAtajados; }

    @Override
    public double getValoracion() {
        // promedio simple de las 6 caracteristicas propias del arquero
        // (formula provisoria, a ajustar en la fase de simulacion)
        return (reflejos + juegoAereo + ubicacion + achique + seguridadManos + juegoPies) / 6.0;
    }

    @Override
    public String toString() {
        return super.toString() + " - Arquero (valoracion: "
                + String.format("%.1f", getValoracion()) + ")";
    }
}
