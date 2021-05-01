package de.timmi6790.mpstats.api.apikey;

import com.google.gson.Gson;
import de.timmi6790.mpstats.api.AbstractRestTest;
import de.timmi6790.mpstats.api.apikey.models.ApiKey;
import de.timmi6790.mpstats.api.utilities.ApiKeyUtilities;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.util.NestedServletException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

class ApiKeyControllerTest extends AbstractRestTest {
    @Autowired
    private ApiKeyService apiKeyService;

    private ApiKey getApiKey(final int dailyRateLimit, final int minuteRateLimit) {
        final InputStream inputStream = RestAssuredMockMvc.given()
                .header("X-Api-Key", ApiKeyUtilities.getSuperAdminApiKey(this.apiKeyService))
                .param("dailyRateLimit", dailyRateLimit)
                .param("minuteRateLimit", minuteRateLimit)
                .when()
                .post("/apiKey/create")
                .then()
                .log()
                .ifValidationFails()
                .status(HttpStatus.OK)
                .contentType(ContentType.JSON)
                .extract().response()
                .asInputStream();

        final Reader reader = new InputStreamReader(inputStream);
        return new Gson().fromJson(reader, ApiKey.class);
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
        RestAssuredMockMvc.given()
                .param("dailyRateLimit", 10)
                .param("minuteRateLimit", 10)
                .when()
                .post("/apiKey/create")
                .then()
                .log()
                .ifValidationFails()
                .assertThat(new StatusAssertion(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void createNeApiKey_invalid_key_admin() {
        RestAssuredMockMvc.given()
                .header("X-Api-Key", ApiKeyUtilities.getAdminApiKey(this.apiKeyService))
                .param("dailyRateLimit", 10)
                .param("minuteRateLimit", 10)
                .when()
                .post("/apiKey/create")
                .then()
                .log()
                .ifValidationFails()
                .assertThat(new StatusAssertion(HttpStatus.FORBIDDEN));
    }

    @Test
    void createNeApiKey_invalid_key_user() {
        RestAssuredMockMvc.given()
                .header("X-Api-Key", ApiKeyUtilities.getUserApiKey(this.apiKeyService))
                .param("dailyRateLimit", 10)
                .param("minuteRateLimit", 10)
                .when()
                .post("/apiKey/create")
                .then()
                .log()
                .ifValidationFails()
                .assertThat(new StatusAssertion(HttpStatus.FORBIDDEN));
    }

    @AllArgsConstructor
    private static class StatusAssertion implements ResultMatcher {
        private final HttpStatus httpStatus;

        @Override
        public void match(final MvcResult result) {
            assertThat(result.getResponse().getStatus()).isEqualTo(this.httpStatus.value());
        }
    }
}