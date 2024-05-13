package com.sourceallies.boilerplate.api;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

class CoffeeHouseEndpointsTest extends BaseIntegrationTest {
    private static final String MENUS_ENDPOINT = "/menus";
    private static final String CUSTOMERS_ENDPOINT = "/customers";
    @Test
    void shouldHaveMenuLifecycleEndpoints() {
        var client = getAuthorizedWebTestClient();
        client.get()
            .uri(MENUS_ENDPOINT)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(JSON_MAP)
            .isEqualTo(Collections.emptyList())
        ;

        var name = "Sampler Menu";
        var updatedName = "Dinner Menu";

        AtomicReference<String> location = new AtomicReference<>();
        client.post()
            .uri(MENUS_ENDPOINT)
            .header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE)
            .bodyValue("""
                {"name": "%s"}""".formatted(name))
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists(HttpHeaderNames.LOCATION.toString())
            .expectHeader().value(HttpHeaderNames.LOCATION.toString(), location::set)
        ;

        var id = location.get().replace(MENUS_ENDPOINT + "/", "");
        client.get()
            .uri(location.get())
            .exchange()
            .expectAll(
                spec -> spec.expectStatus().isOk(),
                spec -> spec.expectBody()
                    .jsonPath("$.name").isEqualTo(name)
                    .jsonPath("$.id").isEqualTo(id)
            )
        ;

