package de.timmi6790.mpstats.api.versions.v1.common.utilities;

import de.timmi6790.mpstats.api.utilities.SimilarityUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.exceptions.InvalidBoardNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.exceptions.InvalidGameNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.group.GroupService;
import de.timmi6790.mpstats.api.versions.v1.common.group.exceptions.InvalidGroupNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.group.repository.models.Group;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.exceptions.InvalidLeaderboardCombinationRestException;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.exceptions.InvalidPlayerNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.exceptions.InvalidStatNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@UtilityClass
public class RestUtilities {
    private List<Leaderboard> getSimilarLeaderboards(final LeaderboardService leaderboardService,
                                                     final Game game,
                                                     final Stat stat,
                                                     final Board board) {
        // First try to find leaderboards with a valid game and stat combination
        final List<Leaderboard> gameStatLeaderboards = leaderboardService.getLeaderboards(game, stat);
        if (!gameStatLeaderboards.isEmpty()) {
            return getSimilarValues(
                    board.getBoardName(),
                    gameStatLeaderboards,
                    lb -> lb.getBoard().getBoardName()
            );
        }

        // Second try to find leaderboard with a valid game and board combination
        final List<Leaderboard> gameBoardLeaderboards = leaderboardService.getLeaderboards(game, board);
        if (!gameBoardLeaderboards.isEmpty()) {
            return getSimilarValues(
                    stat.getStatName(),
                    gameBoardLeaderboards,
                    lb -> lb.getStat().getStatName()
            );
        }

        // If everything else fails return leaderboards based on the stat name
        // TODO: This should take into account both the stat and board name
        return getSimilarValues(
                stat.getStatName(),
                leaderboardService.getLeaderboards(game),
                lb -> lb.getStat().getStatName()
        );
    }

    public <T> List<T> getSimilarValues(final String input,
                                        final Collection<T> values,
                                        final Function<T, String> valueToStringFunction) {
        return SimilarityUtilities.getSimilarityList(
                input,
                values,
                valueToStringFunction,
                8,
                5
        );
    }

    public Game getGameOrThrow(final GameService gameService, final String gameName) throws InvalidGameNameRestException {
        return gameService.getGame(gameName).orElseThrow(() ->
                new InvalidGameNameRestException(getSimilarValues(gameName, gameService.getGames(), Game::getGameName))
        );
    }

    public Board getBoardOrThrow(final BoardService boardService, final String boardName) throws InvalidBoardNameRestException {
        return boardService.getBoard(boardName).orElseThrow(() ->
                new InvalidBoardNameRestException(getSimilarValues(boardName, boardService.getBoards(), Board::getBoardName))
        );
    }

    public Stat getStatOrThrow(final StatService statService, final String statName) throws InvalidStatNameRestException {
        return statService.getStat(statName).orElseThrow(() ->
                new InvalidStatNameRestException(getSimilarValues(statName, statService.getStats(), Stat::getStatName))
        );
    }

    public Leaderboard getLeaderboardOrThrow(final GameService gameService,
                                             final String gameName,
                                             final StatService statService,
                                             final String statName,
                                             final BoardService boardService,
                                             final String boardName,
                                             final LeaderboardService leaderboardService) throws InvalidGameNameRestException, InvalidStatNameRestException, InvalidBoardNameRestException, InvalidLeaderboardCombinationRestException {
        final Game game = RestUtilities.getGameOrThrow(gameService, gameName);
        final Stat stat = RestUtilities.getStatOrThrow(statService, statName);
        final Board board = RestUtilities.getBoardOrThrow(boardService, boardName);

        return getLeaderboardOrThrow(leaderboardService, game, stat, board);
    }

    public Leaderboard getLeaderboardOrThrow(final LeaderboardService leaderboardService,
                                             final Game game,
                                             final Stat stat,
                                             final Board board) throws InvalidLeaderboardCombinationRestException {
        // Add suggestions
        return leaderboardService.getLeaderboard(game, stat, board).orElseThrow(() ->
                new InvalidLeaderboardCombinationRestException(getSimilarLeaderboards(leaderboardService, game, stat, board))
        );
    }

    public Group getGroupOrThrow(final GroupService groupService, final String groupName) throws InvalidGroupNameRestException {
        return groupService.getGroup(groupName).orElseThrow(() ->
                new InvalidGroupNameRestException(getSimilarValues(groupName, groupService.getGroups(), Group::getGroupName))
        );
    }

    public void verifyPlayerName(final PlayerService<?> playerService, final String playerName) throws InvalidPlayerNameRestException {
        if (!playerService.isValidPlayerName(playerName)) {
            throw new InvalidPlayerNameRestException();
        }
    }
}
