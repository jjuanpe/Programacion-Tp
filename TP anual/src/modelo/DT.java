package modelo;

import java.time.LocalDate;

public class DT extends Persona {
    private Pais nacionalidad;
    private int cantidadTitulos;

    public DT(String nombreCompleto, LocalDate fechaNacimiento,
              String tipoDocumento, long numeroDocumento,
              Pais nacionalidad, int cantidadTitulos) {
        super(nombreCompleto, fechaNacimiento, tipoDocumento, numeroDocumento);
        this.nacionalidad = nacionalidad;
        this.cantidadTitulos = cantidadTitulos;
    }

    public Pais getNacionalidad() {
        return nacionalidad;
    }

    public int getCantidadTitulos() {
        return cantidadTitulos;
    }

    @Override
    public String toString() {
        return super.toString() + " - DT (" + nacionalidad + ", "
                + cantidadTitulos + " titulos)";
    }
}
