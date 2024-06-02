package com.example.company.integration;

import static com.example.company.config.ApplicationConfig.OBJECT_MAPPER;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.company.dto.request.CreateUserRequest;
import com.example.company.repository.ImageRepository;
import com.example.company.repository.UserRepository;
import io.github.glytching.junit.extension.random.Random;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    @SneakyThrows
    void whenCreateUser_ThenUserCreated(@Random CreateUserRequest createUserRequest) {
        // when
        mockMvc.perform(post(BASE_URl + "/users")
                        .content(OBJECT_MAPPER.writeValueAsString(createUserRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        var savedUser = userRepository.findAll().getFirst();
        assertEquals(createUserRequest.getUsername(), savedUser.getUsername());
        assertEquals(createUserRequest.getEmail(), savedUser.getEmail());
        assertEquals(createUserRequest.getPhoneNumber(), savedUser.getPhoneNumber());
        assertEquals(
                createUserRequest.getProfileImages().size(),
                savedUser.getProfileImages().size());
        assertArrayEquals(
                createUserRequest.getProfileImages().getFirst().getData(),
                savedUser.getProfileImages().getFirst().getData());
    }
}
