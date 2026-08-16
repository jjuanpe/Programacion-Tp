package model.people;

import java.time.LocalDate;

public class FieldPlayer extends Player {

    private final int speed;
    private final int stamina;
    private final int skill;
    private final int finishing;
    private final int shotPower;
    private final int heading;
    private final int tackling;
    private final int vision;

    public FieldPlayer(String name, LocalDate birthday, DocumentType documentType, String document, Position position,
                        int speed, int stamina, int skill, int finishing, int shotPower, int heading, int tackling, int vision) {
        super(name, birthday, documentType, document, position,
                computeAverage(speed, stamina, skill, finishing, shotPower, heading, tackling, vision));
        this.speed = speed;
        this.stamina = stamina;
        this.skill = skill;
        this.finishing = finishing;
        this.shotPower = shotPower;
        this.heading = heading;
        this.tackling = tackling;
        this.vision = vision;
    }

    public int getSpeed() {
        return speed;
    }

    public int getStamina() {
        return stamina;
    }

    public int getSkill() {
        return skill;
    }

    public int getFinishing() {
        return finishing;
    }

    public int getShotPower() {
        return shotPower;
    }

    public int getHeading() {
        return heading;
    }

    public int getTackling() {
        return tackling;
    }

    public int getVision() {
        return vision;
    }
}
