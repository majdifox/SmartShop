package org.majdifoxx.smartshop.mapper;

import org.majdifoxx.smartshop.dto.request.ProductRequestDTO;
import org.majdifoxx.smartshop.dto.response.ProductResponseDTO;
import org.majdifoxx.smartshop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponseDTO toResponseDTO(Product product);

    Product toEntity(ProductRequestDTO dto);

    void updateEntityFromDTO(ProductRequestDTO dto, @MappingTarget Product product);
}
