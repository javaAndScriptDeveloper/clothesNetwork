package com.example.company.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.example.company.dto.request.CreateUserRequest;
import com.example.company.dto.request.UpdateUserRequest;
import com.example.company.dto.response.UserResponse;
import com.example.company.mapper.dto.UserDtoMapper;
import com.example.company.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private final UserService userService;

    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/{id}")
    UserResponse findUser(@PathVariable Long id) {
        var userModel = userService.findById(id);
        return userDtoMapper.toDto(userModel);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest, @RequestParam UUID secretCode) {
        var userModel = userDtoMapper.toModel(createUserRequest);
        var isSuccessful = userService.create(userModel, secretCode);
        return ResponseEntity.status(isSuccessful ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY)
                .build();
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE)
    void updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        var userModel = userDtoMapper.toModel(updateUserRequest);
        userService.update(userModel);
    }
}
