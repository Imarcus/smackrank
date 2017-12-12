package smackrank.model.statistics;

public class OpponentStatistic {

    private String name;
    private int timesPlayed = 0;
    private int timesWon = 0;
    private int timesLost = 0;
    private int timesDraw = 0;
    private int totalScored = 0;
    private int totalConceded = 0;

    public OpponentStatistic() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimesPlayed() {
        return timesPlayed;
    }

    public void setTimesPlayed(int timesPlayed) {
        this.timesPlayed = timesPlayed;
    }

    public int getTimesWon() {
        return timesWon;
    }

    public void setTimesWon(int timesWon) {
        this.timesWon = timesWon;
    }

    public int getTimesLost() {
        return timesLost;
    }

    public void setTimesLost(int timesLost) {
        this.timesLost = timesLost;
    }

    public int getTimesDraw() {
        return timesDraw;
    }

    public void setTimesDraw(int timesDraw) {
        this.timesDraw = timesDraw;
    }

    public int getTotalScored() {
        return totalScored;
    }

    public void setTotalScored(int totalScored) {
        this.totalScored = totalScored;
    }

    public int getTotalConceded() {
        return totalConceded;
    }

    public void setTotalConceded(int totalConceded) {
        this.totalConceded = totalConceded;
    }
}
