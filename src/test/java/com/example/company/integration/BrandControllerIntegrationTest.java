package com.example.company.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.company.dto.request.CreateBrandRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

public class BrandControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String BRANDS_URL = BASE_URl + "/brands";

    @Test
    @SneakyThrows
    @WithMockUser
    @Transactional
    public void whenCreateBrand_ThenBrandIsCreated() {
        // given
        var request = random.nextObject(CreateBrandRequest.class);
        var author = userRepository.save(generateUserEntity());
        request.setAuthorId(author.getId());

        // when
        var result = executePostApiCall(BRANDS_URL, request);

        // then
        result.andExpect(status().isOk());

        var createdBrand = brandRepository.findAll().getFirst();
        assertThat(createdBrand)
                .usingRecursiveComparison()
                .ignoringFields("id", "affiliatedUsers", "followers", "managingUsers", "posts", "profileImages")
                .isEqualTo(request);
        assertThat(createdBrand.getProfileImages())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("user", "brand")
                .isEqualTo(request.getProfileImages());
        assertTrue(createdBrand.getManagingUsers().contains(author));

        var updatedUser = userRepository.findByIdOrThrowNotFound(author.getId());
        assertEquals(createdBrand, updatedUser.getManagedBrand());
    }
}
