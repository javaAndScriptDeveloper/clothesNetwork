package com.example.company.service.impl;

import com.example.company.mapper.entity.ImageEntityMapper;
import com.example.company.mapper.entity.UserEntityMapper;
import com.example.company.model.User;
import com.example.company.repository.UserRepository;
import com.example.company.service.InviteService;
import com.example.company.service.UserService;
import jakarta.transaction.Transactional;
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

        var userEntity = userEntityMapper.toEntity(userModel);
        var imageEntityList = imageEntityMapper.toEntity(userModel.getProfileImages());
        imageEntityList.forEach(imageEntity -> imageEntity.setUser(userEntity));
        userEntity.setProfileImages(imageEntityList);
        userRepository.save(userEntity);
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
}
