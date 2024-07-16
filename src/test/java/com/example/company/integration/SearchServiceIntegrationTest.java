package com.example.company.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.company.entity.UserEntity;
import com.example.company.enums.SearchOperator;
import com.example.company.model.search.SearchRequest;
import com.example.company.service.SearchService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    SearchService sut;

    @Test
    void whenSearch_AndRequestIsUserEntity_ThenCorrectUserEntityReturned() {
        // given
        var expectedUserEntity = userRepository.save(generateUserEntity());

        userRepository.save(generateUserEntity());
        userRepository.save(generateUserEntity());
        userRepository.save(generateUserEntity());
        userRepository.save(generateUserEntity());

        // when
        var actualUserEntities = sut.search(SearchRequest.<UserEntity>builder()
                .clazz(UserEntity.class)
                .filteringFields(List.of(SearchRequest.FilteringField.builder()
                        .fieldName("username")
                        .operator(SearchOperator.EQUALS)
                        .fieldValue(expectedUserEntity.getUsername())
                        .build()))
                .build());

        // then
        assertEquals(1, actualUserEntities.size());
        assertEquals(expectedUserEntity, actualUserEntities.getFirst());
    }

    @Test
    void whenSearch_AndRequestIsUserEntity_AndChildEntitiesUsedInSearch_ThenCorrectUserEntityReturned() {
        // given
        var followerUserEntity = userRepository.save(generateUserEntity());
        var expectedUserEntity = userRepository.save(generateUserEntity().toBuilder()
                .followers(List.of(followerUserEntity))
                .build());

        userRepository.save(generateUserEntity().toBuilder()
                .followers(List.of(userRepository.save(generateUserEntity())))
                .build());
        userRepository.save(generateUserEntity().toBuilder()
                .followers(List.of(userRepository.save(generateUserEntity())))
                .build());
        userRepository.save(generateUserEntity().toBuilder()
                .followers(List.of(userRepository.save(generateUserEntity())))
                .build());
        userRepository.save(generateUserEntity().toBuilder()
                .followers(List.of(userRepository.save(generateUserEntity())))
                .build());
        // when
        var actualUserEntities = sut.search(SearchRequest.<UserEntity>builder()
                .clazz(UserEntity.class)
                .filteringFields(List.of(
                        SearchRequest.FilteringField.builder()
                                .fieldName("username")
                                .operator(SearchOperator.EQUALS)
                                .fieldValue(expectedUserEntity.getUsername())
                                .build(),
                        SearchRequest.FilteringField.builder()
                                .fieldName("followers.username")
                                .operator(SearchOperator.EQUALS)
                                .fieldValue(followerUserEntity.getUsername())
                                .build()))
                .build());

        // then
        assertEquals(1, actualUserEntities.size());
        assertEquals(expectedUserEntity, actualUserEntities.getFirst());
    }
}
