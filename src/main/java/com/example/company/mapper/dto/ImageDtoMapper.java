package com.example.company.mapper.dto;

import com.example.company.dto.ImageDto;
import com.example.company.model.Image;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface ImageDtoMapper {

    List<Image> toModel(List<ImageDto> dto);
}
