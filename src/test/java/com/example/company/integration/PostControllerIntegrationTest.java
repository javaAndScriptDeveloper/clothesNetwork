package com.example.company.integration;

import static com.example.company.config.ApplicationConfig.OBJECT_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.company.dto.request.CreatePostRequest;
import io.github.glytching.junit.extension.random.Random;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

public class PostControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String POSTS_ENTITY_URL = BASE_URl + "/posts";

    @Test
    @SneakyThrows
    @WithMockUser
    void whenCreatePost_ThenPostCreated(@Random CreatePostRequest createPostRequest) {
        // given
        var authorId =
                switch (createPostRequest.getAuthorType()) {
                    case USER -> userRepository.save(generateUserEntity()).getId();
                    case BRAND -> brandRepository.save(generateBrandEntity()).getId();
                };
        createPostRequest.setAuthorId(authorId);

        // when
        mockMvc.perform(post(POSTS_ENTITY_URL)
                        .content(OBJECT_MAPPER.writeValueAsString(createPostRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        var savedPostEntity = postRepository.findAll().getFirst();
        assertThat(savedPostEntity)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "brandAuthor", "feeds", "id", "userAuthor", "updatedAt")
                .isEqualTo(createPostRequest);
    }
}
