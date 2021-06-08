package de.timmi6790.mpstats.api.versions.v1.common.leaderboard;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.Striped;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.LeaderboardRepository;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.postgres.LeaderboardPostgresRepository;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jdbi.v3.core.Jdbi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Log4j2
public class LeaderboardService {
    @Getter(AccessLevel.PROTECTED)
    private final LeaderboardRepository leaderboardRepository;

    private final Striped<Lock> leaderboardLock = Striped.lock(32);
    private final Cache<String, Leaderboard> leaderboardCache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build();

    private final String schema;

    public LeaderboardService(final Jdbi jdbi,
                              final String schema,
                              final GameService gameService,
                              final StatService statService,
                              final BoardService boardService) {
        this.schema = schema;
        this.leaderboardRepository = new LeaderboardPostgresRepository(
                jdbi,
                schema,
                gameService,
                statService,
                boardService
        );
    }

    private String getUniqName(final Game game, final Stat stat, final Board board) {
        return String.format(
                "%s-%s-%s",
                game.getRepositoryId(),
                stat.getRepositoryId(),
                board.getRepositoryId()
        );
    }

    private Lock getLeaderboardLock(final Game game, final Stat stat, final Board board) {
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
        log.debug("[{}] Add leaderboard to cache {}", this.schema, leaderboard);
    }

    public Map<Integer, Leaderboard> getLeaderboards(final Collection<Integer> repositoryIds) {
        return this.leaderboardRepository.getLeaderboards(repositoryIds);
    }

    public List<Leaderboard> getLeaderboards() {
        return this.leaderboardRepository.getLeaderboards();
    }

    public List<Leaderboard> getLeaderboards(final Game game) {
        return this.leaderboardRepository.getLeaderboards(game);
    }

    public List<Leaderboard> getLeaderboards(final Game game, final Stat stat) {
        return this.leaderboardRepository.getLeaderboards(game, stat);
    }

    public List<Leaderboard> getLeaderboards(final Game game, final Board board) {
        return this.leaderboardRepository.getLeaderboards(game, board);
    }

    public List<Leaderboard> getLeaderboards(final Stat stat) {
        return this.leaderboardRepository.getLeaderboards(stat);
    }

    public List<Leaderboard> getLeaderboards(final Stat stat, final Board board) {
        return this.leaderboardRepository.getLeaderboards(stat, board);
    }

    public List<Leaderboard> getLeaderboards(final Board board) {
        return this.leaderboardRepository.getLeaderboards(board);
    }

    public Optional<Leaderboard> getLeaderboard(final int repositoryId) {
        return this.leaderboardRepository.getLeaderboard(repositoryId);
    }

    public Optional<Leaderboard> getLeaderboard(final Game game, final Stat stat, final Board board) {
        final Optional<Leaderboard> leaderboardCached = this.getLeaderboardFromCache(game, stat, board);
        if (leaderboardCached.isPresent()) {
            return leaderboardCached;
        }

        final Optional<Leaderboard> leaderboardOpt = this.leaderboardRepository.getLeaderboard(game, stat, board);
        leaderboardOpt.ifPresent(this::addLeaderboardToCache);

        return leaderboardOpt;
    }

    public Leaderboard getLeaderboardOrCreate(final Game game,
                                              final Stat stat,
                                              final Board board,
                                              final boolean deprecated) {
        final Lock lock = this.getLeaderboardLock(game, stat, board);
        lock.lock();
        try {
            final Optional<Leaderboard> leaderboardOpt = this.getLeaderboard(game, stat, board);
            if (leaderboardOpt.isPresent()) {
                return leaderboardOpt.get();
            }

            final Leaderboard leaderboard = this.leaderboardRepository.createdLeaderboard(game, stat, board, deprecated);
            this.addLeaderboardToCache(leaderboard);
            log.info("[{}] Created new leaderboard {}", this.schema, leaderboard);
            return leaderboard;
        } finally {
            lock.unlock();
        }
    }
}