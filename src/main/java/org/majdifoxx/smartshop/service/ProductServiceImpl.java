package org.majdifoxx.smartshop.service;

import lombok.RequiredArgsConstructor;
import org.majdifoxx.smartshop.dto.request.ProductRequestDTO;
import org.majdifoxx.smartshop.dto.response.ProductResponseDTO;
import org.majdifoxx.smartshop.entity.Product;
import org.majdifoxx.smartshop.exception.BusinessRuleException;
import org.majdifoxx.smartshop.exception.ResourceNotFoundException;
import org.majdifoxx.smartshop.mapper.ProductMapper;
import org.majdifoxx.smartshop.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        Product product = productMapper.toEntity(request);
        Product saved = productRepository.save(product);
        return productMapper.toResponseDTO(saved);
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toResponseDTO(product);
    }

    @Override
    public Page<ProductResponseDTO> getActiveProducts(String search, Pageable pageable) {
        Page<Product> products;

        if (search != null && !search.trim().isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCaseAndDeletedFalse(search, pageable);
        } else {
            products = productRepository.findByDeletedFalse(pageable);
        }

        return products.map(productMapper::toResponseDTO);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        productMapper.updateEntityFromDTO(request, product);
        Product updated = productRepository.save(product);
        return productMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public void validateStock(Product product, Integer quantity) {
        // PDF: "Validation stock: quantité_demandée ≤ stock_disponible"
        if (product.getDeleted()) {
            throw new BusinessRuleException("Product is no longer available: " + product.getName());
        }
        if (product.getStock() < quantity) {
            throw new BusinessRuleException("Insufficient stock for product: " + product.getName());
        }
    }

    @Override
    public void decrementStock(Product product, Integer quantity) {
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    @Override
    public Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}
