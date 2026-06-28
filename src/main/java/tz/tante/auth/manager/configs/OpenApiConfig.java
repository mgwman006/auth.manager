package tz.tante.auth.manager.configs;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
  name = "bearerAuth",
  type = SecuritySchemeType.HTTP,
  scheme = "bearer",
  bearerFormat = "JWT"
)
public class OpenApiConfig {

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
      .group("auth-manager")
      .packagesToScan("tz.tante.auth.manager.controllers")
      .build();
  }


  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .addServersItem(new Server().url("https://api.auth.tante.tz"));
  }
}
