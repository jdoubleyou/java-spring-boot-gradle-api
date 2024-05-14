package com.sourceallies.boilerplate.api;

import io.netty.handler.codec.http.HttpHeaderNames;
import net.bytebuddy.utility.RandomString;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

class AccountEndpointsTest extends BaseIntegrationTest {
    private static final String ACCOUNTS_ENDPOINT = "/accounts";


    private String createAccount(String name) {
        AtomicReference<String> location = new AtomicReference<>();
        getAuthorizedWebTestClient().post()
            .uri(ACCOUNTS_ENDPOINT)
            .header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE)
            .bodyValue("""
                {"name": "%s"}""".formatted(name))
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists(HttpHeaderNames.LOCATION.toString())
            .expectHeader().value(HttpHeaderNames.LOCATION.toString(), location::set)
        ;
        return location.get();
    }

    @Test
    void shouldHaveAccountLifecycleEndpoints() {
        var endpoint = ACCOUNTS_ENDPOINT;
        var client = getAuthorizedWebTestClient();
        client.get()
            .uri(endpoint)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(JSON_MAP)
            .isEqualTo(Collections.emptyList())
        ;

        var name = RandomString.make();
        var updatedName = RandomString.make();
        var location = createAccount(name);
        var id = location.replace(ACCOUNTS_ENDPOINT + "/", "");

        client.get()
            .uri(ACCOUNTS_ENDPOINT + "/" + id)
            .exchange()
            .expectAll(
                spec -> spec.expectStatus().isOk(),
                spec -> spec.expectBody()
                    .jsonPath("$.name").isEqualTo(name)
                    .jsonPath("$.id").isEqualTo(id)
            )
        ;

        client.put()
            .uri(ACCOUNTS_ENDPOINT + "/" + id)
            .header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE)
            .bodyValue("""
                {"name": "%s"}""".formatted(updatedName))
            .exchange()
            .expectStatus().isNoContent()
        ;

        client.get()
            .uri(location)
            .exchange()
            .expectAll(
                spec -> spec.expectStatus().isOk(),
                spec -> spec.expectBody()
                    .jsonPath("$.name").isEqualTo(updatedName)
                    .jsonPath("$.id").isEqualTo(id)
            )
        ;

        client.get()
            .uri(endpoint)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(JSON_MAP)
            .hasSize(1)
            .value(bodies -> SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(bodies.getFirst()).isNotNull();
                    softly.assertThat(bodies.getFirst().get("id")).isEqualTo(id);
                    softly.assertThat(bodies.getFirst().get("name")).isEqualTo(updatedName);
                })
            )
        ;

        var secondName = RandomString.make();
        var secondAccount = createAccount(secondName);
        var secondId = secondAccount.replace(ACCOUNTS_ENDPOINT + "/", "");

        client.put()
            .uri(location + "/children/" + secondId)
            .exchange()
            .expectStatus().isNoContent()
        ;

        client.get()
            .uri(location + "/children")
            .exchange()
            .expectAll(
                spec -> spec.expectStatus().isOk(),
                spec -> spec.expectBody()
                    .jsonPath("$.name").isEqualTo(updatedName)
                    .jsonPath("$.id").isEqualTo(id)
                    .jsonPath("$.children").isArray()
                    .jsonPath("$.children[0].name").isEqualTo(secondName)
                    .jsonPath("$.children[0].id").isEqualTo(secondId)
            )
        ;

        client.delete()
            .uri(location)
            .exchange()
            .expectStatus().isNoContent()
        ;

        client.get()
            .uri(ACCOUNTS_ENDPOINT + "/" + secondId)
            .exchange()
            .expectAll(
                spec -> spec.expectStatus().isOk(),
                spec -> spec.expectBody()
                    .jsonPath("$.name").isEqualTo(secondName)
                    .jsonPath("$.id").isEqualTo(secondId)
            )
        ;

        client.get()
            .uri(location)
            .exchange()
            .expectStatus().isNotFound()
        ;

        client.delete()
            .uri(ACCOUNTS_ENDPOINT + "/" + secondId)
            .exchange()
            .expectStatus().isNoContent()
        ;

        client.get()
            .uri(ACCOUNTS_ENDPOINT + "/" + secondId)
            .exchange()
            .expectStatus().isNotFound()
        ;

        client.get()
            .uri(endpoint)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(JSON_MAP)
            .isEqualTo(Collections.emptyList())
        ;
    }
}
