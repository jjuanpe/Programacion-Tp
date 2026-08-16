package app;

import dataload.JsonTournamentLoader;
import dataload.TournamentData;
import model.team.Team;

public class Main {

    public static void main(String[] args) throws Exception {
        TournamentData data = new JsonTournamentLoader("TP anual/docs/torneo.json").load();

        System.out.println("Equipos: " + data.getTeams().size());
        for (Team t : data.getTeams()) {
            System.out.println(" - " + t.getName() + " (" + t.getCountry().getCountryName()
                    + ") jugadores: " + t.getPlayers().size());
        }

        System.out.println("Arbitros: " + data.getReferees().size());

        System.out.println("Warnings: " + data.getWarnings().size());
        for (String w : data.getWarnings()) {
            System.out.println(" ! " + w);
        }
    }
}