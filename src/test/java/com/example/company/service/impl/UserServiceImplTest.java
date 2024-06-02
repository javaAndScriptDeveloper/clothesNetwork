package com.example.company.service.impl;

import static org.mockito.Mockito.verify;

import com.example.company.AbstractUnitTest;
import com.example.company.mapper.entity.ImageEntityMapper;
import com.example.company.mapper.entity.UserEntityMapper;
import com.example.company.model.User;
import com.example.company.repository.UserRepository;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

class UserServiceImplTest extends AbstractUnitTest {

    @Spy
    private final UserEntityMapper userEntityMapper = Mappers.getMapper(UserEntityMapper.class);

    @Spy
    private final ImageEntityMapper imageEntityMapper = Mappers.getMapper(ImageEntityMapper.class);

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl sut;

    @Test
    void whenSaveUser_ThenUserSaved(@Random User user) {
        // when
        sut.create(user);

        // then
        var expectedEntity = userEntityMapper.toEntity(user);
        expectedEntity.setProfileImages(imageEntityMapper.toEntity(user.getProfileImages()));

        verify(userRepository).save(expectedEntity);
    }
}
