package de.timmi6790.mpstats.api.versions.v1.java.game;

import de.timmi6790.mpstats.api.versions.v1.java.game.repository.JavaGameRepository;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.models.GameCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JavaGameService {
    private final JavaGameRepository javaGameRepository;

    private final Set<String> gameNames = Collections.synchronizedSet(new TreeSet<>(String.CASE_INSENSITIVE_ORDER));
    private final Set<String> categoryNames = Collections.synchronizedSet(new TreeSet<>(String.CASE_INSENSITIVE_ORDER));

    @Autowired
    public JavaGameService(final JavaGameRepository javaGameRepository) {
        this.javaGameRepository = javaGameRepository;

        for (final GameCategory category : this.getCategories()) {
            this.categoryNames.add(category.getRepositoryName());
        }

        for (final Game game : this.getGames()) {
            this.gameNames.add(game.getGameName());
        }
    }

    public boolean hasCategory(final String categoryName) {
        return this.categoryNames.contains(categoryName);
    }

    public List<GameCategory> getCategories() {
        return this.javaGameRepository.geGameCategories();
    }

    public Optional<GameCategory> getCategory(final String categoryName) {
        return this.javaGameRepository.getGameCategory(categoryName);
    }

    public GameCategory getOrCreateCategory(final String categoryName) {
        if (this.hasCategory(categoryName)) {
            return this.getCategory(categoryName).orElseThrow(RuntimeException::new);
        }

        final GameCategory category = this.javaGameRepository.createGameCategory(categoryName);
        this.categoryNames.add(categoryName);
        return category;
    }

    public boolean hasGame(final String gameName) {
        return this.gameNames.contains(gameName);
    }

    public List<Game> getGames() {
        return this.javaGameRepository.getGames();
    }

    public Optional<Game> getGame(final String gameName) {
        if (this.hasGame(gameName)) {
            return this.javaGameRepository.getGame(gameName);
        }
        return Optional.empty();
    }

    public Game getOrCreateGame(final String websiteName,
                                final String gameName,
                                final String cleanName,
                                final String categoryName) {
        if (this.hasGame(gameName)) {
            return this.getGame(gameName).orElseThrow(RuntimeException::new);
        }

        final GameCategory gameCategory = this.getOrCreateCategory(categoryName);
        this.gameNames.add(gameName);
        return this.javaGameRepository.createGame(websiteName, gameName, cleanName, gameCategory.getRepositoryId());
    }

    public void deleteGame(final String gameName) {
        final Optional<Game> gameOpt = this.getGame(gameName);
        if (gameOpt.isPresent()) {
            this.gameNames.remove(gameName);
            this.javaGameRepository.removeGame(gameOpt.get().getRepositoryId());
        }
    }
}
