package com.example.company.dto.request;

import com.example.company.enums.InviteFormatType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateInvitesRequest {

    InviteFormatType inviteFormatType;
    Integer size;
}
