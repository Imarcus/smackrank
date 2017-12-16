package smackrank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import smackrank.model.League;
import smackrank.model.Match;
import smackrank.model.Player;
import smackrank.model.Round;
import smackrank.model.statistics.LeagueStatistics;
import smackrank.model.statistics.OpponentStatistic;
import smackrank.model.statistics.PlayerStatistics;
import smackrank.util.PlayerRatingComparator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
public class ApplicationController {

    private static final String[] colours = {"#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99",
                    "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6", "#6a3d9a", "#ffff99", "#b15928"};

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);


    @Autowired
    private LeagueRepository repository;

    @RequestMapping("/get-all-league-names")
    public ArrayList<String> getAllLeagues() {
        Iterable<League> leagueIter = repository.findAll();
        ArrayList<String> leagueList = new ArrayList<>();
        for(League league : leagueIter) {
            leagueList.add(league.getName());
        }
        return leagueList;
    }

    @RequestMapping("/play-match")
    public void playMatch(@RequestParam(value = "league") String leagueName,
                       @RequestParam(value = "homePlayer") String homePlayerName,
                       @RequestParam(value = "awayPlayer") String awayPlayerName,
                       @RequestParam(value = "homeScore") int homeScore,
                            @RequestParam(value = "awayScore") int awayScore) {
        if(homePlayerName.equals(awayPlayerName)) { return; }
        League league = repository.findByName(leagueName).get(0);
        if(league.getCurrentRoundCount() == 0) {
            Round roundZero = new Round(league.getPlayers(), league.getCurrentRoundCount());
            league.addRound(roundZero);
        }
        Player homePlayer = league.findPlayerByName(homePlayerName);
        Player awayPlayer = league.findPlayerByName(awayPlayerName);

        int newHomePlayerRank = EloRater.newRating(homePlayer.getRating(), awayPlayer.getRating(), calculateEloScore(homeScore, awayScore));
        int newAwayPlayerRank = EloRater.newRating(awayPlayer.getRating(), homePlayer.getRating(), calculateEloScore(awayScore, homeScore));
        Match match = new Match(homePlayerName, awayPlayerName, homeScore, awayScore, homePlayer.getRating(),
                awayPlayer.getRating(), league.getCurrentRoundCount());
        homePlayer.setRating(newHomePlayerRank);
        awayPlayer.setRating(newAwayPlayerRank);
        Round round = new Round(league.getPlayers(), league.getCurrentRoundCount());
        league.addRound(round);
        league.addMatch(match);
        league.setMaxRankDeviation(calculateMaxRankDeviation(league));
        repository.save(league);
    }

    @RequestMapping("/get-league")
    public League getLeague(@RequestParam(value = "league") String leagueName) {
        League league = repository.findByName(leagueName).get(0);
        return league;
    }

    @RequestMapping("/create-league")
    public void createLeague(@RequestParam(value = "name") String name) {
        repository.save(new League(name));
    }

    @RequestMapping("/create-league-with-players")
    public void createLeagueWithPlayers(@RequestParam(value = "leagueName")String leagueName, @RequestParam(value = "playerNames") String playerNames) {
        League league = new League(leagueName);
        ArrayList<String> playerNamesList = new ArrayList<>(Arrays.asList(playerNames.replace(" ","").split(",")));
        playerNamesList.stream().forEach(p -> league.addPlayer(new Player(p, colours[league.getPlayers().size() % colours.length])));
        repository.save(league);
    }

    @RequestMapping("/add-player")
    public void addPlayer(@RequestParam(value = "leagueName") String leagueName,
                          @RequestParam(value = "playerName") String playerName) {
        League league = repository.findByName(leagueName).get(0);
        if(league.getPlayers().stream().filter(p -> p.getName().equals(playerName)).findFirst().isPresent()){
            throw new IllegalArgumentException("Player with name " + playerName + " already exists in league");
        }
        league.addPlayer(new Player(playerName, colours[league.getPlayers().size() % colours.length]));
        repository.save(league);
    }

    @RequestMapping("/get-players-sorted")
    public List<Player> getPlayersSorted(@RequestParam(value = "league") String leagueName) {
        List<Player> players = repository.findByName(leagueName).get(0).getPlayers();
        Collections.sort(players, new PlayerRatingComparator());
        Collections.reverse(players);
        return players;
    }

    @RequestMapping("/get-player")
    public Player getPlayer(@RequestParam(value = "league") String leagueName,
                            @RequestParam(value = "player") String playerName) {
        League league = repository.findByName(leagueName).get(0);
        return league.findPlayerByName(playerName);
    }

    @RequestMapping("/get-league-statistics")
    public LeagueStatistics getLeagueStatistics(@RequestParam(value = "league") String leagueName) {
        League league = repository.findByName(leagueName).get(0);
        LeagueStatistics leagueStatistics = new LeagueStatistics();
        leagueStatistics.setName(leagueName);
        leagueStatistics.setNrOfRounds(league.getRounds().size());
        leagueStatistics.setPlayerStatistics(new ArrayList<>());
        for(Player player : league.getPlayers()) {
            leagueStatistics.getPlayerStatistics().add(calculatePlayerStatistics(league, player));
        }
        Collections.sort(leagueStatistics.getPlayerStatistics(), new PlayerRatingComparator());
        Collections.reverse(leagueStatistics.getPlayerStatistics());
        return leagueStatistics;
    }

    @RequestMapping("/get-player-statistics")
    public PlayerStatistics getPlayerStatistics(@RequestParam(value = "league") String leagueName,
                                                @RequestParam(value = "player") String playerName) {
        League league = repository.findByName(leagueName).get(0);
        Player player = league.findPlayerByName(playerName);
        return calculatePlayerStatistics(league, player);
    }

    public int calculateMaxRankDeviation(League league) {
        int maxRankDeviation = 80;
        for(Round round : league.getRounds()) {
            for(int rank : round.getRankMap().values()){
                int rankDeviation = Math.abs(rank - 1500);
                if(rankDeviation > maxRankDeviation) {
                    maxRankDeviation = rankDeviation;
                }
            }
        }
        return maxRankDeviation + 20;
    }

    //1 if homeplayer wins, 0.5 if draw, 0 if away wins
    private double calculateEloScore(int homePlayerScore, int awayPlayerScore) {
        if( homePlayerScore > awayPlayerScore) {
            return 1;
        } else if (homePlayerScore == awayPlayerScore) {
            return 0.5;
        } else {
            return 0;
        }
    }

    private PlayerStatistics calculatePlayerStatistics(League league, Player player) {
        int nrWon = 0;
        int nrLost = 0;
        int nrDraw = 0;
        int totalScore = 0;
        int totalConceded = 0;
        ArrayList<OpponentStatistic> opponentList = new ArrayList<>();

        String name = player.getName();
        for(Match match : league.getMatches()) {
            if(match.getHomePlayerName().equals(name)) {
                totalScore = totalScore + match.getHomePlayerScore();
                totalConceded = totalConceded + match.getAwayPlayerScore();
                if(match.getHomePlayerScore() > match.getAwayPlayerScore()) {
                    nrWon++;
                } else if (match.getHomePlayerScore() < match.getAwayPlayerScore()) {
                    nrLost++;
                } else {
                    nrDraw++;
                }
                calculateOpponentStatistic(opponentList, match, true);
            } else if (match.getAwayPlayerName().equals(name)){
                totalScore = totalScore + match.getAwayPlayerScore();
                totalConceded = totalConceded + match.getHomePlayerScore();
                if(match.getHomePlayerScore() < match.getAwayPlayerScore()) {
                    nrWon++;
                } else if (match.getHomePlayerScore() > match.getAwayPlayerScore()) {
                    nrLost++;
                } else {
                    nrDraw++;
                }
                calculateOpponentStatistic(opponentList, match, false);
            }
        }

        int maxWon = 0;
        int maxLost = 0;
        String maxWonName = "";
        String maxLostName = "";
        for(OpponentStatistic opponentStatistic : opponentList) {
            if(opponentStatistic.getTimesWon() > maxWon) {
                maxWon = opponentStatistic.getTimesWon();
                maxWonName = opponentStatistic.getName();
            }
            if(opponentStatistic.getTimesLost() > maxLost) {
                maxLost = opponentStatistic.getTimesLost();
                maxLostName = opponentStatistic.getName();
            }
        }


        int lowestRating = 1500;
        int highestRating = 1500;
        for(Round round : league.getRounds()) {
            if(round.getRankMap().containsKey(name)) {
                int rating = round.getRankMap().get(name);
                if(rating > highestRating) {
                    highestRating = rating;
                } else if (rating < lowestRating) {
                    lowestRating = rating;
                }
            }
        }

        int matchesPlayed = nrWon + nrLost + nrDraw;
        double averageScoreWonPerMatch = round((double) totalScore / matchesPlayed, 2);
        double averageScoreConcededPerMatch = round((double) totalConceded / matchesPlayed, 2);

        PlayerStatistics playerStatistics = new PlayerStatistics();
        playerStatistics.setName(name);
        playerStatistics.setRating(player.getRating());
        playerStatistics.setColour(player.getColour());
        playerStatistics.setMatchesPlayed(matchesPlayed);
        playerStatistics.setMatchesWon(nrWon);
        playerStatistics.setMatchesLost(nrLost);
        playerStatistics.setMatchesDraw(nrDraw);
        playerStatistics.setTotalScoreWon(totalScore);
        playerStatistics.setTotalScoreConceded(totalConceded);
        playerStatistics.setAverageScoreWonPerMatch(averageScoreWonPerMatch);
        playerStatistics.setAverageScoreConcededPerMatch(averageScoreConcededPerMatch);
        playerStatistics.setHighestRatingEver(highestRating);
        playerStatistics.setLowestRatingEver(lowestRating);
        playerStatistics.setPlayerMostLost(maxLostName);
        playerStatistics.setPlayerMostWon(maxWonName);
        playerStatistics.setOpponentList(opponentList);
        return playerStatistics;

    }

    private void calculateOpponentStatistic(ArrayList<OpponentStatistic> opponentList,
                                                         Match match, boolean isHome) {
        OpponentStatistic stat;
        if(isHome) {
            stat = opponentList.stream().filter(o -> o.getName().equals(match.getAwayPlayerName())).findFirst().orElse(new OpponentStatistic());
            stat.setName(match.getAwayPlayerName());

            if(match.getHomePlayerScore() > match.getAwayPlayerScore()) {
                stat.setTimesWon(stat.getTimesWon() + 1);
            } else if (match.getHomePlayerScore() < match.getAwayPlayerScore()) {
                stat.setTimesLost(stat.getTimesWon() + 1);
            } else {
                stat.setTimesDraw(stat.getTimesDraw() + 1);
            }
            stat.setTotalScored(stat.getTotalScored() + match.getHomePlayerScore());
            stat.setTotalConceded(stat.getTotalConceded() + match.getAwayPlayerScore());
        } else {
            stat = opponentList.stream().filter(o -> o.getName().equals(match.getHomePlayerName())).findFirst().orElse(new OpponentStatistic());
            stat.setName(match.getHomePlayerName());

            if(match.getHomePlayerScore() < match.getAwayPlayerScore()) {
                stat.setTimesWon(stat.getTimesWon() + 1);
            } else if (match.getHomePlayerScore() > match.getAwayPlayerScore()) {
                stat.setTimesLost(stat.getTimesWon() + 1);
            } else {
                stat.setTimesDraw(stat.getTimesDraw() + 1);
            }
            stat.setTotalScored(stat.getTotalScored() + match.getHomePlayerScore());
            stat.setTotalConceded(stat.getTotalConceded() + match.getAwayPlayerScore());
        }
        stat.setTimesPlayed(stat.getTimesPlayed() + 1);
        opponentList.removeIf(o -> o.getName().equals(stat.getName()));
        opponentList.add(stat);
    }

    public static double round(double value, int places) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0;
        }
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}