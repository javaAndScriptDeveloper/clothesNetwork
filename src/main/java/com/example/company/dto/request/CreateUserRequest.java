package com.example.company.dto.request;

import com.example.company.dto.ImageDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {

    @NotBlank(message = "Username can not be empty")
    String username;

    @NotBlank(message = "Phone number can not be empty")
    String phoneNumber;

    @NotBlank(message = "Password can not be empty")
    String password;

    @NotBlank(message = "Email can not be empty")
    String email;

    @NotNull(message = "Profile images can not be null") List<@Valid ImageDto> profileImages;
}
