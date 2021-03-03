package de.timmi6790.mineplex_stats_api.versions.v1.java.player.repository.mysql;

import de.timmi6790.mineplex_stats_api.versions.v1.java.groups.repository.mysql.mappers.GroupsMapper;
import de.timmi6790.mineplex_stats_api.versions.v1.java.player.repository.JavaPlayerRepository;
import de.timmi6790.mineplex_stats_api.versions.v1.java.player.repository.models.PlayerStatsDatabaseModel;
import lombok.NonNull;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class JavaPlayerMysqlRepository implements JavaPlayerRepository {
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

    private final Jdbi database;

    @Autowired
    public JavaPlayerMysqlRepository(final Jdbi jdbi) {
        this.database = jdbi;

        this.database.registerRowMapper(new GroupsMapper());
    }

    @Override
    public Optional<PlayerStatsDatabaseModel> getStats(@NonNull final String playerName,
                                                       @NonNull final UUID playerUUID,
                                                       @NonNull final String game,
                                                       @NonNull final String board,
                                                       @NonNull final LocalDateTime time,
                                                       final boolean filter) {
        return this.database.withHandle(handle -> {
            handle.createUpdate(SET_PLAYER_ID_NAME)
                    .bind("playerName", playerName)
                    .execute();

            /*
            return handle.createQuery(GET_JAVA_PLAYER_STATS_FILTER)
                    .registerRowMapper(BeanMapper.factory(PlayerStatsDatabaseModel.class, "c"))
                    .registerRowMapper(BeanMapper.factory(PlayerStatsDatabaseModel.StatEntry.class, "p"))
                    .reduceRows(LinkedHashMapRowReducer.<Long, PlayerStatsDatabaseModel> ((map, rowView) -> {
                PlayerStatsDatabaseModel contact = map.orElseGet(() -> rowView.getRow(PlayerStatsDatabaseModel.class));

                if (rowView.getColumn("p_id", Long.class) != null) {
                    contact.a(rowView.getRow(Phone.class));
                }
            }).findFirst();

             */
            return Optional.empty();
        });
    }
}
