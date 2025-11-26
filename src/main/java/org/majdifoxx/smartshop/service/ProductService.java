package org.majdifoxx.smartshop.service;

import org.majdifoxx.smartshop.dto.request.ProductRequestDTO;
import org.majdifoxx.smartshop.dto.response.ProductResponseDTO;
import org.majdifoxx.smartshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO request);
    ProductResponseDTO getProductById(Long id);
    Page<ProductResponseDTO> getActiveProducts(String search, Pageable pageable);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO request);
    void deleteProduct(Long id);
    void validateStock(Product product, Integer quantity);
    void decrementStock(Product product, Integer quantity);
    Product getProductEntity(Long id);
}
