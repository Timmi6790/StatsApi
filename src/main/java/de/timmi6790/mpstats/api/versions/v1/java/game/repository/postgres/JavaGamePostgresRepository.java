package de.timmi6790.mpstats.api.versions.v1.java.game.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.java.game.repository.JavaGameRepository;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.models.GameCategory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JavaGamePostgresRepository implements JavaGameRepository {
    @Override
    public List<Game> getGames() {
        return null;
    }

    @Override
    public Optional<Game> getGame(final String gameName) {
        return Optional.empty();
    }

    @Override
    public Game createGame(final String websiteName, final String gameName) {
        return null;
    }

    @Override
    public void removeGame(final int gameId) {

    }

    @Override
    public void setGameName(final int gameId, final String newGameName) {

    }

    @Override
    public void setGameWebsiteName(final int gameId, final String newWebsiteName) {

    }

    @Override
    public void setGameDescription(final int gameId, final String description) {

    }

    @Override
    public void setGameWikiUrl(final int gameId, final String wikiUrl) {

    }

    @Override
    public void setGameCategory(final int gameId, final int categoryId) {

    }

    @Override
    public List<GameCategory> geGameCategories() {
        return null;
    }

    @Override
    public Optional<GameCategory> getGameCategory(final String gameCategoryName) {
        return Optional.empty();
    }

    @Override
    public GameCategory createGameCategory(final String gameCategoryName) {
        return null;
    }

    @Override
    public void removeGameCategory(final int categoryId) {

    }

    @Override
    public void addGameAlias(final int gameId, final String aliasName) {

    }

    @Override
    public void removeGameAlias(final int gameId, final String aliasName) {

    }
}
