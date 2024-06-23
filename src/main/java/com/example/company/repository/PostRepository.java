package com.example.company.repository;

import com.example.company.entity.PostEntity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, UUID> {

    Page<PostEntity> findByFeedsId(UUID feedId, Pageable pageable);
}
