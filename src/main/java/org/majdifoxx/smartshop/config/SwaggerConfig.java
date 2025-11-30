package org.majdifoxx.smartshop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger configuration for API testing
 * Brief says: "Tests et simulation: Via Postman ou Swagger"
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI smartShopAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartShop API")
                        .description("Backend REST API - MicroTech Maroc Commercial Management")
                        .version("1.0.0"));
    }
}
