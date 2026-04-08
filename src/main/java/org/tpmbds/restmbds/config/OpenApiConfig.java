package org.tpmbds.restmbds.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI datasetGeneratorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dataset Generator API")
                        .description("API REST pour la génération de datasets")
                        .version("1.0.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentation du projet"));
    }
}