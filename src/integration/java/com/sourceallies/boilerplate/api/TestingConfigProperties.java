package com.sourceallies.boilerplate.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConfigurationProperties("testing")
@Data
@Profile("test")
public class TestingConfigProperties {

    @Builder
    @Data
    @AllArgsConstructor
    static class TestClient {
        String username;
        String client;
        String scopes;
    }

    String directGrantEndpoint;
    TestClient[] testClients;
}
