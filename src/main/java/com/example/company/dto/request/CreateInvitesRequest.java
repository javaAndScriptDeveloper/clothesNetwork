package com.example.company.dto.request;

import com.example.company.enums.InviteFormatType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateInvitesRequest {

    InviteFormatType inviteFormatType;
    Integer size;
}
