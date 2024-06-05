package com.example.company.mapper;

import com.example.company.entity.UserEntity;
import com.example.company.model.User;
import com.example.company.model.UserDetailsEnhanced;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserDetailsMapper {

    @Mapping(target = "authorities", source = "permissions")
    UserDetailsEnhanced toUserDetails(UserEntity userEntity);

    User toModel(UserDetailsEnhanced userDetailsEnhanced);
}
