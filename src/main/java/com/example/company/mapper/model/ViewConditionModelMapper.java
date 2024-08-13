package com.example.company.mapper.model;

import com.example.company.entity.info.ViewConditionInfo;
import com.example.company.model.post.Post;
import com.example.company.model.search.SearchRequest;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface ViewConditionModelMapper {

    List<SearchRequest.FilteringField> toModel(List<ViewConditionInfo> viewConditions);

    List<ViewConditionInfo> toEntity(List<Post.ViewCondition> viewConditions);
}
