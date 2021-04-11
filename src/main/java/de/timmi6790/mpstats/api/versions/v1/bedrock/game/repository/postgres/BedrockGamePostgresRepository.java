package de.timmi6790.mpstats.api.versions.v1.bedrock.game.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.bedrock.game.repository.BedrockGameRepository;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.repository.models.GameCategory;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.repository.postgres.mappers.GameCategoryMapper;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.repository.postgres.mappers.GameMapper;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.repository.postgres.reducers.GameReducer;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BedrockGamePostgresRepository implements BedrockGameRepository {
    private static final String GET_GAME_BASE = "SELECT game.\"id\" game_id, game.website_name website_name, game.game_name game_name, game.clean_name clean_name, game.description description, game.wiki_url wiki_url, category.category_name category_name, alias_name.alias_name alias_name " +
            "FROM bedrock.games game " +
            "INNER JOIN bedrock.game_category category ON category.\"id\" = game.category_id " +
            "LEFT JOIN bedrock.game_alias alias_name ON alias_name.game_id = game.\"id\" " +
            "%s;";

    private static final String GET_GAMES = String.format(GET_GAME_BASE, "");
    private static final String GET_GAME = String.format(GET_GAME_BASE, "WHERE LOWER(game.game_name) = LOWER(:gameName)");

    private static final String INSERT_GAME = "INSERT INTO bedrock.games(website_name, game_name, clean_name, category_id) VALUES(:websiteName, :gameName, :cleanName, :categoryId);";
    private static final String REMOVE_GAME = "DELETE FROM bedrock.games WHERE id = :gameId;";

    private static final String GET_CATEGORY_BASE = "SELECT id, category_name FROM bedrock.game_category %s;";
    private static final String GET_CATEGORIES = String.format(GET_CATEGORY_BASE, "");
    private static final String GET_CATEGORY = String.format(GET_CATEGORY_BASE, "WHERE LOWER(category_name) = LOWER(:categoryName)");
    private static final String INSERT_CATEGORY = "INSERT INTO bedrock.game_category(category_name) VALUES(:categoryName) RETURNING id, category_name;";
    private static final String REMOVE_CATEGORY = "DELETE FROM bedrock.game_category WHERE id = :categoryId;";

    private final Jdbi database;

    @Autowired
    public BedrockGamePostgresRepository(final Jdbi database) {
        this.database = database;

        database.registerRowMapper(new GameMapper())
                .registerRowMapper(new GameCategoryMapper());
    }

    @Override
    public List<Game> getGames() {
        return this.database.withHandle(handle ->
                handle.createQuery(GET_GAMES)
                        .reduceRows(new GameReducer())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<Game> getGame(final String gameName) {
        return this.database.withHandle(handle ->
                handle.createQuery(GET_GAME)
                        .bind("gameName", gameName)
                        .reduceRows(new GameReducer())
                        .findFirst()
        );
    }

    @Override
    public Game createGame(final String websiteName,
                           final String gameName,
                           final String cleanName,
                           final int categoryId) {
        this.database.useHandle(handle ->
                handle.createUpdate(INSERT_GAME)
                        .bind("websiteName", websiteName)
                        .bind("gameName", gameName)
                        .bind("cleanName", cleanName)
                        .bind("categoryId", categoryId)
                        .execute()
        );

        return this.getGame(gameName).orElseThrow(RuntimeException::new);
    }

    @Override
    public void removeGame(final int gameId) {
        this.database.useHandle(handle ->
                handle.createUpdate(REMOVE_GAME)
                        .bind("gameId", gameId)
                        .execute()
        );
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
        return this.database.withHandle(handle ->
                handle.createQuery(GET_CATEGORIES)
                        .mapTo(GameCategory.class)
                        .list()
        );
    }

    @Override
    public Optional<GameCategory> getGameCategory(final String gameCategoryName) {
        return this.database.withHandle(handle ->
                handle.createQuery(GET_CATEGORY)
                        .bind("categoryName", gameCategoryName)
                        .mapTo(GameCategory.class)
                        .findFirst()
        );
    }

    @Override
    public GameCategory createGameCategory(final String gameCategoryName) {
        return this.database.withHandle(handler ->
                handler.createQuery(INSERT_CATEGORY)
                        .bind("categoryName", gameCategoryName)
                        .mapTo(GameCategory.class)
                        .first()
        );
    }

    @Override
    public void removeGameCategory(final int categoryId) {
        this.database.useHandle(handle ->
                handle.createUpdate(REMOVE_CATEGORY)
                        .bind("categoryId", categoryId)
                        .execute()
        );
    }

    @Override
    public void addGameAlias(final int gameId, final String aliasName) {

    }

    @Override
    public void removeGameAlias(final int gameId, final String aliasName) {

    }
}
