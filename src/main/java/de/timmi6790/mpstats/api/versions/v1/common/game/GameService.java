package de.timmi6790.mpstats.api.versions.v1.common.game;

import com.google.common.util.concurrent.Striped;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.GameRepository;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.GameCategory;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.postgres.GamePostgresRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jdbi.v3.core.Jdbi;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

@Log4j2
// TODO: Add tests
public class GameService {
    @Getter(AccessLevel.PROTECTED)
    private final GameRepository gameRepository;

    private final Striped<Lock> categoryLock = Striped.lock(16);
    private final Map<String, GameCategory> categories;

    private final Striped<Lock> gameLock = Striped.lock(32);
    private final Map<String, Game> games;
    private final Map<String, String> gameAliasNames;

    private final String schema;

    public GameService(final Jdbi jdbi, final String schema) {
        this.schema = schema;
        this.gameRepository = new GamePostgresRepository(jdbi, schema);

        // Load existing categories from repository
        log.info("[{}] Load game categories from repository", schema);
        final List<GameCategory> existingCategories = this.gameRepository.geGameCategories();
        this.categories = new LinkedCaseInsensitiveMap<>(existingCategories.size());
        for (final GameCategory category : existingCategories) {
            this.categories.put(category.getCategoryName(), category);
        }
        log.info("[{}] Loaded {} game categories from repository", schema, this.categories.size());

        // Load existing games from repository
        log.info("[{}] Load games from repository", schema);
        final List<Game> existingGames = this.gameRepository.getGames();
        this.games = new LinkedCaseInsensitiveMap<>(existingGames.size());
        this.gameAliasNames = new LinkedCaseInsensitiveMap<>();
        for (final Game game : existingGames) {
            this.games.put(game.getGameName(), game);

            for (final String aliasName : game.getAliasNames()) {
                this.gameAliasNames.put(aliasName, game.getGameName());
            }
        }
        log.info("[{}] Loaded {} games from repository", schema, this.games.size());
        log.info("[{}] Loaded {} game alias names from repository", schema, this.gameAliasNames.size());
    }

    private Lock getGameLock(final String gameName) {
        return this.gameLock.get(gameName.toLowerCase());
    }

    private Lock getCategoryLock(final String categoryName) {
        return this.categoryLock.get(categoryName.toLowerCase());
    }

    private String getGameName(final String gameName) {
        return this.gameAliasNames.getOrDefault(gameName, gameName);
    }

    public boolean hasCategory(final String categoryName) {
        return this.categories.containsKey(categoryName);
    }

    public List<GameCategory> getCategories() {
        return new ArrayList<>(this.categories.values());
    }

    public Optional<GameCategory> getCategory(final String categoryName) {
        return Optional.ofNullable(this.categories.get(categoryName));
    }

    public GameCategory getCategoryOrCreate(final String categoryName) {
        final Lock lock = this.getCategoryLock(categoryName);
        lock.lock();
        try {
            if (this.hasCategory(categoryName)) {
                return this.getCategory(categoryName).orElseThrow(RuntimeException::new);
            }

            final GameCategory category = this.gameRepository.createGameCategory(categoryName);
            this.categories.put(category.getCategoryName(), category);
            log.info("[{}] Created new game category {}", this.schema, category);
            return category;
        } finally {
            lock.unlock();
        }
    }

    public boolean hasGame(final String gameName) {
        return this.games.containsKey(this.getGameName(gameName));
    }

    public List<Game> getGames() {
        return new ArrayList<>(this.games.values());
    }

    public Optional<Game> getGame(final int repositoryId) {
        // We only have ~100 games
        for (final Game game : this.getGames()) {
            if (game.getRepositoryId() == repositoryId) {
                return Optional.of(game);
            }
        }
        return Optional.empty();
    }

    public Optional<Game> getGame(final String gameName) {
        return Optional.ofNullable(this.games.get(this.getGameName(gameName)));
    }

    public Game getOrCreateGame(final String websiteName,
                                final String gameName,
                                final String cleanName,
                                final String categoryName) {
        final Lock lock = this.getGameLock(gameName);
        lock.lock();
        try {
            if (this.hasGame(gameName)) {
                return this.getGame(gameName).orElseThrow(RuntimeException::new);
            }

            final GameCategory gameCategory = this.getCategoryOrCreate(categoryName);
            final Game game = this.gameRepository.createGame(websiteName, gameName, cleanName, gameCategory.getRepositoryId());
            this.games.put(game.getGameName(), game);
            log.info("[{}] Created new game {}", this.schema, game);
            return game;
        } finally {
            lock.unlock();
        }
    }

    public void deleteGame(String gameName) {
        gameName = this.getGameName(gameName);
        final Lock lock = this.getGameLock(gameName);
        lock.lock();
        try {
            final Game game = this.games.remove(gameName);
            if (game != null) {
                this.gameRepository.removeGame(game.getRepositoryId());
                log.info("[{}] Removed game {}", this.schema, game);
            }
        } finally {
            lock.unlock();
        }
    }
}
