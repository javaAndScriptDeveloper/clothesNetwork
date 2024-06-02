package com.example.company.mapper.dto;

import com.example.company.dto.request.CreateUserRequest;
import com.example.company.dto.request.UpdateUserRequest;
import com.example.company.dto.response.UserResponse;
import com.example.company.model.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserDtoMapper {

    User toModel(CreateUserRequest createUserRequest);

    User toModel(UpdateUserRequest updateUserRequest);

    UserResponse toDto(User model);
}
