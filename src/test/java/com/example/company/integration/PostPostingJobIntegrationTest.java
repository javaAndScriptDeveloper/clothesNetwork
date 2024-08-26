package com.example.company.integration;

import static com.example.company.enums.AuthorType.USER;
import static org.junit.jupiter.api.Assertions.*;

import com.example.company.job.PostPostingJob;
import com.example.company.service.TransactionService;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PostPostingJobIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PostPostingJob sut;

    @Autowired
    private TransactionService transactionService;

    @Test
    void whenCallRun_ThenUnpublishedPostsArePublished() {
        // given
        var author = userRepository.save(generateUserEntity());
        var usersToReceivePost =
                Set.of(generateUserEntity(), generateUserEntity(), generateUserEntity(), generateUserEntity());
        usersToReceivePost.forEach(follower -> {
            var savedFeedEntity = feedRepository.save(generateFeedEntity());
            follower.setFeed(savedFeedEntity);
            follower.setSubscribedUsers(List.of(author));
        });
        userRepository.saveAll(usersToReceivePost);

        var postEntity = postRepository.save(generatePostEntity().toBuilder()
                .userAuthor(author)
                .authorType(USER)
                .viewConditions(List.of())
                .publicationTime(Instant.now().minusSeconds(getPositiveInteger()))
                .visible(false)
                .posted(false)
                .build());

        // when
        sut.run();

        // then
        transactionService.execute(() -> {
            var feeds = feedRepository.findAll();
            feeds.forEach(feed ->
                    assertEquals(postEntity.getId(), feed.getPosts().getFirst().getId()));
        });

        var updatedPost = postRepository.findAll().getFirst();
        assertTrue(updatedPost.getVisible());
        assertTrue(updatedPost.getPosted());
        assertNull(updatedPost.getPublicationTime());
    }
}
