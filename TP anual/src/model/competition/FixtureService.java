package model.competition;

import model.match.GroupMatch;
import model.team.Team;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FixtureService {

    private static final int ROUNDS = 3;

    public List<GroupMatch> generateFixture(Zone zone, LocalDate primeraFecha, int diasEntreFechas) {
        List<Team> equipos = zone.getTeams();
        if (equipos.size() != 4) {
            throw new IllegalArgumentException("La zona debe tener 4 equipos, tiene: " + equipos.size());
        }

        Team fijo = equipos.get(0);
        List<Team> rotan = new ArrayList<>(equipos.subList(1, equipos.size())); // los otros 3

        List<GroupMatch> fixture = new ArrayList<>();
        LocalDate fecha = primeraFecha;

        for (int ronda = 0; ronda < ROUNDS; ronda++) {
            Team local1 = fijo;
            Team visitante1 = rotan.get(2);
            Team local2 = rotan.get(0);
            Team visitante2 = rotan.get(1);


            if (ronda % 2 == 1) {
                Team tmp = local1;
                local1 = visitante1;
                visitante1 = tmp;
            }

            fixture.add(new GroupMatch(fecha, local1, visitante1, null, null));
            fixture.add(new GroupMatch(fecha, local2, visitante2, null, null));

            Collections.rotate(rotan, 1);
            fecha = fecha.plusDays(diasEntreFechas);
        }

        zone.getGroupMatches().addAll(fixture);
        return fixture;
    }
}
