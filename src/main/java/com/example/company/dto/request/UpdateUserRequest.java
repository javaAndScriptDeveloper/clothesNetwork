package com.example.company.dto.request;

import com.example.company.dto.ImageDto;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {

    String username;

    String phoneNumber;

    String password;

    String email;

    List<ImageDto> profileImages;
}
