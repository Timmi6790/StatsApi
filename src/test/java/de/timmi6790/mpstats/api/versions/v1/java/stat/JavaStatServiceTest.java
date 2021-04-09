package de.timmi6790.mpstats.api.versions.v1.java.stat;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.JavaStatRepository;
import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.models.Stat;
import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.postgres.JavaStatPostgresRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class JavaStatServiceTest {
    private static JavaStatRepository javaStatRepository;
    private static JavaStatService javaStatService;

    private static final AtomicInteger STAT_ID = new AtomicInteger(0);

    @BeforeAll
    static void setUp() {
        javaStatRepository = new JavaStatPostgresRepository(AbstractIntegrationTest.jdbi());
        javaStatService = new JavaStatService(javaStatRepository);
    }

    private String generateStatName() {
        return "STAT" + STAT_ID.incrementAndGet();
    }


    private Stat generateStat(final String boardName) {
        final String websiteName = this.generateStatName();
        final String cleanName = this.generateStatName();

        return javaStatService.getOrCreateStat(websiteName, boardName, cleanName, true);
    }

    @Test
    void hasStat() {
        final String boardName = this.generateStatName();

        final boolean boardNotFound = javaStatService.hasStat(boardName);
        assertThat(boardNotFound).isFalse();

        this.generateStat(boardName);

        final boolean boardFound = javaStatService.hasStat(boardName);
        assertThat(boardFound).isTrue();
    }

    @Test
    void hasStat_case_insensitive() {
        final String boardName = this.generateStatName();
        this.generateStat(boardName);

        final boolean boardFoundLower = javaStatService.hasStat(boardName.toLowerCase());
        assertThat(boardFoundLower).isTrue();

        final boolean boardFoundUpper = javaStatService.hasStat(boardName.toUpperCase());
        assertThat(boardFoundUpper).isTrue();
    }

    @Test
    void getStats() {
        final Stat board1 = this.generateStat(this.generateStatName());
        final Stat board2 = this.generateStat(this.generateStatName());

        final List<Stat> board = javaStatService.getStats();
        assertThat(board).containsAll(Arrays.asList(board1, board2));
    }

    @Test
    void getStat_case_insensitive() {
        final String boardName = this.generateStatName();
        this.generateStat(boardName);

        final Optional<Stat> boardFoundLower = javaStatService.getStat(boardName.toLowerCase());
        assertThat(boardFoundLower).isPresent();

        final Optional<Stat> boardFoundUpper = javaStatService.getStat(boardName.toUpperCase());
        assertThat(boardFoundUpper).isPresent();

        assertThat(boardFoundLower).contains(boardFoundUpper.get());
    }

    @Test
    void createStat() {
        final String websiteName = this.generateStatName();
        final String cleanName = this.generateStatName();
        final String boardName = this.generateStatName();
        final boolean isAchievement = true;

        final Optional<Stat> boardNotFound = javaStatService.getStat(boardName);
        assertThat(boardNotFound).isNotPresent();

        final Stat createdStat = javaStatService.getOrCreateStat(websiteName, boardName, cleanName, isAchievement);
        assertThat(createdStat.getWebsiteName()).isEqualTo(websiteName);
        assertThat(createdStat.getCleanName()).isEqualTo(cleanName);
        assertThat(createdStat.getStatName()).isEqualTo(boardName);
        assertThat(createdStat.isAchievement()).isEqualTo(isAchievement);

        final Optional<Stat> boardFound = javaStatService.getStat(boardName);
        assertThat(boardFound).isPresent();
        assertThat(boardFound).contains(createdStat);

        final Optional<Stat> boardCacheFound = javaStatRepository.getStat(boardName);
        assertThat(boardCacheFound).isPresent();
        assertThat(boardCacheFound).contains(createdStat);
    }

    @Test
    void createStat_duplicate() {
        final String websiteName = this.generateStatName();
        final String cleanName = this.generateStatName();
        final String boardName = this.generateStatName();
        final boolean isAchievement = false;

        final Stat board1 = javaStatService.getOrCreateStat(websiteName, boardName, cleanName, isAchievement);
        final Stat board2 = javaStatService.getOrCreateStat(websiteName, boardName, cleanName, isAchievement);

        assertThat(board1).isEqualTo(board2);
    }

    @Test
    void deleteStat() {
        final String boardName = this.generateStatName();
        this.generateStat(boardName);

        javaStatService.deleteStat(boardName);

        final boolean notFound = javaStatService.hasStat(boardName);
        assertThat(notFound).isFalse();

        final Optional<Stat> boardNotFound = javaStatService.getStat(boardName);
        assertThat(boardNotFound).isNotPresent();
    }

    @Test
    void deleteStat_case_insensitive() {
        final String boardName = this.generateStatName();
        this.generateStat(boardName);

        javaStatService.deleteStat(boardName.toLowerCase());

        final boolean notFound = javaStatService.hasStat(boardName);
        assertThat(notFound).isFalse();

        final Optional<Stat> boardNotFound = javaStatService.getStat(boardName);
        assertThat(boardNotFound).isNotPresent();
    }

    @Test
    void innit_with_existing_boards() {
        final String boardName = this.generateStatName();
        final Stat board = this.generateStat(boardName);

        final JavaStatService newJavaStatService = new JavaStatService(javaStatRepository);

        final boolean foundStat = newJavaStatService.hasStat(boardName);
        assertThat(foundStat).isTrue();

        final Optional<Stat> boardFound = newJavaStatService.getStat(boardName);
        assertThat(boardFound).isPresent();
        assertThat(boardFound).contains(board);
    }
}