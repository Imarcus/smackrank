package smackrank.model.statistics;

import smackrank.model.Rated;

import java.util.ArrayList;

public class PlayerStatistics implements Rated {

    private String name;
    private int rating;
    private int matchesPlayed;
    private int matchesWon;
    private int matchesLost;
    private int matchesDraw;
    private int totalScoreWon;
    private int totalScoreConceded;
    private String playerMostWon;
    private String playerMostLost;
    private double averageScoreWonPerMatch;
    private double averageScoreConcededPerMatch;
    private int highestRatingEver;
    private int lowestRatingEver;
    private String colour;
    private ArrayList<OpponentStatistic> opponentList = new ArrayList<>();

    public PlayerStatistics() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public int getMatchesWon() {
        return matchesWon;
    }

    public void setMatchesWon(int matchesWon) {
        this.matchesWon = matchesWon;
    }

    public int getMatchesLost() {
        return matchesLost;
    }

    public void setMatchesLost(int matchesLost) {
        this.matchesLost = matchesLost;
    }

    public int getMatchesDraw() {
        return matchesDraw;
    }

    public void setMatchesDraw(int matchesDraw) {
        this.matchesDraw = matchesDraw;
    }

    public int getTotalScoreWon() {
        return totalScoreWon;
    }

    public void setTotalScoreWon(int totalScoreWon) {
        this.totalScoreWon = totalScoreWon;
    }

    public int getTotalScoreConceded() {
        return totalScoreConceded;
    }

    public void setTotalScoreConceded(int totalScoreConceded) {
        this.totalScoreConceded = totalScoreConceded;
    }

    public String getPlayerMostWon() {
        return playerMostWon;
    }

    public void setPlayerMostWon(String playerMostWon) {
        this.playerMostWon = playerMostWon;
    }

    public String getPlayerMostLost() {
        return playerMostLost;
    }

    public void setPlayerMostLost(String playerMostLost) {
        this.playerMostLost = playerMostLost;
    }

    public double getAverageScoreWonPerMatch() {
        return averageScoreWonPerMatch;
    }

    public void setAverageScoreWonPerMatch(double averageScoreWonPerMatch) {
        this.averageScoreWonPerMatch = averageScoreWonPerMatch;
    }

    public double getAverageScoreConcededPerMatch() {
        return averageScoreConcededPerMatch;
    }

    public void setAverageScoreConcededPerMatch(double averageScoreConcededPerMatch) {
        this.averageScoreConcededPerMatch = averageScoreConcededPerMatch;
    }

    public ArrayList<OpponentStatistic> getOpponentList() {
        return opponentList;
    }

    public void setOpponentList(ArrayList<OpponentStatistic> opponentList) {
        this.opponentList = opponentList;
    }

    public int getHighestRatingEver() {
        return highestRatingEver;
    }

    public void setHighestRatingEver(int highestRatingEver) {
        this.highestRatingEver = highestRatingEver;
    }

    public int getLowestRatingEver() {
        return lowestRatingEver;
    }

    public void setLowestRatingEver(int lowestRatingEver) {
        this.lowestRatingEver = lowestRatingEver;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
