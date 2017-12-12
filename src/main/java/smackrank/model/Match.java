package smackrank.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String homePlayerName;
    private String awayPlayerName;

    private int round;

    private int homePlayerScore;
    private int awayPlayerScore;

    private int homePlayerRank;
    private int awayPlayerRank;

    protected Match(){}

    public Match(String homePlayerName, String awayPlayerName, int homePlayerScore, int awayPlayerScore,
                 int homePlayerRank, int awayPlayerRank, int round) {
        this.homePlayerName = homePlayerName;
        this.awayPlayerName = awayPlayerName;
        this.homePlayerScore = homePlayerScore;
        this.awayPlayerScore = awayPlayerScore;
        this.homePlayerRank = homePlayerRank;
        this.awayPlayerRank = awayPlayerRank;
        this.round = round;
    }

    public String getHomePlayerName() {
        return homePlayerName;
    }

    public void setHomePlayerName(String homePlayerName) {
        this.homePlayerName = homePlayerName;
    }

    public String getAwayPlayerName() {
        return awayPlayerName;
    }

    public void setAwayPlayerName(String awayPlayerName) {
        this.awayPlayerName = awayPlayerName;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getHomePlayerScore() {
        return homePlayerScore;
    }

    public void setHomePlayerScore(int homePlayerScore) {
        this.homePlayerScore = homePlayerScore;
    }

    public int getAwayPlayerScore() {
        return awayPlayerScore;
    }

    public void setAwayPlayerScore(int awayPlayerScore) {
        this.awayPlayerScore = awayPlayerScore;
    }

    public int getHomePlayerRank() {
        return homePlayerRank;
    }

    public void setHomePlayerRank(int homePlayerRank) {
        this.homePlayerRank = homePlayerRank;
    }

    public int getAwayPlayerRank() {
        return awayPlayerRank;
    }

    public void setAwayPlayerRank(int awayPlayerRank) {
        this.awayPlayerRank = awayPlayerRank;
    }
}
