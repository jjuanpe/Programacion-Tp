package model.competition;

import model.event.Goal;
import model.event.Incidence;
import model.match.Match;
import model.people.Player;
import model.team.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Calcula el ranking de goleadores del campeonato.
 *
 * Reglas aplicadas:
 *
 * - Solo cuentan las incidencias de tipo {@link Goal}, es decir los goles
 *   convertidos durante el partido.
 * - Los penales de una definicion por penales NO cuentan: la tanda se registra
 *   como {@link model.event.PenaltyExecuted}, una incidencia distinta, asi que
 *   queda afuera por construccion. {@link Match#completeMatch} refuerza esto al
 *   exigir que la cantidad de goles registrados coincida con el marcador.
 * - Un penal convertido durante el partido si cuenta, y ademas suma en la
 *   columna de penales.
 * - Los goles en contra no se le acreditan al jugador que los hizo: no son
 *   goles convertidos por el.
 * - Los jugadores sin goles no aparecen en el ranking.
 */
public class TopScorersService {

    private static final Comparator<ScorerEntry> RANKING_CRITERIA =
            Comparator.comparingInt(ScorerEntry::getGoals).reversed()
                    .thenComparingInt(ScorerEntry::getPenaltyGoals)
                    .thenComparing(entry -> entry.getPlayer().getName());

    /**
     * @param teams   equipos del campeonato, usados para resolver a que equipo
     *                pertenece cada goleador
     * @param matches partidos a considerar; los no jugados se ignoran
     */
    public List<ScorerEntry> computeTopScorers(List<Team> teams, List<? extends Match> matches) {
        Objects.requireNonNull(teams, "The team list is required");
        Objects.requireNonNull(matches, "The match list is required");

        Map<Player, Team> teamsByPlayer = indexPlayersByTeam(teams);
        Map<Player, ScorerEntry> scorers = new LinkedHashMap<>();

        for (Match match : matches) {
            if (match.isPlayed()) {
                registerMatchGoals(match, teamsByPlayer, scorers);
            }
        }

        List<ScorerEntry> ranking = new ArrayList<>(scorers.values());
        ranking.sort(RANKING_CRITERIA);
        assignPositions(ranking);
        return ranking;
    }

    private Map<Player, Team> indexPlayersByTeam(List<Team> teams) {
        Map<Player, Team> teamsByPlayer = new HashMap<>();
        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                teamsByPlayer.put(player, team);
            }
        }
        return teamsByPlayer;
    }

    private void registerMatchGoals(
            Match match,
            Map<Player, Team> teamsByPlayer,
            Map<Player, ScorerEntry> scorers) {
        for (Incidence incidence : match.getIncidences()) {
            if (incidence instanceof Goal goal && !goal.isOwnGoal()) {
                registerGoal(goal, teamsByPlayer, scorers);
            }
        }
    }

    private void registerGoal(
            Goal goal,
            Map<Player, Team> teamsByPlayer,
            Map<Player, ScorerEntry> scorers) {
        Player scorer = goal.getScorer();
        ScorerEntry entry = scorers.get(scorer);
        if (entry == null) {
            entry = new ScorerEntry(scorer, teamsByPlayer.get(scorer));
            scorers.put(scorer, entry);
        }
        entry.registerGoal(goal.isPenalty());
    }

    /**
     * Numera el ranking. Los jugadores empatados en goles comparten posicion y
     * la siguiente salta (1, 2, 2, 4), como en cualquier tabla de goleadores.
     */
    private void assignPositions(List<ScorerEntry> ranking) {
        int position = 0;
        int index = 0;
        int previousGoals = -1;
        for (ScorerEntry entry : ranking) {
            index++;
            if (entry.getGoals() != previousGoals) {
                position = index;
                previousGoals = entry.getGoals();
            }
            entry.setPosition(position);
        }
    }
}
