package de.timmi6790.mpstats.api.apikey;

import de.timmi6790.mpstats.api.AbstractRestTest;
import de.timmi6790.mpstats.api.apikey.models.ApiKey;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.NestedServletException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

class ApiKeyControllerTest extends AbstractRestTest {
    private ApiKey getApiKey(final int dailyRateLimit, final int minuteRateLimit) {
        return this.parseResponse(
                this.getWithSuperAdminPrivileges()
                        .param("dailyRateLimit", dailyRateLimit)
                        .param("minuteRateLimit", minuteRateLimit)
                        .when()
                        .post("/apiKey/create"),
                ApiKey.class
        );
    }

    private void assertStatus(final MockMvcRequestSpecification requestSpecification, final HttpStatus requiredStatus) {
        this.assertStatus(
                requestSpecification
                        .param("dailyRateLimit", 10)
                        .param("minuteRateLimit", 10)
                        .when()
                        .post("/apiKey/create"),
                requiredStatus
        );
    }

    @Test
    void creatNewApiKey() {
        final int dailyRateLimit = 1;
        final int minuteRateLimit = 900;

        final ApiKey apiKey = this.getApiKey(dailyRateLimit, minuteRateLimit);

        assertThat(apiKey.getRateLimit().getDaily()).isEqualTo(dailyRateLimit);
        assertThat(apiKey.getRateLimit().getMinute()).isEqualTo(minuteRateLimit);
    }

    @Test
    void createNeApiKey_invalid_daily() {
        assertThrows(NestedServletException.class, () -> this.getApiKey(0, 100));
    }

    @Test
    void createNeApiKey_invalid_minute() {
        assertThrows(NestedServletException.class, () -> this.getApiKey(100, 0));
    }

    @Test
    void createNeApiKey_no_api_key() {
        this.assertStatus(this.getWithNoApiKey(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void createNeApiKey_invalid_key_admin() {
        this.assertStatus(this.getWithAdminPrivileges(), HttpStatus.FORBIDDEN);
    }

    @Test
    void createNeApiKey_invalid_key_user() {
        this.assertStatus(this.getWithUserPrivileges(), HttpStatus.FORBIDDEN);
    }
}