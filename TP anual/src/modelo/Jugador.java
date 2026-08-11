package modelo;

import java.time.LocalDate;

public abstract class Jugador extends Persona {
    private int partidosJugados;
    private int expulsiones;

    public Jugador(String nombreCompleto, LocalDate fechaNacimiento,
                   String tipoDocumento, long numeroDocumento,
                   int partidosJugados, int expulsiones) {
        super(nombreCompleto, fechaNacimiento, tipoDocumento, numeroDocumento);
        this.partidosJugados = partidosJugados;
        this.expulsiones = expulsiones;
    }

    public int getPartidosJugados() {
        return partidosJugados;
    }

    public int getExpulsiones() {
        return expulsiones;
    }

    // metodo abstracto: cada subclase (Arquero / JugadorDeCampo) calcula
    // su valoracion con sus propias caracteristicas -> resolucion POLIMORFICA,
    // se usara para la simulacion de partidos en una fase posterior.
    public abstract double getValoracion();
}
