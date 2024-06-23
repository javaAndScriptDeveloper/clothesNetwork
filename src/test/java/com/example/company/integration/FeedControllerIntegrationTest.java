package com.example.company.integration;

import static com.example.company.config.ApplicationConfig.OBJECT_MAPPER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.company.dto.request.SearchFeedRequest;
import com.example.company.entity.FeedEntity;
import com.example.company.entity.PostEntity;
import io.github.glytching.junit.extension.random.Random;
import java.util.Comparator;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

public class FeedControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String FEED_ENTITY_URL = BASE_URl + "/feeds";

    @Test
    @SneakyThrows
    @WithMockUser
    void whenSearchFeed_ThenFeedReturned(@Random(type = PostEntity.class) List<PostEntity> toSavePostEntities) {

        // given
        var toSaveManagedBrandEntity = generateBrandEntity();
        var savedManagedBrandEntity = brandRepository.save(toSaveManagedBrandEntity);

        var toSaveUserEntity = generateUserEntity();
        toSaveUserEntity.setManagedBrand(savedManagedBrandEntity);
        var savedUserEntity = userRepository.save(toSaveUserEntity);

        toSavePostEntities.forEach(postEntity -> {
            postEntity.setUserAuthor(null);
            postEntity.setBrandAuthor(null);
            postEntity.setFeeds(List.of());
        });

        var savedPostEntities = postRepository.saveAll(toSavePostEntities);
        savedPostEntities.sort(Comparator.comparing(PostEntity::getUpdatedAt));

        var savedFeedEntity = feedRepository.save(FeedEntity.builder()
                .user(savedUserEntity)
                .posts(savedPostEntities)
                .build());

        savedUserEntity.setFeed(savedFeedEntity);
        userRepository.save(savedUserEntity);

        var searchFeedRequest = SearchFeedRequest.builder()
                .userId(savedUserEntity.getId())
                .startPosition(0)
                .step(5)
                .build();

        // expected
        mockMvc.perform(post(FEED_ENTITY_URL)
                        .content(OBJECT_MAPPER.writeValueAsString(searchFeedRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id")
                        .value(savedPostEntities.get(0).getId().toString()))
                .andExpect(jsonPath("$[0].authorType")
                        .value(savedPostEntities.get(0).getAuthorType().name()))
                .andExpect(jsonPath("$[0].textContent")
                        .value(savedPostEntities.get(0).getTextContent()))
                .andExpect(jsonPath("$[1].id")
                        .value(savedPostEntities.get(1).getId().toString()))
                .andExpect(jsonPath("$[1].authorType")
                        .value(savedPostEntities.get(1).getAuthorType().name()))
                .andExpect(jsonPath("$[1].textContent")
                        .value(savedPostEntities.get(1).getTextContent()));
    }
}
