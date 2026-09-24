package ui.fx;

import java.util.List;

public final class TeamDetail {

    private final String name;
    private final String country;
    private final int ranking;
    private final String coachName;
    private final String coachAge;
    private final String coachNationality;
    private final boolean squadValid;
    private final String squadStatus;
    private final List<PlayerRow> squad;

    public TeamDetail(
            String name,
            String country,
            int ranking,
            String coachName,
            String coachAge,
            String coachNationality,
            boolean squadValid,
            String squadStatus,
            List<PlayerRow> squad) {
        this.name = name;
        this.country = country;
        this.ranking = ranking;
        this.coachName = coachName;
        this.coachAge = coachAge;
        this.coachNationality = coachNationality;
        this.squadValid = squadValid;
        this.squadStatus = squadStatus;
        this.squad = List.copyOf(squad);
    }

    public String getName() { return name; }

    public String getCountry() { return country; }

    public int getRanking() { return ranking; }

    public String getCoachName() { return coachName; }

    public String getCoachAge() { return coachAge; }

    public String getCoachNationality() { return coachNationality; }

    public boolean isSquadValid() { return squadValid; }

    public String getSquadStatus() { return squadStatus; }

    public List<PlayerRow> getSquad() { return squad; }

    @Override
    public String toString() {
        return name;
    }
}
