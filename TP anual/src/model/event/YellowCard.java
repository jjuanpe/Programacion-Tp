package model.event;

import model.competition.FairPlayEntry;
import model.people.Player;
import model.team.Team;

import java.util.Map;
import java.util.Objects;

public class YellowCard extends Incidence {
    private static final long serialVersionUID = 1L;
    private static final int FAIR_PLAY_POINTS = 1;
    private final Player player;

    public YellowCard(int minute, IncidenceContext match, Player player) {
        super(minute, match);
        this.player = Objects.requireNonNull(player, "The cautioned player is required");
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public int fairPlayPointsFor(Team team) {
        return team.getPlayers().contains(player) ? FAIR_PLAY_POINTS : 0;
    }

    @Override
    public void registerCardIn(Map<Player, FairPlayEntry> entries) {
        FairPlayEntry entry = entries.get(player);
        if (entry != null) {
            entry.registerYellowCard();
        }
    }

    @Override
    public String getTypeLabel() {
        return "Yellow card";
    }

    @Override
    public String getDetail() {
        return describeWithTeam(player);
    }

    @Override
    public String getDescription() {
        return new StringBuilder().append(getMinute()).append("' Yellow card: ")
                .append(player.getName()).toString();
    }
}
