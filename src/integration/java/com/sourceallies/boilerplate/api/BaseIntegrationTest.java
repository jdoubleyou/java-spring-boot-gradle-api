package com.sourceallies.boilerplate.api;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BaseIntegrationTest {
    @LocalServerPort
    int port;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void init() {
        mockWebServer = new MockWebServer();
    }

    @BeforeEach
    void setup() {
    }

    @AfterEach
    void teardown() {
    }

    @AfterAll
    static void deinit() throws Exception {
        mockWebServer.shutdown();
    }

    protected final String getServerBaseUrl() {
        return "http://localhost:%s".formatted(port);
    }

    protected final WebTestClient getUnauthorizedWebTestClient() {
        return WebTestClient
            .bindToServer()
            .baseUrl(getServerBaseUrl())
            .build();
    }

    @Test
    void shouldHaveAPublicHealthEndpoint() {
        getUnauthorizedWebTestClient()
            .get()
            .uri("/actuator/health")
            .exchange()
            .expectBody().jsonPath("$.status").isEqualTo("UP")
            ;
    }
}
