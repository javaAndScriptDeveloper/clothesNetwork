package com.example.company.integration;

import static com.example.company.config.ApplicationConfig.OBJECT_MAPPER;
import static com.example.company.enums.AuthorType.BRAND;
import static com.example.company.enums.AuthorType.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.company.dto.post.PostViewCondition;
import com.example.company.entity.FeedEntity;
import com.example.company.entity.PostEntity;
import com.example.company.entity.UserEntity;
import com.example.company.entity.info.ViewConditionInfo;
import com.example.company.enums.SearchOperator;
import io.github.glytching.junit.extension.random.Random;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

public class PostControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String POSTS_ENTITY_URL = BASE_URl + "/posts";

    @Test
    @SneakyThrows
    @WithMockUser
    void whenCreatePost_ThenPostCreated() {
        // given
        var createPostRequest = generateCreatePostRequest();
        var authorId =
                switch (createPostRequest.getAuthorType()) {
                    case USER -> userRepository.save(generateUserEntity()).getId();
                    case BRAND -> brandRepository.save(generateBrandEntity()).getId();
                };
        createPostRequest.setViewConditions(null);
        createPostRequest.setAuthorId(authorId);

        // when
        mockMvc.perform(post(POSTS_ENTITY_URL)
                        .content(OBJECT_MAPPER.writeValueAsString(createPostRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        var savedPostEntity = postRepository.findAll().getFirst();
        assertFalse(savedPostEntity.getPosted());
        assertThat(savedPostEntity)
                .usingRecursiveComparison()
                .ignoringFields(
                        "createdAt",
                        "brandAuthor",
                        "feeds",
                        "id",
                        "userAuthor",
                        "updatedAt",
                        "publicationTime",
                        "posted")
                .isEqualTo(createPostRequest);
        assertEquals(
                Instant.ofEpochMilli(createPostRequest.getPublicationTime().getValue()),
                savedPostEntity.getPublicationTime());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    @Transactional
    void whenUpdatePost_ThenPostUpdated(@Random PostEntity toSaveInitialPostEntity) {
        // given
        var updatePostRequest = generateUpdatePostRequest();
        toSaveInitialPostEntity.setFeeds(List.of());
        switch (toSaveInitialPostEntity.getAuthorType()) {
            case USER -> {
                var userAuthorEntity = userRepository.save(generateUserEntity());
                toSaveInitialPostEntity.setUserAuthor(userAuthorEntity);
                toSaveInitialPostEntity.setBrandAuthor(null);
                updatePostRequest.setAuthorType(USER);
                updatePostRequest.setAuthorId(userAuthorEntity.getId());
            }
            case BRAND -> {
                var brandAuthorEntity = brandRepository.save(generateBrandEntity());
                toSaveInitialPostEntity.setBrandAuthor(brandAuthorEntity);
                toSaveInitialPostEntity.setUserAuthor(null);
                updatePostRequest.setAuthorType(BRAND);
                updatePostRequest.setAuthorId(brandAuthorEntity.getId());
            }
        }

        var savedInitialPostEntity = postRepository.save(toSaveInitialPostEntity);

        updatePostRequest.setViewConditions(List.of());

        // when
        mockMvc.perform(put("%s/%s".formatted(POSTS_ENTITY_URL, savedInitialPostEntity.getId()))
                        .content(OBJECT_MAPPER.writeValueAsString(updatePostRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        var updatedPostEntity = postRepository.findAll().getFirst();
        assertThat(updatedPostEntity)
                .usingRecursiveComparison()
                .ignoringFields(
                        "createdAt",
                        "brandAuthor",
                        "feeds",
                        "id",
                        "userAuthor",
                        "updatedAt",
                        "publicationTime",
                        "posted")
                .isEqualTo(updatePostRequest);

        assertEquals(
                Instant.ofEpochMilli(updatePostRequest.getPublicationTime().getValue()),
                updatedPostEntity.getPublicationTime());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    @Transactional
    void whenCreatePostWithViewConditions_AndAuthorIsUserType_ThenPostCreated_AndExpectedFeedsUpdated(
            @Random String username) {
        // given
        var createPostRequest = generateCreatePostRequest();
        var author = userRepository.save(generateUserEntity());

        var usersToReceivePost =
                Set.of(generateUserEntity(), generateUserEntity(), generateUserEntity(), generateUserEntity());
        usersToReceivePost.forEach(follower -> {
            var savedFeedEntity = feedRepository.save(generateFeedEntity());
            follower.setFeed(savedFeedEntity);
            follower.setUsername(username);
            follower.setSubscribedUsers(List.of(author));
        });
        userRepository.saveAll(usersToReceivePost);

        var usersToSkipPost = Set.of(
                generateUserEntity().toBuilder()
                        .subscribedUsers(List.of(author))
                        .build(),
                generateUserEntity().toBuilder().username(username).build(),
                generateUserEntity(),
                generateUserEntity());
        usersToSkipPost.forEach(follower -> {
            var savedFeedEntity = feedRepository.save(generateFeedEntity());
            follower.setFeed(savedFeedEntity);
            follower.setSubscribedUsers(List.of(author));
        });
        userRepository.saveAll(usersToSkipPost);

        createPostRequest.setViewConditions(List.of(PostViewCondition.builder()
                .fieldName("username")
                .operator(SearchOperator.EQUALS)
                .fieldValue(username)
                .build()));
        createPostRequest.setAuthorType(USER);
        createPostRequest.setAuthorId(author.getId());

        // when
        mockMvc.perform(post(POSTS_ENTITY_URL)
                        .content(OBJECT_MAPPER.writeValueAsString(createPostRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        var savedPostEntity = postRepository.findAll().getFirst();
        var actualUsers = userRepository.findAll();

        var actualUsersToReceivePost = actualUsers.stream()
                .filter(actualUser -> actualUser.getUsername().equals(username));
        actualUsersToReceivePost.forEach(actualUser ->
                assertEquals(savedPostEntity, actualUser.getFeed().getPosts().getFirst()));

        var actualUsersToSkipPost = actualUsers.stream()
                .filter(actualUser -> !actualUser.getUsername().equals(username)
                        && !actualUser.getId().equals(author.getId()))
                .toList();
        actualUsersToSkipPost.forEach(
                actualUser -> assertTrue(actualUser.getFeed().getPosts().isEmpty()));

        var actualAuthor = actualUsers.stream()
                .filter(actualUser -> actualUser.getId().equals(author.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(savedPostEntity, actualAuthor.getPosts().getFirst());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    @Transactional
    void
            whenUpdatePostWithViewConditions_AndAuthorIsUserType_AndViewConditionAreNotChanged_ThenPostUpdated_AndExpectedFeedsUpdated() {
        // given
        var updatePostRequest = generateUpdatePostRequest();
        updatePostRequest.setVisible(true);
        var author = userRepository.save(generateUserEntity());
        var initialPostEntity = postRepository.save(generatePostEntity().toBuilder()
                .userAuthor(author)
                .authorType(USER)
                .build());

        var usersToReceivePost =
                Set.of(generateUserEntity(), generateUserEntity(), generateUserEntity(), generateUserEntity());
        usersToReceivePost.forEach(follower -> {
            var savedFeedEntity = feedRepository.save(generateFeedEntity().toBuilder()
                    .posts(List.of(initialPostEntity))
                    .build());
            follower.setFeed(savedFeedEntity);
            follower.setSubscribedUsers(List.of(author));
        });
        userRepository.saveAll(usersToReceivePost);

        var usersToSkipPost =
                Set.of(generateUserEntity(), generateUserEntity(), generateUserEntity(), generateUserEntity());

        usersToSkipPost.forEach(follower -> {
            var savedFeedEntity = feedRepository.save(generateFeedEntity());
            follower.setFeed(savedFeedEntity);
            follower.setSubscribedUsers(List.of());
        });
        userRepository.saveAll(usersToSkipPost);

        updatePostRequest.setAuthorType(USER);
        updatePostRequest.setAuthorId(author.getId());

        // when
        mockMvc.perform(put("%s/%s".formatted(POSTS_ENTITY_URL, initialPostEntity.getId()))
                        .content(OBJECT_MAPPER.writeValueAsString(updatePostRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        var savedPostEntity = postRepository.findAll().getFirst();
        var actualUsers = userRepository.findAll();

        var usersToReceivePostsUsernames =
                usersToReceivePost.stream().map(UserEntity::getUsername).toList();

        var actualUsersToReceivePost = actualUsers.stream()
                .filter(actualUser -> usersToReceivePostsUsernames.contains(actualUser.getUsername()));
        actualUsersToReceivePost.forEach(actualUser ->
                assertEquals(savedPostEntity, actualUser.getFeed().getPosts().getFirst()));

        var actualUsersToSkipPost = actualUsers.stream()
                .filter(actualUser -> !usersToReceivePostsUsernames.contains(actualUser.getUsername())
                        && !actualUser.getId().equals(author.getId()))
                .toList();
        actualUsersToSkipPost.forEach(
                actualUser -> assertTrue(actualUser.getFeed().getPosts().isEmpty()));

        var actualAuthor = actualUsers.stream()
                .filter(actualUser -> actualUser.getId().equals(author.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(actualAuthor, savedPostEntity.getUserAuthor());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    @Transactional
    void
            whenUpdatePostWithViewConditions_AndAuthorIsUserType_AndViewConditionsAreChanged_ThenPostUpdated_AndExpectedFeedsUpdated(
                    @Random String username) {
        // given
        var updatePostRequest = generateUpdatePostRequest();
        var author = userRepository.save(generateUserEntity());
        var initialPostEntity = postRepository.save(generatePostEntity().toBuilder()
                .userAuthor(author)
                .authorType(USER)
                .viewConditions(List.of(ViewConditionInfo.builder()
                        .fieldName("username")
                        .operator(getRandomEnumValue(SearchOperator.class))
                        .fieldValue(getRandomString())
                        .build()))
                .build());

        var initialUsersToReceivePost =
                Set.of(generateUserEntity(), generateUserEntity(), generateUserEntity(), generateUserEntity());
        initialUsersToReceivePost.forEach(follower -> {
            var savedFeedEntity = feedRepository.save(generateFeedEntity().toBuilder()
                    .posts(List.of(initialPostEntity))
                    .build());
            follower.setFeed(savedFeedEntity);
            follower.setSubscribedUsers(List.of(author));
        });
        userRepository.saveAll(initialUsersToReceivePost);

        var usersToSkipPost =
                Set.of(generateUserEntity(), generateUserEntity(), generateUserEntity(), generateUserEntity());

        usersToSkipPost.forEach(follower -> {
            var savedFeedEntity = feedRepository.save(generateFeedEntity());
            follower.setFeed(savedFeedEntity);
            follower.setSubscribedUsers(List.of());
        });
        userRepository.saveAll(usersToSkipPost);

        var newUsersToReceivePost =
                Set.of(generateUserEntity(), generateUserEntity(), generateUserEntity(), generateUserEntity());
        newUsersToReceivePost.forEach(follower -> {
            var savedFeedEntity = feedRepository.save(generateFeedEntity());
            follower.setUsername(username);
            follower.setFeed(savedFeedEntity);
            follower.setSubscribedUsers(List.of(author));
        });
        var savedNewUsersToReceivePost = userRepository.saveAll(newUsersToReceivePost);
        feedRepository.saveAll(savedNewUsersToReceivePost.stream()
                .map(userEntity -> {
                    var feedEntity = userEntity.getFeed();
                    feedEntity.setUser(userEntity);
                    return feedEntity;
                })
                .collect(Collectors.toList()));

        updatePostRequest.setViewConditions(List.of(PostViewCondition.builder()
                .fieldName("username")
                .operator(SearchOperator.EQUALS)
                .fieldValue(username)
                .build()));
        updatePostRequest.setAuthorType(USER);
        updatePostRequest.setAuthorId(author.getId());

        // when
        mockMvc.perform(put("%s/%s".formatted(POSTS_ENTITY_URL, initialPostEntity.getId()))
                        .content(OBJECT_MAPPER.writeValueAsString(updatePostRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        var savedPostEntity = postRepository.findAll().getFirst();
        var actualUsersReceivedPostsIds = savedPostEntity.getFeeds().stream()
                .map(FeedEntity::getUser)
                .map(UserEntity::getId)
                .toList();
        assertEquals(savedNewUsersToReceivePost.stream().map(UserEntity::getId).toList(), actualUsersReceivedPostsIds);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    @Transactional
    void whenCreatePostWithViewConditions_AndAuthorIsBrandType_ThenPostCreated_AndExpectedFeedsUpdated(
            @Random String username) {
        // given
        var createPostRequest = generateCreatePostRequest();

        var author = brandRepository.save(generateBrandEntity());

        var usersToReceivePost =
                Set.of(generateUserEntity(), generateUserEntity(), generateUserEntity(), generateUserEntity());
        usersToReceivePost.forEach(follower -> {
            var savedFeedEntity = feedRepository.save(generateFeedEntity());
            follower.setFeed(savedFeedEntity);
            follower.setUsername(username);
            follower.setSubscribedBrands(List.of(author));
        });
        userRepository.saveAll(usersToReceivePost);

        var usersToSkipPost = Set.of(
                generateUserEntity().toBuilder()
                        .subscribedBrands(List.of(author))
                        .build(),
                generateUserEntity().toBuilder().username(username).build(),
                generateUserEntity(),
                generateUserEntity());
        usersToSkipPost.forEach(follower -> {
            var savedFeedEntity = feedRepository.save(generateFeedEntity());
            follower.setFeed(savedFeedEntity);
            follower.setSubscribedBrands(List.of(author));
        });
        userRepository.saveAll(usersToSkipPost);

        createPostRequest.setViewConditions(List.of(PostViewCondition.builder()
                .fieldName("username")
                .operator(SearchOperator.EQUALS)
                .fieldValue(username)
                .build()));
        createPostRequest.setAuthorType(BRAND);
        createPostRequest.setAuthorId(author.getId());

        // when
        mockMvc.perform(post(POSTS_ENTITY_URL)
                        .content(OBJECT_MAPPER.writeValueAsString(createPostRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        var savedPostEntity = postRepository.findAll().getFirst();
        var actualUsers = userRepository.findAll();

        var actualUsersToReceivePost = actualUsers.stream()
                .filter(actualUser -> actualUser.getUsername().equals(username));
        actualUsersToReceivePost.forEach(actualUser ->
                assertEquals(savedPostEntity, actualUser.getFeed().getPosts().getFirst()));

        var actualUsersToSkipPost = actualUsers.stream()
                .filter(actualUser -> !actualUser.getUsername().equals(username)
                        && !actualUser.getId().equals(author.getId()))
                .toList();
        actualUsersToSkipPost.forEach(
                actualUser -> assertTrue(actualUser.getFeed().getPosts().isEmpty()));

        var actualAuthor = brandRepository.findAll().getFirst();
        assertEquals(savedPostEntity, actualAuthor.getPosts().getFirst());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    @Transactional
    void
            whenUpdatePostWithViewConditions_AndAuthorIsBrandType_AndViewConditionAreNotChanged_ThenPostUpdated_AndExpectedFeedsUpdated() {
        // given
        var updatePostRequest = generateUpdatePostRequest();
        var author = brandRepository.save(generateBrandEntity());
        var initialPostEntity = postRepository.save(generatePostEntity().toBuilder()
                .brandAuthor(author)
                .authorType(BRAND)
                .build());

        var usersToReceivePost =
                Set.of(generateUserEntity(), generateUserEntity(), generateUserEntity(), generateUserEntity());
        usersToReceivePost.forEach(follower -> {
            var savedFeedEntity = feedRepository.save(generateFeedEntity().toBuilder()
                    .posts(List.of(initialPostEntity))
                    .build());
            follower.setFeed(savedFeedEntity);
            follower.setSubscribedBrands(List.of(author));
        });
        userRepository.saveAll(usersToReceivePost);

        var usersToSkipPost =
                Set.of(generateUserEntity(), generateUserEntity(), generateUserEntity(), generateUserEntity());

        usersToSkipPost.forEach(follower -> {
            var savedFeedEntity = feedRepository.save(generateFeedEntity());
            follower.setFeed(savedFeedEntity);
            follower.setSubscribedBrands(List.of());
        });
        userRepository.saveAll(usersToSkipPost);

        updatePostRequest.setAuthorType(BRAND);
        updatePostRequest.setAuthorId(author.getId());

        // when
        mockMvc.perform(put("%s/%s".formatted(POSTS_ENTITY_URL, initialPostEntity.getId()))
                        .content(OBJECT_MAPPER.writeValueAsString(updatePostRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        var savedPostEntity = postRepository.findAll().getFirst();
        var actualUsers = userRepository.findAll();
        var actualBrands = brandRepository.findAll();

        var usersToReceivePostsUsernames =
                usersToReceivePost.stream().map(UserEntity::getUsername).toList();

        var actualUsersToReceivePost = actualUsers.stream()
                .filter(actualUser -> usersToReceivePostsUsernames.contains(actualUser.getUsername()));
        actualUsersToReceivePost.forEach(actualUser ->
                assertEquals(savedPostEntity, actualUser.getFeed().getPosts().getFirst()));

        var actualUsersToSkipPost = actualUsers.stream()
                .filter(actualUser -> !usersToReceivePostsUsernames.contains(actualUser.getUsername())
                        && !actualUser.getId().equals(author.getId()))
                .toList();
        actualUsersToSkipPost.forEach(
                actualUser -> assertTrue(actualUser.getFeed().getPosts().isEmpty()));

        var actualAuthor = actualBrands.stream()
                .filter(actualBrand -> actualBrand.getId().equals(author.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(actualAuthor, savedPostEntity.getBrandAuthor());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    @Transactional
    void
            whenUpdatePostWithViewConditions_AndAuthorIsBrandType_AndViewConditionsAreChanged_ThenPostUpdated_AndExpectedFeedsUpdated(
                    @Random String username) {
        // given
        var updatePostRequest = generateUpdatePostRequest();
        var author = brandRepository.save(generateBrandEntity());
        var initialPostEntity = postRepository.save(generatePostEntity().toBuilder()
                .brandAuthor(author)
                .authorType(BRAND)
                .viewConditions(List.of(ViewConditionInfo.builder()
                        .fieldName(getRandomString())
                        .operator(getRandomEnumValue(SearchOperator.class))
                        .fieldValue(getRandomString())
                        .build()))
                .build());

        var initialUsersToReceivePost =
                Set.of(generateUserEntity(), generateUserEntity(), generateUserEntity(), generateUserEntity());
        initialUsersToReceivePost.forEach(follower -> {
            var savedFeedEntity = feedRepository.save(generateFeedEntity().toBuilder()
                    .posts(List.of(initialPostEntity))
                    .build());
            follower.setFeed(savedFeedEntity);
            follower.setSubscribedBrands(List.of(author));
        });
        userRepository.saveAll(initialUsersToReceivePost);

        var usersToSkipPost =
                Set.of(generateUserEntity(), generateUserEntity(), generateUserEntity(), generateUserEntity());

        usersToSkipPost.forEach(follower -> {
            var savedFeedEntity = feedRepository.save(generateFeedEntity());
            follower.setFeed(savedFeedEntity);
            follower.setSubscribedUsers(List.of());
        });
        userRepository.saveAll(usersToSkipPost);

        var newUsersToReceivePost =
                Set.of(generateUserEntity(), generateUserEntity(), generateUserEntity(), generateUserEntity());
        newUsersToReceivePost.forEach(follower -> {
            var savedFeedEntity = feedRepository.save(generateFeedEntity());
            follower.setUsername(username);
            follower.setFeed(savedFeedEntity);
            follower.setSubscribedBrands(List.of(author));
        });
        var savedNewUsersToReceivePost = userRepository.saveAll(newUsersToReceivePost);
        feedRepository.saveAll(savedNewUsersToReceivePost.stream()
                .map(userEntity -> {
                    var feedEntity = userEntity.getFeed();
                    feedEntity.setUser(userEntity);
                    return feedEntity;
                })
                .collect(Collectors.toList()));

        updatePostRequest.setViewConditions(List.of(PostViewCondition.builder()
                .fieldName("username")
                .operator(SearchOperator.EQUALS)
                .fieldValue(username)
                .build()));
        updatePostRequest.setAuthorType(USER);
        updatePostRequest.setAuthorId(author.getId());

        // when
        mockMvc.perform(put("%s/%s".formatted(POSTS_ENTITY_URL, initialPostEntity.getId()))
                        .content(OBJECT_MAPPER.writeValueAsString(updatePostRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        var savedPostEntity = postRepository.findAll().getFirst();
        var actualUsersReceivedPostsIds = savedPostEntity.getFeeds().stream()
                .map(FeedEntity::getUser)
                .map(UserEntity::getId)
                .toList();
        assertEquals(savedNewUsersToReceivePost.stream().map(UserEntity::getId).toList(), actualUsersReceivedPostsIds);
    }
}
