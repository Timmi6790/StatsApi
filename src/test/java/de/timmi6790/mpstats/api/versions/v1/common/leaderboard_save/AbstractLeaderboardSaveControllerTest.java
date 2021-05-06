package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save;

import com.google.gson.reflect.TypeToken;
import de.timmi6790.mpstats.api.AbstractRestTest;
import de.timmi6790.mpstats.api.utilities.LeaderboardEntryUtilities;
import de.timmi6790.mpstats.api.utilities.LeaderboardUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractLeaderboardSaveControllerTest<P extends Player> extends AbstractRestTest {
    private final String basePath;

    protected AbstractLeaderboardSaveControllerTest(final String basePath) {
        this.basePath = basePath;
    }

    protected abstract LeaderboardSaveService<P> getSaveService();

    protected abstract LeaderboardService getLeaderboardService();

    protected abstract GameService getGameService();

    protected abstract StatService getStatService();

    protected abstract BoardService getBoardService();

    protected abstract PlayerService<P> getPlayerService();

    protected List<LeaderboardEntry<P>> generateEntries(final int count) {
        return LeaderboardEntryUtilities.generateEntries(this.getPlayerService(), count);
    }

    protected Leaderboard generateLeaderboard() {
        return LeaderboardUtilities.generateLeaderboard(
                this.getLeaderboardService(),
                this.getGameService(),
                this.getStatService(),
                this.getBoardService()
        );
    }

    @Test
    void getLeaderboardSaveTimes() {
        final Leaderboard leaderboard = this.generateLeaderboard();

        final Supplier<MockMvcResponse> responseSupplier = () -> this.getWithNoApiKey()
                .when()
                .get(this.basePath + "/" + leaderboard.getGame().getGameName() + "/" + leaderboard.getStat().getStatName() + "/" + leaderboard.getBoard().getBoardName() + "/saves");

        final List<LocalDateTime> savesNotFound = this.parseResponse(
                responseSupplier.get(),
                new TypeToken<ArrayList<LocalDateTime>>() {
                }
        );
        assertThat(savesNotFound).isEmpty();

        final int addedEntries = 3;
        for (int count = 0; addedEntries > count; count++) {
            this.getSaveService().saveLeaderboardEntries(leaderboard, this.generateEntries(1), LocalDateTime.now());
        }


        final List<LocalDateTime> savesFound = this.parseResponse(
                responseSupplier.get(),
                new TypeToken<ArrayList<LocalDateTime>>() {
                }
        );
        assertThat(savesFound).hasSize(addedEntries);
    }
}