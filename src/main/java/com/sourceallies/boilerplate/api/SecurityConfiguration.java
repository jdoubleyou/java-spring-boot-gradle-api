package com.sourceallies.boilerplate.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {
    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
        return http
            .authorizeExchange(exchanges ->
                exchanges
                    .pathMatchers("/actuator/health").permitAll()
                    .pathMatchers("/actuator/info").authenticated()
                    .pathMatchers("/public/**").permitAll()
                    .anyExchange().authenticated()
            )
            .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
            .build()
            ;
    }

    @Bean
    ReactiveJwtDecoder reactiveJwtDecoder(SecurityProperties securityProperties) {
        return ReactiveJwtDecoders.fromIssuerLocation(securityProperties.getJwtIssuer());
    }
}
