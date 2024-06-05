package com.example.company.repository;

import com.example.company.entity.UserEntity;
import com.example.company.exception.UserNotFoundException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    default UserEntity findByIdOrThrowNotFound(Long id) {
        return findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    Optional<UserEntity> findByUsername(String username);
}
