package model.simulation;

import model.team.Team;
import model.match.Match;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class KnockoutStageResult {

    private final List<KnockoutTieReport> quarterFinals;
    private final List<KnockoutTieReport> semiFinals;
    private final FinalMatchReport finalMatchReport;

    public KnockoutStageResult(
            List<KnockoutTieReport> quarterFinals,
            List<KnockoutTieReport> semiFinals,
            FinalMatchReport finalMatchReport) {
        this.quarterFinals = List.copyOf(quarterFinals);
        this.semiFinals = List.copyOf(semiFinals);
        this.finalMatchReport = Objects.requireNonNull(finalMatchReport, "The final match report is required");
    }

    public List<KnockoutTieReport> getQuarterFinals() {
        return quarterFinals;
    }

    public List<KnockoutTieReport> getSemiFinals() {
        return semiFinals;
    }

    public FinalMatchReport getFinalMatchReport() {
        return finalMatchReport;
    }

    public Team getChampion() {
        return finalMatchReport.getChampion();
    }

    public List<Match> getMatches() {
        List<Match> matches = new ArrayList<>();
        for (KnockoutTieReport tie : quarterFinals) {
            matches.add(tie.getFirstLeg());
            matches.add(tie.getSecondLeg());
        }
        for (KnockoutTieReport tie : semiFinals) {
            matches.add(tie.getFirstLeg());
            matches.add(tie.getSecondLeg());
        }
        matches.add(finalMatchReport.getFinalMatch());
        return List.copyOf(matches);
    }
}
