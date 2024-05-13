package com.sourceallies.boilerplate.api;

import org.junit.jupiter.api.Test;

class ActuatorEndpointsTest extends BaseIntegrationTest {

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
