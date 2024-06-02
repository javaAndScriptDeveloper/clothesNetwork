package com.example.company.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
}
