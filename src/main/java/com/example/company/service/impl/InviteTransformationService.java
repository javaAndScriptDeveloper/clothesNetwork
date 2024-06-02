package com.example.company.service.impl;

import java.util.List;

public interface InviteTransformationService {

    byte[] convertInviteUrlsToPdfWithQrCodes(List<String> inviteUrls);
}
