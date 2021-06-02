package de.timmi6790.mpstats.api.versions.v1.common.stat;

import com.google.gson.reflect.TypeToken;
import de.timmi6790.mpstats.api.AbstractRestTest;
import de.timmi6790.mpstats.api.utilities.StatUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static de.timmi6790.mpstats.api.utilities.StatUtilities.generateStatName;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractStatControllerTest<T extends StatService> extends AbstractRestTest {
    protected static final String[] STAT_IGNORED_FIELDS = {"repositoryId", "websiteName"};

    private final String basePath;

    protected AbstractStatControllerTest(final String basePath) {
        this.basePath = basePath;
    }

    protected abstract T getStatService();

    protected Stat generateStat() {
        return StatUtilities.generateStat(this.getStatService());
    }

    protected Stat generateStat(final String statName) {
        return StatUtilities.generateStat(this.getStatService(), statName);
    }

    protected MockMvcResponse getInsertResponse(final MockMvcRequestSpecification specification) {
        final String statName = generateStatName();
        final String websiteName = generateStatName();
        final String cleanName = generateStatName();
        final boolean achievement = true;

        return this.getInsertResponse(
                specification,
                statName,
                websiteName,
                cleanName,
                achievement
        );
    }

    protected MockMvcResponse getInsertResponse(final MockMvcRequestSpecification specification,
                                                final String statName,
                                                final String websiteName,
                                                final String cleanName,
                                                final boolean achievement) {
        return specification
                .param("websiteName", websiteName)
                .param("cleanName", cleanName)
                .param("isAchievement", achievement)
                .when()
                .put(this.basePath + "/" + statName);
    }

    @SneakyThrows
    @Test
    void getStats() {
        final List<Stat> expectedStats = new ArrayList<>();
        for (int count = 0; 10 >= count; count++) {
            expectedStats.add(this.generateStat());
        }

        final List<Stat> foundStats = this.parseResponse(
                this.getWithNoApiKey()
                        .when()
                        .get(this.basePath),
                new TypeToken<ArrayList<Stat>>() {
                }
        );
        assertThat(foundStats)
                .usingElementComparatorIgnoringFields(STAT_IGNORED_FIELDS)
                .containsAll(expectedStats);
    }

    @Test
    void getStat() {
        final String statName = generateStatName();
        final Supplier<MockMvcResponse> responseSupplier = () -> this.getWithNoApiKey()
                .when()
                .get(this.basePath + "/" + statName);

        // Assure that the stat does not exist
        this.assertStatus(responseSupplier.get(), HttpStatus.NOT_FOUND);

        // Create stat
        final Stat stat = this.generateStat(statName);

        // Assure that the stat does exist
        final Stat statFound = this.parseResponse(
                responseSupplier.get(),
                Stat.class
        );
        assertThat(statFound)
                .usingRecursiveComparison()
                .ignoringFields(STAT_IGNORED_FIELDS)
                .isEqualTo(stat);
    }

    @Test
    void createStat_super_admin_perms() {
        final String statName = generateStatName();
        final String websiteName = generateStatName();
        final String cleanName = generateStatName();
        final boolean achievement = false;

        final Stat foundStat = this.parseResponse(
                this.getInsertResponse(
                        this.getWithSuperAdminPrivileges(),
                        statName,
                        websiteName,
                        cleanName,
                        achievement
                ),
                Stat.class
        );

        assertThat(foundStat.getStatName()).isEqualTo(statName);
        assertThat(foundStat.getCleanName()).isEqualTo(cleanName);
        assertThat(foundStat.isAchievement()).isEqualTo(achievement);
    }

    @Test
    void createStat_status_check_admin() {
        this.assertStatus(
                this.getInsertResponse(this.getWithAdminPrivileges()),
                HttpStatus.OK
        );
    }

    @Test
    void createStat_status_check_user() {
        this.assertStatus(
                this.getInsertResponse(this.getWithUserPrivileges()),
                HttpStatus.FORBIDDEN
        );
    }

    @Test
    void createStat_status_check_no_api_key() {
        this.assertStatus(
                this.getInsertResponse(this.getWithNoApiKey()),
                HttpStatus.UNAUTHORIZED
        );
    }
}