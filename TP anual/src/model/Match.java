package model;

import java.time.LocalDate;
import java.util.List;

public class Match {
    import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

    public abstract class Match {

        private LocalDate date;
        private Team homeTeam;
        private Team awayTeam;
        private Referee referee;
        private Stadium stadium;
        private int homeGoals;
        private int awayGoals;
        private Formation homeFormation;
        private Formation awayFormation;
        private List<PlayerParticipation> participations;
        private List<Incidence> incidences;

        public Match(LocalDate date, Team homeTeam, Team awayTeam, Referee referee, Stadium stadium) {
            this.date = date;
            this.homeTeam = homeTeam;
            this.awayTeam = awayTeam;
            this.referee = referee;
            this.stadium = stadium;
            this.participations = new ArrayList<>();
            this.incidences = new ArrayList<>();
        }

        public LocalDate getDate() {
            return date;
        }


        public Team getHomeTeam() {
            return homeTeam;
        }


        public Team getAwayTeam() {
            return awayTeam;
        }


        public Referee getReferee() {
            return referee;
        }


        public Stadium getStadium() {
            return stadium;
        }


        public int getHomeGoals() {
            return homeGoals;
        }

        public int getAwayGoals() {
            return awayGoals;
        }


        public Formation getHomeFormation() {
            return homeFormation;
        }


        public Formation getAwayFormation() {
            return awayFormation;
        }


        public List<PlayerParticipation> getParticipations() {
            return participations;
        }

        public void setParticipations(List<PlayerParticipation> participations) {
            this.participations = participations;
        }

        public List<Incidence> getIncidences() {
            return incidences;
        }

        public void setIncidences(List<Incidence> incidences) {
            this.incidences = incidences;
        }

    }
}
