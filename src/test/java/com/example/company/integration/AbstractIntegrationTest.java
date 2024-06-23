package com.example.company.integration;

import com.example.company.entity.BrandEntity;
import com.example.company.entity.UserEntity;
import com.example.company.repository.*;
import com.example.company.utils.StringUtils;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import java.util.Collections;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RandomBeansExtension.class)
@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AbstractIntegrationTest {

    private static final String BASIC_AUTH_SCHEME_NAME = "Basic";
    protected static final String BASE_URl = "/api/v1";

    protected static final EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandom();

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected BrandRepository brandRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected InviteRepository inviteRepository;

    @Autowired
    protected FeedRepository feedRepository;

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected ImageRepository imageRepository;

    protected UserEntity generateUserEntity() {
        var userEntity = random.nextObject(
                UserEntity.class,
                "managedBrand",
                "feed",
                "affiliatedBrands",
                "subscribedBrands",
                "followers",
                "profileImages");
        userEntity.setAffiliatedBrands(Collections.emptyList());
        userEntity.setSubscribedBrands(Collections.emptyList());
        userEntity.setFollowers(Collections.emptyList());
        userEntity.setProfileImages(Collections.emptyList());
        return userEntity;
    }

    protected BrandEntity generateBrandEntity() {
        var brandEntity = random.nextObject(BrandEntity.class, "followers", "affiliatedUsers", "managingUsers");
        brandEntity.setEnabled(true);
        brandEntity.setAffiliatedUsers(Collections.emptyList());
        brandEntity.setFollowers(Collections.emptyList());
        brandEntity.setManagingUsers(Collections.emptyList());
        return brandEntity;
    }

    protected String encodeCredentials(String username, String password) {
        return BASIC_AUTH_SCHEME_NAME + " " + StringUtils.encodeBase64("%s:%s".formatted(username, password));
    }
}
