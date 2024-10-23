package com.example.company.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.example.company.dto.request.AddBrandRequest;
import com.example.company.dto.request.CreateUserRequest;
import com.example.company.dto.request.SubscribeToUserRequest;
import com.example.company.dto.request.UpdateUserRequest;
import com.example.company.dto.response.UserResponse;
import com.example.company.mapper.dto.ImageDtoMapper;
import com.example.company.mapper.dto.UserDtoMapper;
import com.example.company.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "users")
@RequiredArgsConstructor
@RequestMapping("/api/${api.version}/users")
public class UserController {

    private final UserDtoMapper userDtoMapper;
    private final ImageDtoMapper imageDtoMapper;
    private final UserService userService;

    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/{id}")
    UserResponse getUser(@PathVariable Long id) {
        var userModel = userService.getById(id);
        return userDtoMapper.toDto(userModel);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(
            @RequestBody @Valid CreateUserRequest createUserRequest, @RequestParam UUID secretCode) {
        var userModel = userDtoMapper.toModel(createUserRequest);
        userModel.setProfileImages(imageDtoMapper.toModel(createUserRequest.getProfileImages()));
        var isSuccessful = userService.create(userModel, secretCode);
        return ResponseEntity.status(isSuccessful ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY)
                .build();
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE, path = "/{id}")
    void updateUser(@RequestBody UpdateUserRequest updateUserRequest, @PathVariable Long id) {
        var userModel = userDtoMapper.toModel(updateUserRequest);
        userModel.setId(id);
        userModel.setProfileImages(imageDtoMapper.toModel(updateUserRequest.getProfileImages()));
        userService.update(userModel);
    }

    @PutMapping(path = "/{id}/add-brand", consumes = APPLICATION_JSON_VALUE)
    void addBrand(@RequestBody AddBrandRequest request, @PathVariable Long id) {
        userService.addBrand(id, request.getBrandId());
    }

    @PostMapping(path = "/{subscriberId}/subscribe", consumes = APPLICATION_JSON_VALUE)
    void subscribe(@PathVariable Long subscriberId, @RequestBody SubscribeToUserRequest request) {
        userService.subscribe(subscriberId, request.getUserId());
    }
}
