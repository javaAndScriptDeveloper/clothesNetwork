package com.example.company.mapper.entity;

import com.example.company.entity.ImageEntity;
import com.example.company.model.Image;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface ImageEntityMapper {

    List<Image> toModel(List<ImageEntity> entity);

    List<ImageEntity> toEntity(List<Image> models);
}
