package de.timmi6790.mpstats.api.versions.v1.common.game;

import de.timmi6790.mpstats.api.security.annontations.RequireAdminPerms;
import de.timmi6790.mpstats.api.versions.v1.common.game.exceptions.InvalidGameCategoryNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.game.exceptions.InvalidGameNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.GameCategory;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.RestUtilities;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Getter(AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class GameController {
    private final GameService gameService;

    @GetMapping
    @Operation(summary = "Find all available games")
    public List<Game> getGames() {
        return this.gameService.getGames();
    }

    @GetMapping("/{gameName}")
    @Operation(summary = "Find game by name")
    public Game getGame(@PathVariable final String gameName) throws InvalidGameNameRestException {
        return RestUtilities.getGameOrThrow(this.gameService, gameName);
    }

    @PutMapping("/{gameName}")
    @Operation(summary = "Create a new game")
    @RequireAdminPerms
    public Game createGame(@PathVariable final String gameName,
                           @RequestParam final String websiteName,
                           @RequestParam final String cleanName,
                           @RequestParam final String categoryName) {
        return this.gameService.getOrCreateGame(websiteName, gameName, cleanName, categoryName);
    }

    @GetMapping("/category")
    @Operation(summary = "Find all available game categories")
    public List<GameCategory> getCategories() {
        return this.gameService.getCategories();
    }

    @GetMapping("/category/{categoryName}")
    @Operation(summary = "Find game category by name")
    public GameCategory getCategory(@PathVariable final String categoryName) throws InvalidGameCategoryNameRestException {
        return this.gameService.getCategory(categoryName).orElseThrow(() ->
                new InvalidGameCategoryNameRestException(
                        RestUtilities.getSimilarValues(
                                categoryName,
                                this.gameService.getCategories(),
                                GameCategory::getCategoryName
                        )
                )
        );
    }
}

