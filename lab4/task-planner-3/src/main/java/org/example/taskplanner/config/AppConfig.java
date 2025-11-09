package org.example.taskplanner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.example.taskplanner.service.SimpleLogger;

@Configuration
public class AppConfig {

    @Bean
    public SimpleLogger singletonLogger() {
        return new SimpleLogger("Singleton Logger");
    }

    @Bean
    @Scope("prototype")
    public SimpleLogger prototypeLogger() {
        return new SimpleLogger("Prototype Logger");
    }
}
