package com.example.company.dto.request;

import com.example.company.model.Image;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpdateUserRequest {

    String username;

    String phoneNumber;

    String email;

    List<Image> profileImages;
}
