package modelo;

import java.time.LocalDate;

public class Referi extends Persona {
    private Pais nacionalidad;
    private int cantidadAniosReferato;

    public Referi(String nombreCompleto, LocalDate fechaNacimiento,
                  String tipoDocumento, long numeroDocumento,
                  Pais nacionalidad, int cantidadAniosReferato) {
        super(nombreCompleto, fechaNacimiento, tipoDocumento, numeroDocumento);
        this.nacionalidad = nacionalidad;
        this.cantidadAniosReferato = cantidadAniosReferato;
    }

    public Pais getNacionalidad() {
        return nacionalidad;
    }

    public int getCantidadAniosReferato() {
        return cantidadAniosReferato;
    }

    @Override
    public String toString() {
        return super.toString() + " - Referi (" + nacionalidad + ", "
                + cantidadAniosReferato + " años en el referato)";
    }
}
