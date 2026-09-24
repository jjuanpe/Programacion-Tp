package model.simulation;

import model.competition.StadiumDrawService;
import model.people.Referee;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Estado que hay que mantener vivo entre las 3 fases de la eliminatoria
 * (cuartos, semis, final) cuando se juegan por separado, en llamadas
 * distintas -- por ejemplo, una por cada click del usuario en la interfaz.
 *
 * Guarda las piezas que tienen que ser LAS MISMAS a lo largo de toda la
 * eliminatoria, no una nueva por cada fase:
 * - el sorteo de estadios ({@link StadiumDrawService}), para que ningun
 *   estadio se repita en TODA la eliminatoria (no solo dentro de una fase
 *   puntual)
 * - la lista de arbitros disponibles
 * - el generador de semillas, para que la simulacion completa siga siendo
 *   reproducible con la semilla original, sin importar en cuantos pasos se
 *   la juegue
 *
 * {@link KnockoutStageSimulator#startKnockoutStage(java.util.List, java.util.List, long)}
 * crea el contexto una sola vez, al principio; despues se pasa tal cual a
 * cada llamada de {@code playQuarterFinals}/{@code playSemiFinals}/
 * {@code playFinalMatch}.
 */
public final class KnockoutStageContext {

    private final StadiumDrawService stadiumDrawService;
    private final List<Referee> refereePool;
    private final Random seedGenerator;

    KnockoutStageContext(
            StadiumDrawService stadiumDrawService, List<Referee> refereePool, Random seedGenerator) {
        this.stadiumDrawService =
                Objects.requireNonNull(stadiumDrawService, "The stadium draw service is required");
        this.refereePool = Objects.requireNonNull(refereePool, "The referee pool is required");
        this.seedGenerator = Objects.requireNonNull(seedGenerator, "The seed generator is required");
    }

    StadiumDrawService getStadiumDrawService() {
        return stadiumDrawService;
    }

    List<Referee> getRefereePool() {
        return refereePool;
    }

    Random getSeedGenerator() {
        return seedGenerator;
    }
}
