package com.example.company.mapper.entity;

import com.example.company.entity.BrandEntity;
import com.example.company.model.Brand;
import org.mapstruct.Mapper;

@Mapper
public interface BrandEntityMapper {

    BrandEntity toEntity(Brand brand);
}
