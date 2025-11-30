package org.majdifoxx.smartshop.config;

import org.springframework.context.annotation.Configuration;

/**
 * HTTP Session configuration
 * Brief says: "Authentification: HTTP Session, Pas de JWT, Pas de Spring Security"
 * Session timeout already configured in application.properties (30 min)
 */
@Configuration
public class SessionConfig {
    // Session configuration is in application.properties
    // This class can be expanded if needed
}
