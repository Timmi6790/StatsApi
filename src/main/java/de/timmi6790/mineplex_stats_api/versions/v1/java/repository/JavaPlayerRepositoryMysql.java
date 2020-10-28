package de.timmi6790.mineplex_stats_api.versions.v1.java.repository;

import de.timmi6790.mineplex_stats_api.versions.v1.java.repository.database_mapper.GroupsDatabaseMapper;
import de.timmi6790.mineplex_stats_api.versions.v1.java.repository.models.GroupsModel;
import de.timmi6790.mineplex_stats_api.versions.v1.java.repository.models.PlayerStatsModule;
import lombok.NonNull;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JavaPlayerRepositoryMysql implements JavaPlayerRepository {
    private static final String SET_PLAYER_ID_NAME = "SET @playerId = (SELECT id FROM java_player WHERE player_name = :playerName LIMIT 1);";
    private static final String SET_PLAYER_ID_UUID = "SET @playerId = (SELECT id FROM java_player WHERE uuid = :uuid LIMIT 1);";

    private static final String GET_JAVA_PLAYER_STATS = "SELECT player.uuid uuid, " +
            "player.player_name player, " +
            "game.game_name game, " +
            "jboard.board_name board, " +
            "stat.stat_name, " +
            "(SELECT get_java_player_position_filter(save.leaderboard_save_id, @playerId)) position, " +
            "save.score, " +
            "UNIX_TIMESTAMP(datetime) unixtime " +
            " " +
            "FROM java_leaderboard board   " +
            "INNER JOIN java_leaderboard_save_id saveId ON saveId.id = (SELECT id FROM java_leaderboard_save_id " +
            "WHERE leaderboard_id = board.id ORDER BY ABS(TIMESTAMPDIFF(SECOND, java_leaderboard_save_id.datetime, '0')) LIMIT 1) " +
            "INNER JOIN java_leaderboard_save_new save ON save.leaderboard_save_id = saveId.id   " +
            "INNER JOIN java_player player ON player.id = save.player_id   " +
            "INNER JOIN java_stat stat ON stat.id = board.stat_id   " +
            "INNER JOIN java_game game ON game.id = board.game_id   " +
            "INNER JOIN java_board jboard ON jboard.id = board.board_id   " +
            "LEFT JOIN java_filter filter ON filter.player_id = player.id AND filter.leaderboard_id = board.id " +
            "WHERE player.id = @playerId " +
            "AND game.game_name = 'Global' " +
            "AND jboard.board_name = 'All' ";

    private static final String GET_JAVA_PLAYER_STATS_FILTER = GET_JAVA_PLAYER_STATS + "AND filter.id IS NULL;";

    private static final String GET_GROUPS = "SELECT jgroup.group_name groupName, jgroup.description, GROUP_CONCAT(DISTINCT groupAlias.alias_name) aliasNames, GROUP_CONCAT(DISTINCT game.game_name) gameNames\n" +
            "FROM java_group jgroup\n" +
            "LEFT JOIN java_group_alias groupAlias ON groupAlias.group_id = jgroup.id\n" +
            "INNER JOIN java_group_game groupGame ON groupGame.group_id = jgroup.id\n" +
            "INNER JOIN java_game game ON game.id = groupGame.game_id\n" +
            "GROUP BY jgroup.id;\n";

    private final Jdbi database;

    public JavaPlayerRepositoryMysql(final Jdbi jdbi) {
        this.database = jdbi;

        this.database.registerRowMapper(new GroupsDatabaseMapper());
    }

    @Override
    public Optional<PlayerStatsModule> getPlayerStats(@NonNull final String playerName,
                                                      @NonNull final UUID playerUUID,
                                                      @NonNull final String game,
                                                      @NonNull final String board,
                                                      @NonNull final LocalDateTime time,
                                                      final boolean filter) {

        return Optional.empty();
    }

    @Override
    public List<GroupsModel> getGroups() {
        return this.database.withHandle(handle -> handle.createQuery(GET_GROUPS).mapTo(GroupsModel.class).list());
    }
}
