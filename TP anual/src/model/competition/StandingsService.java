package model.competition;

import model.match.GroupMatch;
import model.team.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StandingsService {

    public List<StandingEntry> computeStandings(Zone zone) {
        Map<Team, StandingEntry> tabla = new LinkedHashMap<>();
        for (Team team : zone.getTeams()) {
            tabla.put(team, new StandingEntry(team));
        }

        for (GroupMatch match : zone.getGroupMatches()) {
            if (!match.isPlayed()) {
                continue;
            }

            Team local = match.getHomeTeam();
            Team visitante = match.getAwayTeam();
            int golesLocal = match.getHomeGoals();
            int golesVisitante = match.getAwayGoals();

            StandingEntry entradaLocal = tabla.get(local);
            StandingEntry entradaVisitante = tabla.get(visitante);

            if (golesLocal > golesVisitante) {
                entradaLocal.registerWin(golesLocal, golesVisitante);
                entradaVisitante.registerLoss(golesVisitante, golesLocal);
            } else if (golesLocal < golesVisitante) {
                entradaLocal.registerLoss(golesLocal, golesVisitante);
                entradaVisitante.registerWin(golesVisitante, golesLocal);
            } else {
                entradaLocal.registerDraw(golesLocal, golesVisitante);
                entradaVisitante.registerDraw(golesVisitante, golesLocal);
            }
        }

        List<StandingEntry> resultado = new ArrayList<>(tabla.values());
        resultado.sort(
                Comparator.comparingInt(StandingEntry::getPoints).reversed()
                        .thenComparing(Comparator.comparingInt(StandingEntry::getGoalDifference).reversed())
                        .thenComparing(Comparator.comparingInt(StandingEntry::getGoalsFor).reversed())
        );
        return resultado;
    }
}
