package de.timmi6790.mpstats.api.versions.v1.java.leaderboard;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.Striped;
import de.timmi6790.mpstats.api.versions.v1.java.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.repository.JavaLeaderboardRepository;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.models.Stat;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Service
@Log4j2
public class JavaLeaderboardService {
    private final JavaLeaderboardRepository javaLeaderboardRepository;

    private final Striped<Lock> leaderboardLock = Striped.lock(32);
    private final Cache<String, Leaderboard> leaderboardCache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build();

    @Autowired
    public JavaLeaderboardService(final JavaLeaderboardRepository javaLeaderboardRepository) {
        this.javaLeaderboardRepository = javaLeaderboardRepository;
    }

    private String getUniqName(final Game game, final Stat stat, final Board board) {
        return String.format(
                "%s-%s-%s",
                game.getRepositoryId(),
                stat.getRepositoryId(),
                board.getRepositoryId()
        );
    }

    private Lock getGLeaderboardLock(final Game game, final Stat stat, final Board board) {
        final String uniqName = this.getUniqName(game, stat, board);
        return this.leaderboardLock.get(uniqName);
    }

    private Optional<Leaderboard> getLeaderboardFromCache(final Game game, final Stat stat, final Board board) {
        final String uniqName = this.getUniqName(game, stat, board);
        return Optional.ofNullable(this.leaderboardCache.getIfPresent(uniqName));
    }

    private void addLeaderboardToCache(final Leaderboard leaderboard) {
        final String uniqName = this.getUniqName(leaderboard.getGame(), leaderboard.getStat(), leaderboard.getBoard());
        this.leaderboardCache.put(uniqName, leaderboard);
    }

    public List<Leaderboard> getLeaderboards() {
        return this.javaLeaderboardRepository.getLeaderboards();
    }

    public List<Leaderboard> getLeaderboards(final Game game) {
        return this.javaLeaderboardRepository.getLeaderboards(game);
    }

    public Optional<Leaderboard> getLeaderboard(final Game game, final Stat stat, final Board board) {
        final Optional<Leaderboard> leaderboardCache = this.getLeaderboardFromCache(game, stat, board);
        if (leaderboardCache.isPresent()) {
            return leaderboardCache;
        }

        final Optional<Leaderboard> leaderboardOpt = this.javaLeaderboardRepository.getLeaderboard(game, stat, board);
        leaderboardOpt.ifPresent(this::addLeaderboardToCache);

        return leaderboardOpt;
    }

    public Leaderboard getLeaderboardOrCreate(final Game game,
                                              final Stat stat,
                                              final Board board,
                                              final boolean deprecated) {
        final Lock lock = this.getGLeaderboardLock(game, stat, board);
        lock.lock();
        try {
            final Optional<Leaderboard> leaderboardOpt = this.getLeaderboard(game, stat, board);
            if (leaderboardOpt.isPresent()) {
                return leaderboardOpt.get();
            }

            final Leaderboard leaderboard = this.javaLeaderboardRepository.createdLeaderboard(game, stat, board, deprecated);
            this.addLeaderboardToCache(leaderboard);
            return leaderboard;
        } finally {
            lock.unlock();
        }
    }
}