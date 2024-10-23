package com.example.company.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.example.company.dto.request.CreateBrandRequest;
import com.example.company.mapper.dto.BrandDtoMapper;
import com.example.company.mapper.dto.ImageDtoMapper;
import com.example.company.service.BrandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "brands")
@RequiredArgsConstructor
@RequestMapping("/api/${api.version}/brands")
public class BrandController {

    private final BrandDtoMapper brandDtoMapper;
    private final ImageDtoMapper imageDtoMapper;

    private final BrandService brandService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    void createBrand(@RequestBody @Valid CreateBrandRequest createBrandRequest) {
        var brandModel = brandDtoMapper.toModel(createBrandRequest);
        brandModel.setProfileImages(imageDtoMapper.toModel(createBrandRequest.getProfileImages()));
        brandService.save(brandModel);
    }
}
