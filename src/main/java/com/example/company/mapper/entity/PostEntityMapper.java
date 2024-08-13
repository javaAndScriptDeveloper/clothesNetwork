package com.example.company.mapper.entity;

import com.example.company.entity.PostEntity;
import com.example.company.model.post.Post;
import com.example.company.model.post.PostPublicationTime;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface PostEntityMapper {

    List<Post> toModel(List<PostEntity> entities);

    @Mapping(target = "publicationTime", source = "publicationTime", qualifiedByName = "mapPublicationTime")
    Post toModel(PostEntity entity);

    @Named("mapPublicationTime")
    default PostPublicationTime mapPublicationTime(Instant publicationTime) {
        return PostPublicationTime.builder()
                .value(publicationTime.toEpochMilli())
                .build();
    }
}
