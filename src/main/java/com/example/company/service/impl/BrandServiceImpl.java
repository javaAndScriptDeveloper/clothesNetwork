package com.example.company.service.impl;

import com.example.company.mapper.entity.BrandEntityMapper;
import com.example.company.mapper.entity.ImageEntityMapper;
import com.example.company.model.Brand;
import com.example.company.repository.BrandRepository;
import com.example.company.repository.UserRepository;
import com.example.company.service.BrandService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandEntityMapper brandEntityMapper;
    private final ImageEntityMapper imageEntityMapper;

    private final BrandRepository brandRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void save(Brand brand) {
        var brandEntity = brandEntityMapper.toEntity(brand);
        var authorEntity = userRepository.findByIdOrThrowNotFound(brand.getAuthorId());
        brandEntity.setManagingUsers(List.of(authorEntity));
        var imageEntityList = imageEntityMapper.toEntity(brand.getProfileImages());
        imageEntityList.forEach(imageEntity -> imageEntity.setBrand(brandEntity));
        brandEntity.setProfileImages(imageEntityList);
        var savedBrandEntity = brandRepository.save(brandEntity);
        authorEntity.setManagedBrand(savedBrandEntity);
        userRepository.save(authorEntity);
    }
}
