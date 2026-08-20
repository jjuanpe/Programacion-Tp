package model.competition;

import model.team.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;


public class DrawService {

    private static final int ZONE_COUNT = 4;
    private static final int TEAMS_PER_ZONE = 4;
    private static final String[] ZONE_NAMES = {"A", "B", "C", "D"};

    private final Random random;

    public DrawService() {
        this(new Random());
    }

    public DrawService(Random random) {
        this.random = random;
    }

    public List<Zone> draw(List<Team> teams) {
        if (teams == null || teams.size() != ZONE_COUNT * TEAMS_PER_ZONE) {
            throw new IllegalArgumentException(
                    "El sorteo requiere exactamente 16 equipos, llegaron: "
                            + (teams == null ? 0 : teams.size()));
        }

        List<Team> sortedTeams = new ArrayList<>(teams);
        sortedTeams.sort(Comparator.comparingInt(Team::getRanking));

        List<Zone> zones = new ArrayList<>();
        for (String name : ZONE_NAMES) {
            zones.add(new Zone(name));
        }

        for (int pot = 0; pot < ZONE_COUNT; pot++) {
            List<Team> teamsInPot = new ArrayList<>(
                    sortedTeams.subList(pot * TEAMS_PER_ZONE, pot * TEAMS_PER_ZONE + TEAMS_PER_ZONE));
            Collections.shuffle(teamsInPot, random);
            for (int zoneIndex = 0; zoneIndex < ZONE_COUNT; zoneIndex++) {
                zones.get(zoneIndex).addTeam(teamsInPot.get(zoneIndex));
            }
        }

        return zones;
    }
}
