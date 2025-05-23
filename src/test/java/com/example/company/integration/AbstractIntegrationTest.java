package com.example.company.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.example.company.base.AbstractTest;
import com.example.company.dto.request.CreatePostRequest;
import com.example.company.dto.request.UpdatePostRequest;
import com.example.company.entity.BrandEntity;
import com.example.company.entity.FeedEntity;
import com.example.company.entity.PostEntity;
import com.example.company.entity.UserEntity;
import com.example.company.repository.*;
import com.example.company.service.TransactionService;
import com.example.company.utils.StringUtils;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RandomBeansExtension.class)
@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AbstractIntegrationTest extends AbstractTest {

    private static final String BASIC_AUTH_SCHEME_NAME = "Basic";
    protected static final String BASE_URl = "/api/v1";

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

    @Autowired
    protected TransactionService transactionService;

    protected UserEntity generateUserEntity() {
        var userEntity = random.nextObject(
                UserEntity.class,
                "managedBrand",
                "feed",
                "affiliatedBrands",
                "subscribedBrands",
                "subscribedUsers",
                "followers",
                "profileImages",
                "posts");
        userEntity.setAffiliatedBrands(Collections.emptyList());
        userEntity.setSubscribedBrands(Collections.emptyList());
        userEntity.setSubscribedUsers(Collections.emptyList());
        userEntity.setFollowers(Collections.emptyList());
        userEntity.setProfileImages(Collections.emptyList());
        userEntity.setPosts(Collections.emptyList());
        return userEntity;
    }

    protected FeedEntity generateFeedEntity() {
        var feedEntity = random.nextObject(FeedEntity.class, "user", "posts");
        feedEntity.setPosts(Collections.emptyList());
        return feedEntity;
    }

    protected BrandEntity generateBrandEntity() {
        var brandEntity = random.nextObject(
                BrandEntity.class, "followers", "affiliatedUsers", "managingUsers", "posts", "profileImages");
        brandEntity.setEnabled(true);
        brandEntity.setAffiliatedUsers(Collections.emptyList());
        brandEntity.setFollowers(Collections.emptyList());
        brandEntity.setManagingUsers(Collections.emptyList());
        brandEntity.setPosts(Collections.emptyList());
        return brandEntity;
    }

    protected PostEntity generatePostEntity() {
        var postEntity = random.nextObject(PostEntity.class);
        postEntity.setUserAuthor(null);
        postEntity.setBrandAuthor(null);
        postEntity.setFeeds(List.of());
        postEntity.setViewConditions(List.of());
        postEntity.setAuthorType(null);
        postEntity.setVisible(true);
        postEntity.setPosted(true);
        return postEntity;
    }

    protected CreatePostRequest generateCreatePostRequest() {
        var createPostRequest = random.nextObject(CreatePostRequest.class, "viewConditions");
        createPostRequest.setVisible(true);
        createPostRequest.getPublicationTime().setValue(Instant.now().toEpochMilli());
        return createPostRequest;
    }

    protected UpdatePostRequest generateUpdatePostRequest() {
        var updatePostRequest = random.nextObject(UpdatePostRequest.class, "viewConditions");
        updatePostRequest.setVisible(true);
        updatePostRequest.getPublicationTime().setValue(Instant.now().toEpochMilli());
        return updatePostRequest;
    }

    protected String encodeCredentials(String username, String password) {
        return BASIC_AUTH_SCHEME_NAME + " " + StringUtils.encodeBase64("%s:%s".formatted(username, password));
    }

    @SneakyThrows
    protected <T> ResultActions executePostApiCall(String path, T requestBody) {
        return mockMvc.perform(post(path)
                        .content(StringUtils.toJson(requestBody))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());
    }
}
