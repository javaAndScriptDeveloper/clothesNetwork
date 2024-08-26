package com.example.company.repository;

import com.example.company.entity.PostEntity;
import com.example.company.exception.notfound.PostNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, UUID> {

    default PostEntity findByIdOrThrowNotFound(UUID id) {
        return findById(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    Page<PostEntity> findByFeedsId(UUID feedId, Pageable pageable);

    List<PostEntity> findAllByPublicationTimeIsBeforeAndPostedIsFalse(Instant threshold);
}
