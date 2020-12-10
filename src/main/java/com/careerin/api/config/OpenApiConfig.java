package com.careerin.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class OpenApiConfig {

   /* @Bean
    public OpenAPI customCofiguration() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("can-next-init")
                        .description("swagger for can-next-api"));
    }
    */
    @Bean
    public OpenAPI customCofiguration() {
        final SecurityScheme securitySchemesItem =
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearer-key", securitySchemesItem))
                .info(info());
    }

    private Info info() {

        return new Info().title("CareerIn API")
                .version("version-1")
                .description("CareerIn API operations");
    }

    @Bean
    public RouterFunction<ServerResponse> staticResourceLocator() {
        return RouterFunctions
                .resources("/careerin-api/**", new ClassPathResource("public/"));
    }
}