package com.example.company.service.impl;

import com.example.company.exception.NoAuthenticatedUserException;
import com.example.company.mapper.UserDetailsMapper;
import com.example.company.model.User;
import com.example.company.model.UserDetailsEnhanced;
import com.example.company.service.SecurityService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final UserDetailsMapper userDetailsMapper;

    @Override
    public User getCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(authentication -> {
                    var userDetails = (UserDetailsEnhanced) authentication.getPrincipal();
                    return userDetailsMapper.toModel(userDetails);
                })
                .orElseThrow(NoAuthenticatedUserException::new);
    }
}
