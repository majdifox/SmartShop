package org.majdifoxx.smartshop.repository;

import org.majdifoxx.smartshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByDeletedFalse(Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndDeletedFalse(String name, Pageable pageable);
}
