package com.example.company.repository;

import com.example.company.entity.FeedEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedRepository extends JpaRepository<FeedEntity, UUID> {

    @Query(value = "SELECT f.id FROM FeedEntity f WHERE f.user.id = :userId")
    Optional<UUID> findFeedIdByUserId(Long userId);
}
