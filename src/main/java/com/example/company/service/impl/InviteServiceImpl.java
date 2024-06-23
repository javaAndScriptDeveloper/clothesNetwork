package com.example.company.service.impl;

import com.example.company.entity.InviteEntity;
import com.example.company.exception.BrandNotFoundException;
import com.example.company.exception.InviteNotFoundException;
import com.example.company.model.Invite;
import com.example.company.repository.BrandRepository;
import com.example.company.repository.InviteRepository;
import com.example.company.service.InviteService;
import com.example.company.service.SecurityService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {

    private final SecurityService securityService;
    private final InviteRepository inviteRepository;
    private final BrandRepository brandRepository;

    @Value("${api.public-service-url}")
    private String publicServiceUrl;

    @Value("${api.version}")
    private String apiVersion;

    @Override
    @Transactional
    public List<String> generateInviteUrls(Integer size) {

        var brandId = securityService.getCurrentUser().getManagedBrandId();
        var brandEntity = brandRepository.findById(brandId).orElseThrow(() -> new BrandNotFoundException(brandId));

        var inviteUrls = new ArrayList<String>();
        var inviteEntities = new ArrayList<InviteEntity>();
        for (int i = 0; i < size; i++) {
            var secretCode = UUID.randomUUID();
            var inviteUrl = generateInviteUrl(secretCode.toString());
            inviteUrls.add(inviteUrl);
            var inviteEntity = InviteEntity.builder()
                    .id(secretCode)
                    .url(inviteUrl)
                    .brand(brandEntity)
                    .used(false)
                    .build();
            inviteEntities.add(inviteEntity);
        }

        inviteRepository.saveAll(inviteEntities);

        return inviteUrls;
    }

    @Override
    @Transactional
    public boolean checkAndUseInvite(UUID inviteId) {
        var inviteEntityOptional = inviteRepository.findById(inviteId);
        if (inviteEntityOptional.isEmpty()) {
            return false;
        }
        var inviteEntity = inviteEntityOptional.get();
        if (inviteEntity.getUsed()) {
            return false;
        }
        inviteEntity.setUsed(true);
        inviteRepository.save(inviteEntity);
        return true;
    }

    @Override
    public Invite findByIdOrThrowNotFound(UUID id) {
        var inviteEntity = inviteRepository.findById(id).orElseThrow(() -> new InviteNotFoundException(id));
        return Invite.builder().brandId(inviteEntity.getBrand().getId()).build();
    }

    private String generateInviteUrl(String secretCode) {
        return "%s/api/%s/users?secretCode=%s".formatted(publicServiceUrl, apiVersion, secretCode);
    }
}
