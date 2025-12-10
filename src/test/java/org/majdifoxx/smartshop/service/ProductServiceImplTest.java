package org.majdifoxx.smartshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.majdifoxx.smartshop.dto.request.ProductRequestDTO;
import org.majdifoxx.smartshop.dto.response.ProductResponseDTO;
import org.majdifoxx.smartshop.entity.Product;
import org.majdifoxx.smartshop.exception.BusinessRuleException;
import org.majdifoxx.smartshop.exception.ResourceNotFoundException;
import org.majdifoxx.smartshop.mapper.ProductMapper;
import org.majdifoxx.smartshop.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductMapper productMapper;
    @InjectMocks private ProductServiceImpl service;

    @Test
    void createProduct_Success() {
        ProductRequestDTO request = new ProductRequestDTO();
        Product entity = Product.builder()
                .name("Test Product")
                .unitPrice(BigDecimal.valueOf(10.99))
                .stock(100)
                .build();
        Product saved = Product.builder()
                .id(1L)
                .name("Test Product")
                .unitPrice(BigDecimal.valueOf(10.99))
                .stock(100)
                .build();
        ProductResponseDTO dto = new ProductResponseDTO();

        when(productMapper.toEntity(request)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(saved);
        when(productMapper.toResponseDTO(saved)).thenReturn(dto);

        ProductResponseDTO result = service.createProduct(request);
        assertEquals(dto, result);
        verify(productMapper).toEntity(request);
        verify(productRepository).save(entity);
    }

    @Test
    void getProductById_NotFound_ThrowsResourceNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getProductById(999L));
    }

    @Test
    void getProductById_Success() {
        Product product = Product.builder().id(1L).name("Test").build();
        ProductResponseDTO dto = new ProductResponseDTO();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(dto);

        ProductResponseDTO result = service.getProductById(1L);
        assertEquals(dto, result);
    }

    @Test
    void getActiveProducts_NoSearch_ReturnsActivePage() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = List.of(Product.builder().id(1L).deleted(false).build());
        Page<Product> page = new PageImpl<>(products);
        ProductResponseDTO dto = new ProductResponseDTO();
        when(productRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(productMapper.toResponseDTO(products.get(0))).thenReturn(dto);

        Page<ProductResponseDTO> result = service.getActiveProducts(null, pageable);
        assertEquals(1, result.getContent().size());
        assertEquals(dto, result.getContent().get(0));
        verify(productRepository).findByDeletedFalse(pageable);
    }

    @Test
    void getActiveProducts_WithSearch_ReturnsFilteredPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of());  // Empty, no mapper call
        when(productRepository.findByNameContainingIgnoreCaseAndDeletedFalse("test", pageable)).thenReturn(page);

        Page<ProductResponseDTO> result = service.getActiveProducts("test", pageable);
        assertEquals(0, result.getTotalElements());
        verify(productRepository).findByNameContainingIgnoreCaseAndDeletedFalse("test", pageable);
        // No mapper stub needed (empty page)
    }

    @Test
    void updateProduct_Success() {
        Product existing = Product.builder().id(1L).name("Old Name").build();
        ProductRequestDTO request = new ProductRequestDTO();
        Product saved = Product.builder().id(1L).name("New Name").build();
        ProductResponseDTO dto = new ProductResponseDTO();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(saved);
        when(productMapper.toResponseDTO(saved)).thenReturn(dto);

        ProductResponseDTO result = service.updateProduct(1L, request);
        assertEquals(dto, result);
        verify(productMapper).updateEntityFromDTO(request, existing);
    }

    @Test
    void deleteProduct_NotFound_ThrowsResourceNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.deleteProduct(999L));
    }

    @Test
    void deleteProduct_SoftDeletes() {
        Product product = Product.builder().id(1L).deleted(false).name("Test").build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        service.deleteProduct(1L);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getId());
        assertTrue(captor.getValue().getDeleted());
        assertEquals("Test", captor.getValue().getName());
    }

    @Test
    void validateStock_Deleted_ThrowsBusinessRule() {
        Product deleted = Product.builder().deleted(true).name("Unavailable").build();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.validateStock(deleted, 1));
        assertTrue(ex.getMessage().contains("no longer available"));
    }

    @Test
    void validateStock_Insufficient_ThrowsBusinessRule() {
        Product lowStock = Product.builder().stock(2).name("Low Stock").deleted(false).build();
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.validateStock(lowStock, 3));
        assertTrue(ex.getMessage().contains("Insufficient stock"));
    }

    @Test
    void validateStock_Sufficient_NoException() {
        Product sufficient = Product.builder().stock(5).deleted(false).build();
        service.validateStock(sufficient, 3);
        // Passes
    }

    @Test
    void decrementStock_UpdatesAndSaves() {
        Product product = Product.builder().id(1L).stock(10).build();
        Product saved = Product.builder().id(1L).stock(7).build();
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        service.decrementStock(product, 3);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals(7, captor.getValue().getStock().intValue());
    }

    @Test
    void getProductEntity_NotFound_ThrowsResourceNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getProductEntity(999L));
    }
}
