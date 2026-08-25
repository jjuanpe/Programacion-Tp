package model.competition;

import model.venue.Stadium;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Randomly assigns stadiums for the knockout stage matches, guaranteeing
 * that each stadium is used at most once across the whole knockout stage.
 * Since several knockout ties are simulated concurrently (one thread per
 * tie), {@link #draw(Random)} is synchronized to avoid two threads drawing
 * the same stadium at the same time.
 */
public class StadiumDrawService {

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
