package de.timmi6790.mpstats.api.versions.v1.common.game;

import de.timmi6790.mpstats.api.versions.v1.common.game.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.game.models.GameCategory;

import java.util.List;
import java.util.Optional;

public interface GameRepository {
    List<Game> getGames();

    Optional<Game> getGame(String gameName);

    Game createGame(String websiteName, String gameName, String cleanName, int categoryId);

    void removeGame(int gameId);

    void setGameName(int gameId, String newGameName);

    void setGameWebsiteName(int gameId, String newWebsiteName);

    void setGameDescription(int gameId, String description);

    void setGameWikiUrl(int gameId, String wikiUrl);

    void setGameCategory(int gameId, int categoryId);

    List<GameCategory> geGameCategories();

    Optional<GameCategory> getGameCategory(String gameCategoryName);

    GameCategory createGameCategory(String gameCategoryName);

    void removeGameCategory(int categoryId);

    void addGameAlias(int gameId, String aliasName);

    void removeGameAlias(int gameId, String aliasName);
}
