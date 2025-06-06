package com.example.company.dto.response;

import com.example.company.model.Image;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    String username;

    String phoneNumber;

    String email;

    List<Image> profileImages;
}
