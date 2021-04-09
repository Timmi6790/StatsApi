package de.timmi6790.mpstats.api.versions.v1.java.game;

import com.google.common.util.concurrent.Striped;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.JavaGameRepository;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.models.GameCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

@Service
public class JavaGameService {
    private final JavaGameRepository javaGameRepository;

    private final Striped<Lock> categoryLock = Striped.lock(16);
    private final Map<String, GameCategory> categories;

    private final Striped<Lock> gameLock = Striped.lock(32);
    private final Map<String, Game> games;

    @Autowired
    public JavaGameService(final JavaGameRepository javaGameRepository) {
        this.javaGameRepository = javaGameRepository;

        // Load existing categories from repository
        final List<GameCategory> existingCategories = javaGameRepository.geGameCategories();
        this.categories = new LinkedCaseInsensitiveMap<>(existingCategories.size());
        for (final GameCategory category : existingCategories) {
            this.categories.put(category.getCategoryName(), category);
        }

        // Load existing games from repository
        final List<Game> existingGames = javaGameRepository.getGames();
        this.games = new LinkedCaseInsensitiveMap<>(existingGames.size());
        for (final Game game : existingGames) {
            this.games.put(game.getGameName(), game);
        }
    }

    private Lock getGameLock(final String gameName) {
        return this.gameLock.get(gameName.toLowerCase());
    }

    private Lock getCategoryLock(final String categoryName) {
        return this.categoryLock.get(categoryName.toLowerCase());
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

    public GameCategory getOrCreateCategory(final String categoryName) {
        final Lock lock = this.getCategoryLock(categoryName);
        lock.lock();
        try {
            if (this.hasCategory(categoryName)) {
                return this.getCategory(categoryName).orElseThrow(RuntimeException::new);
            }

            final GameCategory category = this.javaGameRepository.createGameCategory(categoryName);
            this.categories.put(category.getCategoryName(), category);
            return category;
        } finally {
            lock.unlock();
        }
    }

    public boolean hasGame(final String gameName) {
        return this.games.containsKey(gameName);
    }

    public List<Game> getGames() {
        return new ArrayList<>(this.games.values());
    }

    public Optional<Game> getGame(final String gameName) {
        return Optional.ofNullable(this.games.get(gameName));
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

            final GameCategory gameCategory = this.getOrCreateCategory(categoryName);
            final Game game = this.javaGameRepository.createGame(websiteName, gameName, cleanName, gameCategory.getRepositoryId());
            this.games.put(game.getGameName(), game);
            return game;
        } finally {
            lock.unlock();
        }
    }

    public void deleteGame(final String gameName) {
        final Lock lock = this.getGameLock(gameName);
        lock.lock();
        try {
            final Game game = this.games.remove(gameName);
            if (game != null) {
                this.javaGameRepository.removeGame(game.getRepositoryId());
            }
        } finally {
            lock.unlock();
        }
    }
}
