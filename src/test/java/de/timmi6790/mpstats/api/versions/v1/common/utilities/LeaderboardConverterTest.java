package de.timmi6790.mpstats.api.versions.v1.common.utilities;

import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardPositionEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LeaderboardConverterTest {
    private void verifyPosition(final List<LeaderboardPositionEntry<Player>> convertedEntries,
                                final int index,
                                final int expectedPosition) {
        final LeaderboardPositionEntry<?> entry = convertedEntries.get(index);
        assertThat(entry.getPosition()).isEqualTo(expectedPosition);
    }

    private LeaderboardEntry<Player> generateLeaderboardEntry(final long score) {
        return new LeaderboardEntry<>(
                mock(Player.class),
                score
        );
    }

    private void testConverter(final int startPosition) {
        final List<LeaderboardEntry<Player>> entries = List.of(
                this.generateLeaderboardEntry(1),
                this.generateLeaderboardEntry(1),
                this.generateLeaderboardEntry(1),
                this.generateLeaderboardEntry(2),
                this.generateLeaderboardEntry(3),
                this.generateLeaderboardEntry(4),
                this.generateLeaderboardEntry(5),
                this.generateLeaderboardEntry(5)
        );

        final List<LeaderboardPositionEntry<Player>> convertedEntries;
        // We do this for the coverage
        if (startPosition == 1) {
            convertedEntries = LeaderboardConverter.convertEntries(entries);
        } else {
            convertedEntries = LeaderboardConverter.convertEntries(entries, startPosition);
        }
        assertThat(convertedEntries).hasSize(entries.size());

        // Verify the correct positions
        // index = position
        // 0-2 = 1
        // 3 = 4
        // 4 = 5
        // 5 = 6
        // 6-7 = 7
        for (int index = 0; 3 > index; index++) {
            this.verifyPosition(
                    convertedEntries,
                    index,
                    startPosition
            );
        }

        this.verifyPosition(
                convertedEntries,
                3,
                startPosition + 3
        );

        this.verifyPosition(
                convertedEntries,
                4,
                startPosition + 4
        );

        this.verifyPosition(
                convertedEntries,
                5,
                startPosition + 5
        );

        this.verifyPosition(
                convertedEntries,
                6,
                startPosition + 6
        );
        this.verifyPosition(
                convertedEntries,
                7,
                startPosition + 6
        );
    }

    @Test
    void convertEntries() {
        this.testConverter(1);
    }

    @Test
    void convertEntries_start_count_100() {
        this.testConverter(100);
    }

    @Test
    void convertEntries_start_count_255() {
        this.testConverter(255);
    }
}