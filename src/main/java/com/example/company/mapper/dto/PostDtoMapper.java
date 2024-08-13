package com.example.company.mapper.dto;

import com.example.company.dto.request.CreatePostRequest;
import com.example.company.dto.request.UpdatePostRequest;
import com.example.company.dto.response.PostResponse;
import com.example.company.model.post.Post;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface PostDtoMapper {

    Post toModel(CreatePostRequest dto);

    Post toModel(UpdatePostRequest dto);

    List<PostResponse> toDto(List<Post> models);
}
