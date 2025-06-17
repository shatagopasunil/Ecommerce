package com.sunil45.ecommerce.service;

import com.sunil45.ecommerce.dto.ProductDTO;
import com.sunil45.ecommerce.dto.ProductResponseDTO;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, @Valid ProductDTO productDTO);

    ProductResponseDTO getProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponseDTO getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize,
            String sortBy, String sortOrder);

    ProductResponseDTO getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize,
            String sortBy, String sortOrder);

    ProductDTO updateProduct(Long productId, @Valid ProductDTO productDTO);

    void deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
