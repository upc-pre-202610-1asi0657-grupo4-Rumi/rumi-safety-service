package com.rumi.safety.infrastructure;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI safetyServiceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Rumi Safety Service API")
                .description("REST API of the Resident Safety bounded context: evacuation plans of buildings (US19) "
                        + "and the post-earthquake self-check of residents (US20). It sits behind the API gateway, "
                        + "which validates the JWT and forwards the caller in the X-User-Id, X-Role and "
                        + "X-Building-Id headers. Errors are RFC 7807 problem details.")
                .version("0.1.0"));
    }
}
