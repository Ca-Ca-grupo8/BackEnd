package com.caeca.estoque.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Bearer",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CA&CA API")
                        .description("API REST para gerenciamento de estoque, incluindo controle de produtos, entradas, saídas e relatórios.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("grupo082ano-@sptech.school")
                        )
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Documentação completa do Projeto")
                        .url("https://bandteccom-my.sharepoint.com/...")
                )
                .addTagsItem(new Tag()
                        .name("Usuários")
                        .description("Operações relacionadas aos usuários"));
    }
}