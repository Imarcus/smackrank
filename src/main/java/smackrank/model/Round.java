package smackrank.model;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ElementCollection
    private Map<String, Integer> rankMap = new HashMap<>();
    private int round;

    public Round(){}

    public Round(List<Player> players, int round){
        this.round = round;
        insertPlayersToMap(players);
    }

    private void insertPlayersToMap(List<Player> players){
        for(Player player : players) {
            rankMap.put(player.getName(), player.getRating());
        }
    }

    public Map<String, Integer> getRankMap() {
        return rankMap;
    }

    public int getRound() {
        return round;
    }
}
