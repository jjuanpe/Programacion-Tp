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

        List<Team> ordenados = new ArrayList<>(teams);
        ordenados.sort(Comparator.comparingInt(Team::getRanking));

        List<Zone> zonas = new ArrayList<>();
        for (String nombre : ZONE_NAMES) {
            zonas.add(new Zone(nombre));
        }

        for (int bombo = 0; bombo < ZONE_COUNT; bombo++) {
            List<Team> equiposDelBombo = new ArrayList<>(
                    ordenados.subList(bombo * TEAMS_PER_ZONE, bombo * TEAMS_PER_ZONE + TEAMS_PER_ZONE));
            Collections.shuffle(equiposDelBombo, random);
            for (int zonaIndex = 0; zonaIndex < ZONE_COUNT; zonaIndex++) {
                zonas.get(zonaIndex).getTeams().add(equiposDelBombo.get(zonaIndex));
            }
        }

        return zonas;
    }
}
