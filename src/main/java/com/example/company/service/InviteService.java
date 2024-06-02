package com.example.company.service;

import java.util.List;
import java.util.UUID;

public interface InviteService {

    List<String> generateInviteUrls(Integer size);

    boolean checkAndUseInvite(UUID inviteId);
}
