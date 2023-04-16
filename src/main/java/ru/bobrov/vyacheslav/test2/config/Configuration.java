package ru.bobrov.vyacheslav.test2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {
    @Bean
    public ObjectMapper mapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .build();
    }
}
