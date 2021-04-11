package de.timmi6790.mpstats.api.versions.v1.java.cache;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderboardCache extends CrudRepository<Leaderboard, Integer> {
}
