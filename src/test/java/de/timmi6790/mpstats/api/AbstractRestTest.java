package de.timmi6790.mpstats.api;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractRestTest extends AbstractSpringBootTest {
    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.mockMvc(this.mockMvc);
    }
}
