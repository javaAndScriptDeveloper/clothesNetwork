package com.example.company.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.example.company.dto.request.CreatePostRequest;
import com.example.company.mapper.dto.PostDtoMapper;
import com.example.company.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "posts")
@RequiredArgsConstructor
@RequestMapping("/api/${api.version}/posts")
public class PostController {

    private final PostDtoMapper postDtoMapper;
    private final PostService postService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    void create(@RequestBody CreatePostRequest createPostRequest) {
        var postModel = postDtoMapper.toModel(createPostRequest);
        postService.save(postModel);
    }
}
