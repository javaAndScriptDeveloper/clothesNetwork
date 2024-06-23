package com.example.company.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    Long id;

    String username;

    String password;

    String phoneNumber;

    String email;

    List<Image> profileImages;

    List<Long> subscribedBrandIds;

    List<Long> affiliatedBrandIds;

    Long managedBrandId;
}
