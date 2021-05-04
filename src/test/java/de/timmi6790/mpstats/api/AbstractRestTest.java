package de.timmi6790.mpstats.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.timmi6790.mpstats.api.apikey.ApiKeyService;
import de.timmi6790.mpstats.api.ratelimit.RateLimitService;
import de.timmi6790.mpstats.api.utilities.ApiKeyUtilities;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractRestTest extends AbstractSpringBootTest {
    protected static final String API_KEY_HEADER = "X-Api-Key";

    protected final Gson gson = new GsonBuilder().create();

    @Autowired
    protected ApiKeyService apiKeyService;

    @Autowired
    protected RateLimitService rateLimitService;

    protected MockMvcRequestSpecification getWithSuperAdminPrivileges() {
        return this.getWithApiKey(ApiKeyUtilities.getSuperAdminApiKey(this.apiKeyService));
    }

    protected MockMvcRequestSpecification getWithAdminPrivileges() {
        return this.getWithApiKey(ApiKeyUtilities.getAdminApiKey(this.apiKeyService));
    }

    protected MockMvcRequestSpecification getWithUserPrivileges() {
        return this.getWithApiKey(ApiKeyUtilities.getUserApiKey(this.apiKeyService));
    }

    protected MockMvcRequestSpecification getWithApiKey(final UUID apiKey) {
        return RestAssuredMockMvc.given()
                .header(API_KEY_HEADER, apiKey);
    }

    protected MockMvcRequestSpecification getWithNoApiKey() {
        return RestAssuredMockMvc.given();
    }

    protected <T> T parseResponse(final MockMvcResponse response, final Class<T> type) {
        return this.parseResponse(response, TypeToken.get(type));
    }

    @SneakyThrows
    protected <T> T parseResponse(final MockMvcResponse response, final TypeToken<T> type) {
        try (final InputStream inputStream = response.then()
                .log()
                .ifValidationFails()
                .status(HttpStatus.OK)
                .contentType(ContentType.JSON)
                .extract().response()
                .asInputStream()) {
            final Reader reader = new InputStreamReader(inputStream);
            return this.gson.fromJson(reader, type.getType());
        }
    }

    protected void assertStatus(final MockMvcResponse response, final HttpStatus requiredStatus) {
        response
                .then()
                .log()
                .ifValidationFails()
                .assertThat(result -> assertThat(result.getResponse().getStatus()).isEqualTo(requiredStatus.value()));
    }

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.mockMvc(this.mockMvc);
        this.rateLimitService.invalidateCache();
    }
}
