package com.example.company.service.impl;

import com.example.company.entity.FeedEntity;
import com.example.company.mapper.entity.ImageEntityMapper;
import com.example.company.mapper.entity.UserEntityMapper;
import com.example.company.model.User;
import com.example.company.repository.BrandRepository;
import com.example.company.repository.UserRepository;
import com.example.company.service.InviteService;
import com.example.company.service.UserService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final InviteService inviteService;
    private final UserEntityMapper userEntityMapper;
    private final ImageEntityMapper imageEntityMapper;
    private final UserRepository userRepository;
    private final BrandRepository brandRepository;

    @Override
    public User findById(Long id) {
        return userEntityMapper.toModel(userRepository.findByIdOrThrowNotFound(id));
    }

    @Override
    @Transactional
    public boolean create(User userModel, UUID inviteId) {

        var isSuccessful = inviteService.checkAndUseInvite(inviteId);
        if (!isSuccessful) {
            return false;
        }

        var toSaveUserEntity = userEntityMapper.toEntity(userModel);

        var imageEntityList = imageEntityMapper.toEntity(userModel.getProfileImages());
        imageEntityList.forEach(imageEntity -> imageEntity.setUser(toSaveUserEntity));

        var invite = inviteService.findByIdOrThrowNotFound(inviteId);

        var brandEntity = brandRepository.findByIdOrThrowNotFound(invite.getBrandId());

        toSaveUserEntity.setSubscribedBrands(List.of(brandEntity));
        toSaveUserEntity.setAffiliatedBrands(List.of(brandEntity));
        toSaveUserEntity.setProfileImages(imageEntityList);
        toSaveUserEntity.setFeed(FeedEntity.builder().build());

        var brandAffiliatedUsers = new ArrayList<>(brandEntity.getAffiliatedUsers());
        brandAffiliatedUsers.add(toSaveUserEntity);
        brandEntity.setAffiliatedUsers(brandAffiliatedUsers);
        var brandFollowers = new ArrayList<>(brandEntity.getFollowers());
        brandFollowers.add(toSaveUserEntity);
        brandEntity.setFollowers(brandFollowers);

        userRepository.save(toSaveUserEntity);
        brandRepository.save(brandEntity);

        return true;
    }

    @Override
    @Transactional
    public void update(User userModel) {
        var userEntity = userRepository.findByIdOrThrowNotFound(userModel.getId());
        userEntityMapper.updateEntity(userEntity, userModel);
        var imageEntityList = imageEntityMapper.toEntity(userModel.getProfileImages());
        imageEntityList.forEach(imageEntity -> imageEntity.setUser(userEntity));
        userEntity.setProfileImages(imageEntityList);
        userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public void addBrand(Long userId, Long brandId) {
        var userEntity = userRepository.findByIdOrThrowNotFound(userId);
        var brandEntity = brandRepository.findByIdOrThrowNotFound(brandId);
        userEntity.getAffiliatedBrands().add(brandEntity);
        userEntity.getSubscribedBrands().add(brandEntity);
        userRepository.save(userEntity);
    }
}
