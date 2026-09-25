package model.competition;

import java.io.Serializable;
import model.venue.Stadium;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class StadiumDrawService implements Serializable {
    private final List<Stadium> remainingStadiums;

    public StadiumDrawService(List<Stadium> stadiums) {
        Objects.requireNonNull(stadiums, "The stadium list is required");
        this.remainingStadiums = new ArrayList<>(stadiums);
    }

    public synchronized Stadium draw(Random random) {
        Objects.requireNonNull(random, "The random generator is required");
        if (remainingStadiums.isEmpty()) {
            throw new IllegalStateException(
                    "There are no stadiums left for the knockout stage draw");
        }
        int index = random.nextInt(remainingStadiums.size());
        return remainingStadiums.remove(index);
    }
}
