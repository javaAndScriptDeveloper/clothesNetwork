package com.example.company.service.impl;

import com.example.company.entity.BrandEntity;
import com.example.company.mapper.UserDetailsMapper;
import com.example.company.model.UserDetailsEnhanced;
import com.example.company.repository.UserRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDetailsMapper userDetailsMapper;
    private final UserRepository userRepository;

    @Override
    @SneakyThrows
    public UserDetailsEnhanced loadUserByUsername(String username) {
        var userEntity = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User by username '%s' not found".formatted(username)));
        var userDetails = userDetailsMapper.toUserDetails(userEntity);
        userDetails.setBrandIds(
                userEntity.getBrands().stream().map(BrandEntity::getId).collect(Collectors.toList()));
        return userDetails;
    }
}
