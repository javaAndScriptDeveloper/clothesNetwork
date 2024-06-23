package com.example.company.service;

import com.example.company.model.Invite;
import java.util.List;
import java.util.UUID;

public interface InviteService {

    List<String> generateInviteUrls(Integer size);

    boolean checkAndUseInvite(UUID inviteId);

    Invite findByIdOrThrowNotFound(UUID inviteId);
}
