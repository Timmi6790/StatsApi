package de.timmi6790.mpstats.api.apikey;

import de.timmi6790.mpstats.api.AbstractRestTest;
import de.timmi6790.mpstats.api.apikey.models.CreatedApiKey;
import de.timmi6790.mpstats.api.apikey.models.RateLimit;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ApiKeyControllerTest extends AbstractRestTest {
    private CreatedApiKey getApiKey(final int dailyRateLimit, final int minuteRateLimit) {
        return this.parseResponse(
                this.getWithSuperAdminPrivileges()
                        .param("dailyRateLimit", dailyRateLimit)
                        .param("minuteRateLimit", minuteRateLimit)
                        .when()
                        .post("/apiKey/create"),
                CreatedApiKey.class
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

        final CreatedApiKey apiKey = this.getApiKey(dailyRateLimit, minuteRateLimit);

        final RateLimit rateLimit = apiKey.getKeyInformation().getRateLimit();
        assertThat(rateLimit.getDaily()).isEqualTo(dailyRateLimit);
        assertThat(rateLimit.getMinute()).isEqualTo(minuteRateLimit);
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