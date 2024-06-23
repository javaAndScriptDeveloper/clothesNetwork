package com.example.company.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

public class SecurityIntegrationTest extends AbstractIntegrationTest {

    @Test
    @SneakyThrows
    void whenGetUser_AndValidCredentialsPassed_NoAuthErrorThrown() {

        // given
        var savedUserEntity = userRepository.save(generateUserEntity());

        // expected
        mockMvc.perform(get(BASE_URl + "/users/" + savedUserEntity.getId())
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                encodeCredentials(savedUserEntity.getUsername(), savedUserEntity.getPassword())))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
