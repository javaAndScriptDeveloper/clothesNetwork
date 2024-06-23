package com.example.company.service.impl;

import com.example.company.exception.ApplicationException;
import com.example.company.exception.FeedNotFoundException;
import com.example.company.mapper.entity.PostEntityMapper;
import com.example.company.model.Post;
import com.example.company.repository.FeedRepository;
import com.example.company.repository.PostRepository;
import com.example.company.service.FeedService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private static final String FEED_POSTS_SORTING_FIELD = "updatedAt";

    private final FeedRepository feedRepository;
    private final PostRepository postRepository;
    private final PostEntityMapper postEntityMapper;

    @Override
    public List<Post> searchFeed(Long userId, Integer startPosition, Integer step) {
        validateParams(startPosition, step);
        var feedId = feedRepository.findFeedIdByUserId(userId).orElseThrow(() -> new FeedNotFoundException(userId));
        var pageNumber = startPosition / step;
        var pageableRequest = PageRequest.of(pageNumber, step, Sort.by(FEED_POSTS_SORTING_FIELD));
        var postEntities = postRepository.findByFeedsId(feedId, pageableRequest).getContent();
        return postEntityMapper.toModel(postEntities);
    }

    private static void validateParams(Integer startPosition, Integer step) {
        if (startPosition % step != 0) {
            throw new ApplicationException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    ("Invalid feed parameters startPosition: %s, step: %s;"
                                    + "start position must be divided by step without remainder")
                            .formatted(startPosition, step));
        }
    }
}
