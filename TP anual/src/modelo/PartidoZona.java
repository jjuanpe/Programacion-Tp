package modelo;

public class PartidoZona {
    private Equipo equipoLocal;
    private Equipo equipoVisitante;

    public PartidoZona(Equipo equipoLocal, Equipo equipoVisitante) {
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
    }

    public Equipo getEquipoLocal() {
        return equipoLocal;
    }

    public Equipo getEquipoVisitante() {
        return equipoVisitante;
    }

    @Override
    public String toString() {
        return equipoLocal.getNombre() + "  vs  " + equipoVisitante.getNombre();
    }
}
