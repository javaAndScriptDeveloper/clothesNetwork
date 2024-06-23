package com.example.company.controller;

import com.example.company.dto.request.SearchFeedRequest;
import com.example.company.dto.response.PostResponse;
import com.example.company.mapper.dto.PostDtoMapper;
import com.example.company.service.FeedService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "feeds")
@RequiredArgsConstructor
@RequestMapping("/api/${api.version}/feeds")
public class FeedController {

    private final FeedService feedService;
    private final PostDtoMapper postDtoMapper;

    @PostMapping
    List<PostResponse> searchFeed(@RequestBody SearchFeedRequest searchFeedRequest) {
        var foundPosts = feedService.searchFeed(
                searchFeedRequest.getUserId(), searchFeedRequest.getStartPosition(), searchFeedRequest.getStep());
        return postDtoMapper.toDto(foundPosts);
    }
}
