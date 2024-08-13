package com.example.company.integration;

import static com.example.company.config.ApplicationConfig.OBJECT_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.company.dto.ImageDto;
import com.example.company.dto.request.AddBrandRequest;
import com.example.company.dto.request.CreateUserRequest;
import com.example.company.dto.request.UpdateUserRequest;
import com.example.company.dto.response.UserResponse;
import com.example.company.entity.InviteEntity;
import io.github.glytching.junit.extension.random.Random;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

public class UserControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String USERS_ENTITY_URL = BASE_URl + "/users";

    @Test
    @WithMockUser
    @SneakyThrows
    void whenFindUserById_ThenReturnsUser() {
        // given
        var savedUserEntity = userRepository.save(generateUserEntity());

        // when
        var responseContent = mockMvc.perform(get("%s/%s".formatted(USERS_ENTITY_URL, savedUserEntity.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        assertThat(savedUserEntity)
                .usingRecursiveComparison()
                .ignoringFields(
                        "id",
                        "password",
                        "profileImages",
                        "feed",
                        "subscribedBrands",
                        "followers",
                        "permissions",
                        "managedBrand",
                        "affiliatedBrands",
                        "subscribedUsers",
                        "posts")
                .isEqualTo(OBJECT_MAPPER.readValue(responseContent, UserResponse.class));
    }

    @Test
    @SneakyThrows
    @Transactional
    void whenCreateUser_ThenUserCreated(@Random CreateUserRequest createUserRequest) {
        // given
        var savedBrandEntity = brandRepository.save(generateBrandEntity());
        var savedInviteEntity = inviteRepository.save(InviteEntity.builder()
                .id(UUID.randomUUID())
                .used(false)
                .url(random.nextObject(String.class))
                .brand(savedBrandEntity)
                .build());

        // when
        mockMvc.perform(post(USERS_ENTITY_URL)
                        .param("secretCode", savedInviteEntity.getId().toString())
                        .content(OBJECT_MAPPER.writeValueAsString(createUserRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        var savedUserEntity = userRepository.findAll().getFirst();
        assertEquals(createUserRequest.getUsername(), savedUserEntity.getUsername());
        assertEquals(createUserRequest.getEmail(), savedUserEntity.getEmail());
        assertEquals(createUserRequest.getPhoneNumber(), savedUserEntity.getPhoneNumber());
        assertEquals(
                createUserRequest.getProfileImages().size(),
                savedUserEntity.getProfileImages().size());
        assertArrayEquals(
                createUserRequest.getProfileImages().getFirst().getData(),
                savedUserEntity.getProfileImages().getFirst().getData());
        assertNotNull(savedUserEntity.getFeed());
        assertEquals(1, savedUserEntity.getAffiliatedBrands().size());
        assertEquals(1, savedUserEntity.getSubscribedBrands().size());

        var savedFeedEntity = feedRepository.findFeedIdByUserId(savedUserEntity.getId());
        assertNotNull(savedFeedEntity);

        var updatedInviteEntity =
                inviteRepository.findById(savedInviteEntity.getId()).orElseThrow();
        assertTrue(updatedInviteEntity.getUsed());

        var updatedBrandEntity = brandRepository.findByIdOrThrowNotFound(savedBrandEntity.getId());
        assertTrue(updatedBrandEntity.getAffiliatedUsers().contains(savedUserEntity));
        assertTrue(updatedBrandEntity.getFollowers().contains(savedUserEntity));
    }

    @Test
    @SneakyThrows
    @WithMockUser
    void whenUpdateUser_ThenUserUpdated() {
        // given
        var savedUserEntity = userRepository.save(generateUserEntity());
        var updateUserRequest = random.nextObject(UpdateUserRequest.class);
        updateUserRequest.setProfileImages(List.of(random.nextObject(ImageDto.class)));

        // when
        mockMvc.perform(put("%s/%s".formatted(USERS_ENTITY_URL, savedUserEntity.getId()))
                        .content(OBJECT_MAPPER.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        var updatedUserEntity = userRepository.findByIdOrThrowNotFound(savedUserEntity.getId());
        assertThat(updatedUserEntity)
                .usingRecursiveComparison()
                .ignoringFields(
                        "id",
                        "profileImages",
                        "feed",
                        "subscribedBrands",
                        "followers",
                        "permissions",
                        "managedBrand",
                        "affiliatedBrands",
                        "subscribedUsers",
                        "posts")
                .isEqualTo(updateUserRequest);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    // @Transactional
    void whenAddBrandToUser_ThenBrandIsAddedToUser() {
        // given
        var savedUserEntity = userRepository.save(generateUserEntity());
        var savedBrandEntity = brandRepository.save(generateBrandEntity());

        // when
        mockMvc.perform(put("%s/%s/add-brand".formatted(USERS_ENTITY_URL, savedUserEntity.getId()))
                        .content(OBJECT_MAPPER.writeValueAsString(AddBrandRequest.builder()
                                .brandId(savedBrandEntity.getId())
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        var updatedUser = userRepository.findByIdOrThrowNotFound(savedUserEntity.getId());
        assertTrue(updatedUser.getAffiliatedBrands().contains(savedBrandEntity));
        assertTrue(updatedUser.getSubscribedBrands().contains(savedBrandEntity));
        /* TODO
        var updatedBrandEntity = brandRepository.findByIdOrThrowNotFound(savedBrandEntity.getId());
        assertTrue(updatedBrandEntity.getAffiliatedUsers().contains(updatedUser));
        assertTrue(updatedBrandEntity.getFollowers().contains(updatedUser));
         */
    }
}
