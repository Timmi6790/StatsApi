package de.timmi6790.mpstats.api.versions.v1.common.game;

import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.GameCategory;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

// TODO: Fix JsonIgnore
public abstract class GameController {
    @Getter(value = AccessLevel.PROTECTED)
    private final GameService gameService;

    protected GameController(final GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<Game> getGames() {
        return this.gameService.getGames();
    }

    @GetMapping(value = "/{gameName}")
    public Optional<Game> getGame(@PathVariable final String gameName) {
        return this.gameService.getGame(gameName);
    }

    @PostMapping(value = "/{gameName}")
    public Game createGame(@PathVariable final String gameName,
                           @RequestParam final String websiteName,
                           @RequestParam final String cleanName,
                           @RequestParam final String categoryName) {
        // TODO: Add spring security
        return this.gameService.getOrCreateGame(websiteName, gameName, cleanName, categoryName);
    }

    @GetMapping(value = "/category")
    public List<GameCategory> getCategories() {
        return this.gameService.getCategories();
    }

    @GetMapping(value = "/category/{categoryName}")
    public Optional<GameCategory> getCategory(@PathVariable final String categoryName) {
        return this.gameService.getCategory(categoryName);
    }
}

