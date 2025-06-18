package com.sunil45.ecommerce.repository;

import com.sunil45.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory_CategoryId(Long id, Pageable pageable);

    Page<Product> findByCategoryNameContainingIgnoreCase(String keyword, Pageable pageable);

    boolean existsByCategoryNameAndCategory_CategoryId(String productName, Long categoryId);
}
