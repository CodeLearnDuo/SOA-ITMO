package soa.duo.configserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Применяется ко всем маршрутам
                        .allowedOrigins("http://localhost:3000", "https://se.ifmo.ru/") // Разрешённые источники
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Разрешённые методы
                        .allowedHeaders("*") // Разрешённые заголовки
                        .allowCredentials(true); // Разрешение отправки кук
            }
        };
    }
}