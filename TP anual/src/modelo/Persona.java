package modelo;

import java.time.LocalDate;
import java.time.Period;

public abstract class Persona {
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private String tipoDocumento;
    private long numeroDocumento;

    public Persona(String nombreCompleto, LocalDate fechaNacimiento,
                   String tipoDocumento, long numeroDocumento) {
        this.nombreCompleto = nombreCompleto;
        this.fechaNacimiento = fechaNacimiento;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public long getNumeroDocumento() {
        return numeroDocumento;
    }

    // metodo comun a toda Persona: calcula la edad actual a partir de la fecha de nacimiento
    public int getEdad() {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return nombreCompleto + " (Doc: " + tipoDocumento + " " + numeroDocumento
                + ", " + getEdad() + " años)";
    }
}
