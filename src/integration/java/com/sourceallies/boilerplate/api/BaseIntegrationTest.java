package com.sourceallies.boilerplate.api;

import io.netty.handler.codec.http.HttpHeaderNames;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

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

    protected final String getAccessTokenFor(String directGrantEndpoint, String user, String client, List<String> scopes) {
        var body = new LinkedMultiValueMap<String, String>();
        body.put("client_id", List.of(client));
        body.put("client_secret", List.of("%s_secret".formatted(client)));
        body.put("username", List.of(user));
        body.put("password", List.of("%s_password".formatted(user)));
        body.put("grant_type", List.of("password"));
        body.put("scope", List.of(String.join(" ", scopes)));

        return RestClient.builder().build()
            .post()
            .uri(directGrantEndpoint)
            .body(body)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<Map<String, String>>() {
            })
            .getBody()
            .getOrDefault("access_token", "NO_ACCESS_TOKEN_FOUND");
    }

    protected final WebTestClient getAuthorizedWebTestClient() {
        String accessToken = getAccessTokenFor(
            "http://localhost:8081/realms/custom_realm/protocol/openid-connect/token",
            "test_user",
            "test_client",
            List.of("openid", "email", "profile", "roles")
        );
        return WebTestClient
            .bindToServer()
            .baseUrl(getServerBaseUrl())
            .defaultHeader(HttpHeaderNames.AUTHORIZATION.toString(), bearerToken(accessToken))
            .build();
    }

    protected final String bearerToken(String token) {
        return "Bearer %s".formatted(token);
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

    @Test
    void shouldHaveAInfoEndpointThatRequiresAuthorization() {
        getUnauthorizedWebTestClient()
            .get()
            .uri("/actuator/info")
            .exchange()
            .expectStatus().isUnauthorized();
        getAuthorizedWebTestClient()
            .get()
            .uri("/actuator/info")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.application.name").isNotEmpty()
            .jsonPath("$.git.commit.id.full").isNotEmpty()
            .jsonPath("$.git.commit.time").isNotEmpty()
            .jsonPath("$.git.branch").isNotEmpty()
            .jsonPath("$.build").doesNotExist()
            .jsonPath("$.java").doesNotExist()
        ;
    }
}
