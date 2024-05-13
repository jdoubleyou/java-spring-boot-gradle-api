package com.sourceallies.boilerplate.api;

import io.netty.handler.codec.http.HttpHeaderNames;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BaseIntegrationTest {
    static final ParameterizedTypeReference<Map<String, String>> JSON_MAP = new ParameterizedTypeReference<>() {
    };

    @LocalServerPort
    int port;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void init() {
        mockWebServer = new MockWebServer();
    }

    @Autowired
    TestingConfigProperties testingConfigProperties;

    @Autowired
    DatabaseClient databaseClient;

    @BeforeEach
    void cleanup(@Value("classpath:db/cleanup.sql") Resource sqlFile) throws IOException {
        //noinspection SqlSourceToSinkFlow
        databaseClient.sql(sqlFile.getContentAsString(StandardCharsets.UTF_8)).then().block();
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
            .toEntity(JSON_MAP)
            .getBody()
            .getOrDefault("access_token", "NO_ACCESS_TOKEN_FOUND");
    }

    protected final WebTestClient getAuthorizedWebTestClient() {
        var testClient = testingConfigProperties.getTestClients()[0];
        String accessToken = getAccessTokenFor(
            testingConfigProperties.getDirectGrantEndpoint(),
            testClient.getUsername(),
            testClient.getClient(),
            Arrays.asList(testClient.getScopes().split(" "))
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
}
