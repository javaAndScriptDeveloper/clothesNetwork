package com.example.company.service;

import com.example.company.model.User;
import java.util.UUID;

public interface UserService {

    User getById(Long id);

    boolean create(User user, UUID secretCode);

    void update(User user);

    void addBrand(Long userId, Long brandId);

    void subscribe(Long subscriberId, Long toSubscribeId);
}
