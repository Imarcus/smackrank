package smackrank;

import org.springframework.data.repository.CrudRepository;
import smackrank.model.League;


import java.util.List;

public interface LeagueRepository extends CrudRepository<League, Long> {

    List<League> findByName(String name);
}
