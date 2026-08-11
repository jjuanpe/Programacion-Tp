package modelo;

public enum PosicionCampo {
    DEFENSOR("Defensor"),
    MEDIOCAMPISTA("Mediocampista"),
    DELANTERO("Delantero");

    private final String descripcion;

    PosicionCampo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
