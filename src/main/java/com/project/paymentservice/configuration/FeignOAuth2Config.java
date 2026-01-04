package com.project.paymentservice.configuration;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@Configuration
public class FeignOAuth2Config {

    private final OAuth2AuthorizedClientManager clientManager;

    public FeignOAuth2Config(
            @Qualifier("authorizedClientManager")
            OAuth2AuthorizedClientManager clientManager
    ) {
        this.clientManager = clientManager;
    }

    @Bean
    public RequestInterceptor oauth2FeignInterceptor() {
        return requestTemplate -> {

            OAuth2AuthorizeRequest authorizeRequest =
                    OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
                            .principal("payment-service")
                            .build();

            OAuth2AuthorizedClient client =
                    clientManager.authorize(authorizeRequest);

            if (client == null || client.getAccessToken() == null) {
                throw new IllegalStateException("Failed to obtain access token from Keycloak");
            }

            requestTemplate.header(
                    "Authorization",
                    "Bearer " + client.getAccessToken().getTokenValue()
            );
        };
    }
}

