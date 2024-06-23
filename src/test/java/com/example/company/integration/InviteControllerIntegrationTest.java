package com.example.company.integration;

import static com.example.company.config.ApplicationConfig.OBJECT_MAPPER;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.company.dto.request.CreateInvitesRequest;
import com.example.company.enums.InviteFormatType;
import com.example.company.enums.Permission;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class InviteControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String INVITE_ENTITY_URLS = BASE_URl + "/invites";

    @Value("${api.public-service-url}")
    private String publicServiceUrl;

    @Test
    @SneakyThrows
    void whenCreateInvites_ThenReturnsInvites() {
        // given
        var savedBrandEntity = brandRepository.save(generateBrandEntity());
        var toSaveUserEntity = generateUserEntity();
        toSaveUserEntity.setPermissions(Set.of(Permission.BO_READ));
        toSaveUserEntity.setManagedBrand(savedBrandEntity);
        var savedUserEntity = userRepository.save(toSaveUserEntity);

        var inviteUrlsSize = random.nextInt(5, 10);

        var createInvitesRequest = CreateInvitesRequest.builder()
                .inviteFormatType(InviteFormatType.URL)
                .size(inviteUrlsSize)
                .build();

        // when
        var responseBody = mockMvc.perform(MockMvcRequestBuilders.post(INVITE_ENTITY_URLS)
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                encodeCredentials(savedUserEntity.getUsername(), savedUserEntity.getPassword()))
                        .content(OBJECT_MAPPER.writeValueAsString(createInvitesRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(inviteUrlsSize))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var commonInviteUrlPart = "%s/api/v1/users?secretCode=".formatted(publicServiceUrl);
        var createdInviteUrls = OBJECT_MAPPER.readValue(responseBody, new TypeReference<List<String>>() {});
        var createdInviteIds = new ArrayList<UUID>();
        createdInviteUrls.forEach(createdInviteUrl -> {
            assertTrue(createdInviteUrl.startsWith(commonInviteUrlPart));
            var inviteId = UUID.fromString(createdInviteUrl.replace(commonInviteUrlPart, ""));
            createdInviteIds.add(inviteId);
        });

        var inviteEntities = inviteRepository.findAll();
        assertEquals(inviteUrlsSize, inviteEntities.size());
        inviteEntities.forEach(inviteEntity -> {
            assertTrue(createdInviteIds.contains(inviteEntity.getId()));
            assertFalse(inviteEntity.getUsed());
            assertTrue(inviteEntity.getUrl().startsWith(commonInviteUrlPart));
            assertEquals(savedBrandEntity, inviteEntity.getBrand());
        });
    }
}
