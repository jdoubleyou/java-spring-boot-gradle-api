package com.sourceallies.boilerplate.api;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("boilerplate.oauth2")
public class SecurityConfig {
    private String jwtIssuer;
}
