package com.example.company.mapper.entity;

import com.example.company.entity.UserEntity;
import com.example.company.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface UserEntityMapper {

    @Mapping(target = "profileImages", ignore = true)
    User toModel(UserEntity entity);

    @Mapping(target = "profileImages", ignore = true)
    UserEntity toEntity(User model);

    @Mapping(target = "profileImages", ignore = true)
    void updateEntity(@MappingTarget UserEntity entity, User model);
}
