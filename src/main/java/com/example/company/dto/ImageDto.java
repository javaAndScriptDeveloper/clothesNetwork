package com.example.company.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImageDto {

    @NotNull(message = "Id can not be null") Long id;

    @NotNull(message = "Data can not be null") byte[] data;
}
