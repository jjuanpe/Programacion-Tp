package model.people;

import java.time.LocalDate;

public class Goalkeeper extends Player {

    private final int reflexes;
    private final int handling;
    private final int aerialAbility;
    private final int rushingOut;
    private final int positioning;
    private final int footwork;

    public Goalkeeper(String name, LocalDate birthday, DocumentType documentType, String document,
                       int reflexes, int handling, int aerialAbility, int rushingOut, int positioning, int footwork) {
        super(name, birthday, documentType, document, Position.GOALKEEPER,
                computeAverage(reflexes, handling, aerialAbility, rushingOut, positioning, footwork));
        this.reflexes = reflexes;
        this.handling = handling;
        this.aerialAbility = aerialAbility;
        this.rushingOut = rushingOut;
        this.positioning = positioning;
        this.footwork = footwork;
    }

    public int getReflexes() {
        return reflexes;
    }

    public int getHandling() {
        return handling;
    }

    public int getAerialAbility() {
        return aerialAbility;
    }

    public int getRushingOut() {
        return rushingOut;
    }

    public int getPositioning() {
        return positioning;
    }

    public int getFootwork() {
        return footwork;
    }
}
