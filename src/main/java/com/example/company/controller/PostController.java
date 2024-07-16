package com.example.company.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.example.company.dto.request.CreatePostRequest;
import com.example.company.dto.request.UpdatePostRequest;
import com.example.company.mapper.dto.PostDtoMapper;
import com.example.company.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        postService.create(postModel);
    }

    @PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE)
    void update(@PathVariable UUID id, @RequestBody UpdatePostRequest updatePostRequest) {
        var postModel = postDtoMapper.toModel(updatePostRequest);
        postModel.setId(id);
        postService.update(postModel);
    }
}
