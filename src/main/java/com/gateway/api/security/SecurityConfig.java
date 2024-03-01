package com.gateway.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasAuthority;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.oauth2.core.authorization.OAuth2ReactiveAuthorizationManagers.hasScope;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/users/signup").permitAll() // here scope_ is not the prefix, its the claim name in jwt token
                        .pathMatchers("/api/v1/products/**").access(hasScope("scope_view_products")) // we have used profile scope in jwt token so validating it here
                // as of now ,   we are using scopes in jwt token so we are validating it her
                // roles not working wil debug later
                  /*
                        .pathMatchers("/api/v1/products/**").hasAuthority("SCOPE_ADMIN") // Neither this is working
                        .pathMatchers("/api/v1/products/**").hasRole("SCOPE_ADMIN")     // Nor this is working
                    */
                        .anyExchange().authenticated()
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2ResourceServer((resourceServer) ->
                        resourceServer.jwt(withDefaults())
                );
        return http.build();
    }

    /**
     * In above case, we are using scopes in jwt token for authorization , but usually we use roles for authorization.
     *  So if we want to use roles for authorization, we need to convert the roles from jwt token to authorities.
     *  This is we are setting at auhserver in jwtTokenCustomizer() method.
     */
   // @Bean  // disabling for now because roles not working, will debug later this flow
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
      //  grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities"); // default is "authorities" if not set in jwt token claim name
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // we have used roles in jwt token claim name so setting it here
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); // default is "SCOPE_" if not set in jwt token claim name so changing it to "ROLE_"

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

}
