package modelo;

import java.time.LocalDate;

public class JugadorDeCampo extends Jugador {
    private PosicionCampo posicion;

    private int capacidadQuite;
    private int velocidad;
    private int habilidad;
    private int cabezazo;
    private int definicion;
    private int potenciaDisparo;
    private int visionDeJuego;
    private int resistenciaFisica;

    private int goles;
    private int penales;
    private int penalesConvertidos;
    private int pasesGol;

    public JugadorDeCampo(String nombreCompleto, LocalDate fechaNacimiento,
                          String tipoDocumento, long numeroDocumento,
                          int partidosJugados, int expulsiones,
                          PosicionCampo posicion,
                          int capacidadQuite, int velocidad, int habilidad, int cabezazo,
                          int definicion, int potenciaDisparo, int visionDeJuego, int resistenciaFisica,
                          int goles, int penales, int penalesConvertidos, int pasesGol) {
        super(nombreCompleto, fechaNacimiento, tipoDocumento, numeroDocumento,
                partidosJugados, expulsiones);
        this.posicion = posicion;
        this.capacidadQuite = capacidadQuite;
        this.velocidad = velocidad;
        this.habilidad = habilidad;
        this.cabezazo = cabezazo;
        this.definicion = definicion;
        this.potenciaDisparo = potenciaDisparo;
        this.visionDeJuego = visionDeJuego;
        this.resistenciaFisica = resistenciaFisica;
        this.goles = goles;
        this.penales = penales;
        this.penalesConvertidos = penalesConvertidos;
        this.pasesGol = pasesGol;
    }

    public PosicionCampo getPosicion() { return posicion; }
    public int getCapacidadQuite() { return capacidadQuite; }
    public int getVelocidad() { return velocidad; }
    public int getHabilidad() { return habilidad; }
    public int getCabezazo() { return cabezazo; }
    public int getDefinicion() { return definicion; }
    public int getPotenciaDisparo() { return potenciaDisparo; }
    public int getVisionDeJuego() { return visionDeJuego; }
    public int getResistenciaFisica() { return resistenciaFisica; }
    public int getGoles() { return goles; }
    public int getPenales() { return penales; }
    public int getPenalesConvertidos() { return penalesConvertidos; }
    public int getPasesGol() { return pasesGol; }

    @Override
    public double getValoracion() {
        // promedio simple de las 8 caracteristicas propias del jugador de campo
        // (formula provisoria, a ajustar en la fase de simulacion)
        return (capacidadQuite + velocidad + habilidad + cabezazo + definicion
                + potenciaDisparo + visionDeJuego + resistenciaFisica) / 8.0;
    }

    @Override
    public String toString() {
        return super.toString() + " - " + posicion
                + " (valoracion: " + String.format("%.1f", getValoracion()) + ")";
    }
}
