package hotel_booking.config;

/**
 * CORS Configuration has been consolidated into SecurityConfig.java.
 * The CorsConfigurationSource bean in SecurityConfig handles all CORS settings
 * for Spring Security's filter chain, which takes precedence over WebMvcConfigurer.
 *
 * This class is intentionally left empty to avoid duplicate CORS configuration.
 * Do NOT re-add a WebMvcConfigurer here as it will conflict with SecurityConfig CORS.
 */
public class CorsConfig {
    // See SecurityConfig.corsConfigurationSource() for CORS configuration
}
