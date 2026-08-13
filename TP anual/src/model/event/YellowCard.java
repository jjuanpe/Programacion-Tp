package model.event;

import model.match.Match;
import model.people.Player;

public class YellowCard extends Incidence {
     private Player player;

    public YellowCard(int minute, Match match, Player player) {
        super(minute, match);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
