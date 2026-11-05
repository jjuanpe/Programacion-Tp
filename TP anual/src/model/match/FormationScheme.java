package model.match;

import model.people.Position;

// Fixed list of schemes. Each team picks one at random for every match.
public enum FormationScheme {

    F_4_4_2(4, 4, 2),
    F_4_3_3(4, 3, 3),
    F_4_5_1(4, 5, 1),
    F_3_5_2(3, 5, 2),
    F_3_4_3(3, 4, 3),
    F_5_3_2(5, 3, 2);

    private static final int GOALKEEPERS = 1;

    private final int defenders;
    private final int midfielders;
    private final int forwards;

    FormationScheme(int defenders, int midfielders, int forwards) {
        this.defenders = defenders;
        this.midfielders = midfielders;
        this.forwards = forwards;
    }

    public int startersAt(Position position) {
        int count;
        switch (position) {
            case GOALKEEPER -> count = GOALKEEPERS;
            case DEFENDER -> count = defenders;
            case MIDFIELDER -> count = midfielders;
            default -> count = forwards;
        }
        return count;
    }

    public String getLabel() {
        return new StringBuilder()
                .append(defenders).append('-')
                .append(midfielders).append('-')
                .append(forwards).toString();
    }
}
