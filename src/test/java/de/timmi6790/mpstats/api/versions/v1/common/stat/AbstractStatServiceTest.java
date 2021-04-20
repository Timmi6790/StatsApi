package de.timmi6790.mpstats.api.versions.v1.common.stat;

import de.timmi6790.mpstats.api.utilities.StatUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.StatRepository;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static de.timmi6790.mpstats.api.utilities.StatUtilities.generateStatName;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractStatServiceTest {
    private final Supplier<StatService> statServiceSupplier;
    private final StatService statService;
    private final StatRepository statRepository;

    public AbstractStatServiceTest(final Supplier<StatService> statServiceSupplier) {
        this.statServiceSupplier = statServiceSupplier;
        this.statService = statServiceSupplier.get();
        this.statRepository = this.statService.getJavaStatRepository();
    }

    protected Stat generateStat(final String boardName) {
        return StatUtilities.generateStat(statService, boardName);
    }
    
    @Test
    void hasStat() {
        final String boardName = generateStatName();

        final boolean boardNotFound = this.statService.hasStat(boardName);
        assertThat(boardNotFound).isFalse();

        this.generateStat(boardName);

        final boolean boardFound = this.statService.hasStat(boardName);
        assertThat(boardFound).isTrue();
    }

    @Test
    void hasStat_case_insensitive() {
        final String boardName = generateStatName();
        this.generateStat(boardName);

        final boolean boardFoundLower = this.statService.hasStat(boardName.toLowerCase());
        assertThat(boardFoundLower).isTrue();

        final boolean boardFoundUpper = this.statService.hasStat(boardName.toUpperCase());
        assertThat(boardFoundUpper).isTrue();
    }

    @Test
    void getStats() {
        final Stat board1 = this.generateStat(generateStatName());
        final Stat board2 = this.generateStat(generateStatName());

        final List<Stat> board = this.statService.getStats();
        assertThat(board).containsAll(Arrays.asList(board1, board2));
    }

    @Test
    void getStat_case_insensitive() {
        final String boardName = generateStatName();
        this.generateStat(boardName);

        final Optional<Stat> boardFoundLower = this.statService.getStat(boardName.toLowerCase());
        assertThat(boardFoundLower).isPresent();

        final Optional<Stat> boardFoundUpper = this.statService.getStat(boardName.toUpperCase());
        assertThat(boardFoundUpper).isPresent();

        assertThat(boardFoundLower).contains(boardFoundUpper.get());
    }

    @Test
    void createStat() {
        final String websiteName = generateStatName();
        final String cleanName = generateStatName();
        final String boardName = generateStatName();
        final boolean isAchievement = true;

        final Optional<Stat> boardNotFound = this.statService.getStat(boardName);
        assertThat(boardNotFound).isNotPresent();

        final Stat createdStat = this.statService.getStatOrCreate(websiteName, boardName, cleanName, isAchievement);
        assertThat(createdStat.getWebsiteName()).isEqualTo(websiteName);
        assertThat(createdStat.getCleanName()).isEqualTo(cleanName);
        assertThat(createdStat.getStatName()).isEqualTo(boardName);
        assertThat(createdStat.isAchievement()).isEqualTo(isAchievement);

        final Optional<Stat> boardFound = this.statService.getStat(boardName);
        assertThat(boardFound)
                .isPresent()
                .contains(createdStat);

        final Optional<Stat> boardCacheFound = this.statRepository.getStat(boardName);
        assertThat(boardCacheFound)
                .isPresent()
                .contains(createdStat);
    }

    @Test
    void createStat_duplicate() {
        final String websiteName = generateStatName();
        final String cleanName = generateStatName();
        final String boardName = generateStatName();
        final boolean isAchievement = false;

        final Stat board1 = this.statService.getStatOrCreate(websiteName, boardName, cleanName, isAchievement);
        final Stat board2 = this.statService.getStatOrCreate(websiteName, boardName, cleanName, isAchievement);

        assertThat(board1).isEqualTo(board2);
    }

    @Test
    void deleteStat() {
        final String boardName = generateStatName();
        this.generateStat(boardName);

        this.statService.deleteStat(boardName);

        final boolean notFound = this.statService.hasStat(boardName);
        assertThat(notFound).isFalse();

        final Optional<Stat> boardNotFound = this.statService.getStat(boardName);
        assertThat(boardNotFound).isNotPresent();
    }

    @Test
    void deleteStat_case_insensitive() {
        final String boardName = generateStatName();
        this.generateStat(boardName);

        this.statService.deleteStat(boardName.toLowerCase());

        final boolean notFound = this.statService.hasStat(boardName);
        assertThat(notFound).isFalse();

        final Optional<Stat> boardNotFound = this.statService.getStat(boardName);
        assertThat(boardNotFound).isNotPresent();
    }

    @Test
    void innit_with_existing_boards() {
        final String boardName = generateStatName();
        final Stat board = this.generateStat(boardName);

        final StatService newStatService = this.statServiceSupplier.get();

        final boolean foundStat = newStatService.hasStat(boardName);
        assertThat(foundStat).isTrue();

        final Optional<Stat> boardFound = newStatService.getStat(boardName);
        assertThat(boardFound)
                .isPresent()
                .contains(board);
    }
}