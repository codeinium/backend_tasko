package ru.kpfu.codeinium.tasko.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import ru.kpfu.codeinium.tasko.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ApplicationConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
