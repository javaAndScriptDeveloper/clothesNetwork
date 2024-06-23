package com.example.company.mapper.entity;

import com.example.company.entity.PostEntity;
import com.example.company.model.Post;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface PostEntityMapper {

    List<Post> toModel(List<PostEntity> entities);
}
