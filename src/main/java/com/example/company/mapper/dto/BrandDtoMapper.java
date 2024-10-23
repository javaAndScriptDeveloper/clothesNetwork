package com.example.company.mapper.dto;

import com.example.company.dto.request.CreateBrandRequest;
import com.example.company.model.Brand;
import org.mapstruct.Mapper;

@Mapper
public interface BrandDtoMapper {

    Brand toModel(CreateBrandRequest dto);
}
