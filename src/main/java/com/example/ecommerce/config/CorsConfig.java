package com.example.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // السماح للـ React app بالوصول للـ API
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",  // React development server
                "http://127.0.0.1:3000",  // Alternative localhost
                "http://localhost:3001"   // Backup port if needed
        ));
        
        // السماح بجميع HTTP methods المطلوبة
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // السماح بجميع headers المطلوبة
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // السماح بإرسال credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // تحديد headers التي يمكن للـ frontend قراءتها
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization", 
                "Content-Type", 
                "Accept",
                "X-Requested-With",
                "Cache-Control"
        ));
        
        // مدة cache للـ preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
