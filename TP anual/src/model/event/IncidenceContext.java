package model.event;

import model.team.Team;

import java.util.List;

// What an incidence needs to know about the match it belongs to.
// Match implements it, so the event package does not depend on the match package.
public interface IncidenceContext {

    Team getHomeTeam();

    Team getAwayTeam();

    List<Incidence> getIncidences();

    Expulsion findFifthExpulsion(List<Incidence> events);
}
