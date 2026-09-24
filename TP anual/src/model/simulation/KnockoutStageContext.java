package model.simulation;

import model.competition.StadiumDrawService;
import model.people.Referee;

import java.util.List;
import java.util.Objects;
import java.util.Random;

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
