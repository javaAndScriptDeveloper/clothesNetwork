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
public class CreateBrandRequest {

    @NotBlank(message = "Name can not be empty")
    String name;

    @NotNull(message = "Author id can not be null") Long authorId;

    @NotNull(message = "Profile images can not be null") List<@Valid ImageDto> profileImages;

    @NotNull(message = "Enabled can not be null") Boolean enabled;
}
