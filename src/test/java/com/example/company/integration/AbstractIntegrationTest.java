package com.example.company.integration;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RandomBeansExtension.class)
public class AbstractIntegrationTest {}