        client.put()
            .uri(location.get())
            .header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE)
            .bodyValue("""
                {"name": "%s"}""".formatted(updatedName))
            .exchange()
            .expectStatus().isNoContent()
        ;

        client.get()
            .uri(location.get())
            .exchange()
            .expectAll(
                spec -> spec.expectStatus().isOk(),
                spec -> spec.expectBody()
                    .jsonPath("$.name").isEqualTo(updatedName)
                    .jsonPath("$.id").isEqualTo(id)
            )
        ;

        client.get()
            .uri(MENUS_ENDPOINT)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(JSON_MAP)
            .hasSize(1)
            .value(bodies -> SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(bodies.getFirst()).isNotNull();
                    softly.assertThat(bodies.getFirst().get("id")).isEqualTo(id);
                    softly.assertThat(bodies.getFirst().get("name")).isEqualTo(updatedName);
                    softly.assertThat(bodies.getFirst()).containsKey("createdDate");
                    softly.assertThat(bodies.getFirst()).containsKey("lastUpdatedDate");
                })
            )
        ;

        client.delete()
            .uri(location.get())
            .exchange()
            .expectStatus().isNoContent()
        ;

        client.get()
            .uri(location.get())
            .exchange()
            .expectStatus().isNotFound()
        ;

        client.delete()
            .uri(location.get())
            .exchange()
            .expectStatus().isNoContent()
        ;

        client.get()
            .uri(MENUS_ENDPOINT)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(JSON_MAP)
            .isEqualTo(Collections.emptyList())
        ;
    }

    @ParameterizedTest
    @MethodSource("invalidMenus")
    void shouldValidateIncomingCreateMenuRequest(String body) {
        getAuthorizedWebTestClient().post()
            .uri(MENUS_ENDPOINT)
            .header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(body)
            .exchange()
            .expectBody()
            .jsonPath("$.status").isEqualTo("400")
            .jsonPath("$.detail").isEqualTo("The given request is not valid for the endpoint.")
            .jsonPath("$.instance").isEqualTo(MENUS_ENDPOINT)
            .jsonPath("$.title").isEqualTo("Bad Request")
            .jsonPath("$.type").isEqualTo("about:blank")
            .jsonPath("$.errors").exists()
            .jsonPath("$.errors").isNotEmpty()
            .jsonPath("$.errors").isArray()
        ;
    }

    @ParameterizedTest
    @MethodSource("invalidMenus")
    void shouldValidateIncomingUpdateMenuRequest(String body) {
        getAuthorizedWebTestClient().put()
            .uri(MENUS_ENDPOINT + "/1")
            .header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(body)
            .exchange()
            .expectBody()
            .jsonPath("$.status").isEqualTo("400")
            .jsonPath("$.detail").isEqualTo("The given request is not valid for the endpoint.")
            .jsonPath("$.instance").isEqualTo(MENUS_ENDPOINT + "/1")
            .jsonPath("$.title").isEqualTo("Bad Request")
            .jsonPath("$.type").isEqualTo("about:blank")
            .jsonPath("$.errors").exists()
            .jsonPath("$.errors").isNotEmpty()
            .jsonPath("$.errors").isArray()
        ;
    }

    private static Stream<String> invalidMenus() {
        return Stream.of(
            """
                {"name": null}""",
            """
                {"name": ""}""",
            """
                {}"""
        );
    }

    @Test
    void shouldHaveCustomerLifecycleEndpoints() {
        var client = getAuthorizedWebTestClient();
        client.get()
            .uri(CUSTOMERS_ENDPOINT)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(JSON_MAP)
            .isEqualTo(Collections.emptyList())
        ;

        var name = "Jim Johnsson";
        var updatedName = "John Jimsson";

        AtomicReference<String> location = new AtomicReference<>();
        client.post()
            .uri(CUSTOMERS_ENDPOINT)
            .header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE)
            .bodyValue("""
                {"name": "%s"}""".formatted(name))
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists(HttpHeaderNames.LOCATION.toString())
            .expectHeader().value(HttpHeaderNames.LOCATION.toString(), location::set)
        ;

        var id = location.get().replace(CUSTOMERS_ENDPOINT + "/", "");
        client.get()
            .uri(location.get())
            .exchange()
            .expectAll(
                spec -> spec.expectStatus().isOk(),
                spec -> spec.expectBody()
                    .jsonPath("$.name").isEqualTo(name)
                    .jsonPath("$.id").isEqualTo(id)
            )
        ;

        client.put()
            .uri(location.get())
            .header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE)
            .bodyValue("""
                {"name": "%s"}""".formatted(updatedName))
            .exchange()
            .expectStatus().isNoContent()
        ;

        client.get()
            .uri(location.get())
            .exchange()
            .expectAll(
                spec -> spec.expectStatus().isOk(),
                spec -> spec.expectBody()
                    .jsonPath("$.name").isEqualTo(updatedName)
                    .jsonPath("$.id").isEqualTo(id)
            )
        ;

        client.get()
            .uri(CUSTOMERS_ENDPOINT)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(JSON_MAP)
            .hasSize(1)
            .value(bodies -> SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(bodies.getFirst()).isNotNull();
                    softly.assertThat(bodies.getFirst().get("id")).isEqualTo(id);
                    softly.assertThat(bodies.getFirst().get("name")).isEqualTo(updatedName);
                    softly.assertThat(bodies.getFirst()).containsKey("createdDate");
                    softly.assertThat(bodies.getFirst()).containsKey("lastUpdatedDate");
                })
            )
        ;

        client.delete()
            .uri(location.get())
            .exchange()
            .expectStatus().isNoContent()
        ;

        client.get()
            .uri(location.get())
            .exchange()
            .expectStatus().isNotFound()
        ;

        client.delete()
            .uri(location.get())
            .exchange()
            .expectStatus().isNoContent()
        ;

        client.get()
            .uri(CUSTOMERS_ENDPOINT)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(JSON_MAP)
            .isEqualTo(Collections.emptyList())
        ;
    }

    @ParameterizedTest
    @MethodSource("invalidCustomers")
    void shouldValidateIncomingCreateCustomerRequest(String body) {
        getAuthorizedWebTestClient().post()
            .uri(CUSTOMERS_ENDPOINT)
            .header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(body)
            .exchange()
            .expectBody()
            .jsonPath("$.status").isEqualTo("400")
            .jsonPath("$.detail").isEqualTo("The given request is not valid for the endpoint.")
            .jsonPath("$.instance").isEqualTo(CUSTOMERS_ENDPOINT)
            .jsonPath("$.title").isEqualTo("Bad Request")
            .jsonPath("$.type").isEqualTo("about:blank")
            .jsonPath("$.errors").exists()
            .jsonPath("$.errors").isNotEmpty()
            .jsonPath("$.errors").isArray()
        ;
    }

    @ParameterizedTest
    @MethodSource("invalidCustomers")
    void shouldValidateIncomingUpdateCustomerRequest(String body) {
        getAuthorizedWebTestClient().put()
            .uri(CUSTOMERS_ENDPOINT + "/1")
            .header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(body)
            .exchange()
            .expectBody()
            .jsonPath("$.status").isEqualTo("400")
            .jsonPath("$.detail").isEqualTo("The given request is not valid for the endpoint.")
            .jsonPath("$.instance").isEqualTo(CUSTOMERS_ENDPOINT + "/1")
            .jsonPath("$.title").isEqualTo("Bad Request")
            .jsonPath("$.type").isEqualTo("about:blank")
            .jsonPath("$.errors").exists()
            .jsonPath("$.errors").isNotEmpty()
            .jsonPath("$.errors").isArray()
        ;
    }

    private static Stream<String> invalidCustomers() {
        return Stream.of(
            """
                {"name": null}""",
            """
                {"name": ""}""",
            """
                {}"""
        );
    }
}
