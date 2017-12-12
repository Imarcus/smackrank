package smackrank.model.statistics;

import java.util.ArrayList;

public class LeagueStatistics {

    private String name;
    private int nrOfRounds;
    private ArrayList<PlayerStatistics> playerStatistics;

    public LeagueStatistics() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNrOfRounds() {
        return nrOfRounds;
    }

    public void setNrOfRounds(int nrOfRounds) {
        this.nrOfRounds = nrOfRounds;
    }

    public ArrayList<PlayerStatistics> getPlayerStatistics() {
        return playerStatistics;
    }

    public void setPlayerStatistics(ArrayList<PlayerStatistics> playerStatistics) {
        this.playerStatistics = playerStatistics;
    }
}
