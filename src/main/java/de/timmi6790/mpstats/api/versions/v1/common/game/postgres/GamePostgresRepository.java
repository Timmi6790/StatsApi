package de.timmi6790.mpstats.api.versions.v1.common.game.postgres;

import de.timmi6790.mpstats.api.versions.v1.common.game.GameRepository;
import de.timmi6790.mpstats.api.versions.v1.common.game.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.game.models.GameCategory;
import de.timmi6790.mpstats.api.versions.v1.common.game.postgres.mappers.GameCategoryMapper;
import de.timmi6790.mpstats.api.versions.v1.common.game.postgres.mappers.GameMapper;
import de.timmi6790.mpstats.api.versions.v1.common.game.postgres.reducers.GameReducer;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.PostgresRepository;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GamePostgresRepository extends PostgresRepository implements GameRepository {
    private final String getGames;
    private final String getGame;

    private final String insertGame;
    private final String removeGame;

    private final String getCategories;
    private final String getCategory;
    private final String insertCategory;
    private final String removeCategory;

    public GamePostgresRepository(final Jdbi database, final String schema) {
        super(database, schema);

        this.getDatabase()
                .registerRowMapper(new GameMapper())
                .registerRowMapper(new GameCategoryMapper());

        // Create queries
        this.getGames = this.formatQuery(QueryTemplates.GET_GAMES);
        this.getGame = this.formatQuery(QueryTemplates.GET_GAME);

        this.insertGame = this.formatQuery(QueryTemplates.INSERT_GAME);
        this.removeGame = this.formatQuery(QueryTemplates.REMOVE_GAME);

        this.getCategories = this.formatQuery(QueryTemplates.GET_CATEGORIES);
        this.getCategory = this.formatQuery(QueryTemplates.GET_CATEGORY);
        this.insertCategory = this.formatQuery(QueryTemplates.INSERT_CATEGORY);
        this.removeCategory = this.formatQuery(QueryTemplates.REMOVE_CATEGORY);
    }


    @Override
    public List<Game> getGames() {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getGames)
                        .reduceRows(new GameReducer())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<Game> getGame(final String gameName) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getGame)
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
        this.getDatabase().useHandle(handle ->
                handle.createUpdate(this.insertGame)
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
        this.getDatabase().useHandle(handle ->
                handle.createUpdate(this.removeGame)
                        .bind("gameId", gameId)
                        .execute()
        );
    }

    @Override
    public void setGameName(final int gameId, final String newGameName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGameWebsiteName(final int gameId, final String newWebsiteName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGameDescription(final int gameId, final String description) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGameWikiUrl(final int gameId, final String wikiUrl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGameCategory(final int gameId, final int categoryId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<GameCategory> geGameCategories() {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getCategories)
                        .mapTo(GameCategory.class)
                        .list()
        );
    }

    @Override
    public Optional<GameCategory> getGameCategory(final String gameCategoryName) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getCategory)
                        .bind("categoryName", gameCategoryName)
                        .mapTo(GameCategory.class)
                        .findFirst()
        );
    }

    @Override
    public GameCategory createGameCategory(final String gameCategoryName) {
        return this.getDatabase().withHandle(handler ->
                handler.createQuery(this.insertCategory)
                        .bind("categoryName", gameCategoryName)
                        .mapTo(GameCategory.class)
                        .first()
        );
    }

    @Override
    public void removeGameCategory(final int categoryId) {
        this.getDatabase().useHandle(handle ->
                handle.createUpdate(this.removeCategory)
                        .bind("categoryId", categoryId)
                        .execute()
        );
    }

    @Override
    public void addGameAlias(final int gameId, final String aliasName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeGameAlias(final int gameId, final String aliasName) {
        throw new UnsupportedOperationException();
    }

    private static class QueryTemplates {
        private static final String GET_GAME_BASE = "SELECT game.\"id\" game_id, game.website_name website_name, game.game_name game_name, game.clean_name clean_name, game.description description, game.wiki_url wiki_url, category.category_name category_name, alias_name.alias_name alias_name " +
                "FROM $schema$.games game " +
                "INNER JOIN $schema$.game_category category ON category.\"id\" = game.category_id " +
                "LEFT JOIN $schema$.game_alias alias_name ON alias_name.game_id = game.\"id\" " +
                "%s;";

        private static final String GET_GAMES = String.format(GET_GAME_BASE, "");
        private static final String GET_GAME = String.format(GET_GAME_BASE, "WHERE LOWER(game.game_name) = LOWER(:gameName)");

        private static final String INSERT_GAME = "INSERT INTO $schema$.games(website_name, game_name, clean_name, category_id) VALUES(:websiteName, :gameName, :cleanName, :categoryId);";
        private static final String REMOVE_GAME = "DELETE FROM $schema$.games WHERE id = :gameId;";

        private static final String GET_CATEGORY_BASE = "SELECT id, category_name FROM $schema$.game_category %s;";
        private static final String GET_CATEGORIES = String.format(GET_CATEGORY_BASE, "");
        private static final String GET_CATEGORY = String.format(GET_CATEGORY_BASE, "WHERE LOWER(category_name) = LOWER(:categoryName)");
        private static final String INSERT_CATEGORY = "INSERT INTO $schema$.game_category(category_name) VALUES(:categoryName) RETURNING id, category_name;";
        private static final String REMOVE_CATEGORY = "DELETE FROM $schema$.game_category WHERE id = :categoryId;";
    }
}
