package com.example.company.repository;

import com.example.company.entity.BrandEntity;
import com.example.company.exception.notfound.BrandNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<BrandEntity, Long> {

    default BrandEntity findByIdOrThrowNotFound(Long id) {
        return findById(id).orElseThrow(() -> new BrandNotFoundException(id));
    }
}
