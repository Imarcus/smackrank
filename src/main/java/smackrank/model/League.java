package smackrank.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class League {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "league_id")
	private Long id;
	private String name;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Player> players = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL)
	private List<Match> matches = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL)
	private List<Round> rounds = new ArrayList<>();

	private int currentRoundCount = 0;

	private int maxRankDeviation = 100;

	public League() {

	}
	public League(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void addPlayer(Player player){
		players.add(player);
	}

	public Player findPlayerByName(String name) {
		Optional<Player> optional= players.stream().filter(p -> p.getName().equals(name)).findFirst();
		if(optional.isPresent()) {
			return optional.get();
		} else {
			return null;
		}
	}

	public List<Round> getRounds() {
		return rounds;
	}

	public void addRound(Round round) {
		rounds.add(round);
		incrementRoundCount();
	}

	public int getCurrentRoundCount() {
		return currentRoundCount;
	}

	public List<Match> getMatches() {
		return matches;
	}

	public void addMatch(Match match) {
		matches.add(match);
	}

	private void incrementRoundCount() {
		currentRoundCount = currentRoundCount + 1;
	}

	public int getMaxRankDeviation() {
		return maxRankDeviation;
	}

	public void setMaxRankDeviation(int maxRankDeviation) {
		this.maxRankDeviation = maxRankDeviation;
	}

	@Override
	public String toString() {
		String s = "League: " + name;
		if(players.size() > 0) {
			for(Player player : players) {
				s = s + "\tplayer: " + player.toString();
			}
		}
		return s;
	}
}