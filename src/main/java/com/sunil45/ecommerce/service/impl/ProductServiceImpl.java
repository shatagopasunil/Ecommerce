package com.sunil45.ecommerce.service.impl;

import com.sunil45.ecommerce.dto.PageDetailsDTO;
import com.sunil45.ecommerce.dto.ProductDTO;
import com.sunil45.ecommerce.dto.ProductResponseDTO;
import com.sunil45.ecommerce.exceptions.ApiRequestException;
import com.sunil45.ecommerce.model.Category;
import com.sunil45.ecommerce.model.Product;
import com.sunil45.ecommerce.repository.CategoryRepository;
import com.sunil45.ecommerce.repository.ProductRepository;
import com.sunil45.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiRequestException("Category with id " + categoryId + " does not exist"));
        boolean productExists = productRepository.existsByCategory_CategoryNameAndCategory_CategoryId(
                productDTO.getProductName(), categoryId);
        if (productExists) {
            throw new ApiRequestException(
                    "Product with name '" + productDTO.getProductName() + "' already exists in this category");
        }
        Product product = modelMapper.map(productDTO, Product.class);
        product.setCategory(category);
        return modelMapper.map(productRepository.save(product), ProductDTO.class);
    }

    @Override
    public ProductResponseDTO getProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productsPage = productRepository.findAll(pageable);
        List<ProductDTO> productDTOList = productsPage.getContent().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        PageDetailsDTO pageDetailsDTO = new PageDetailsDTO(productsPage.getNumber(), productsPage.getSize(),
                productsPage.getTotalPages(), productsPage.getTotalElements(), productsPage.isLast());
        return new ProductResponseDTO(productDTOList, pageDetailsDTO);
    }

    @Override
    public ProductResponseDTO getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize,
            String sortBy, String sortOrder) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productsPage = productRepository.findByCategory_CategoryId(categoryId, pageable);
        if (productsPage.isEmpty() && !categoryRepository.existsById(categoryId)) {
            throw new ApiRequestException("Category with id " + categoryId + " does not exist");
        }
        List<ProductDTO> productDTOList = productsPage.getContent().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        PageDetailsDTO pageDetailsDTO = new PageDetailsDTO(productsPage.getNumber(), productsPage.getSize(),
                productsPage.getTotalPages(), productsPage.getTotalElements(), productsPage.isLast());
        return new ProductResponseDTO(productDTOList, pageDetailsDTO);
    }

    @Override
    public ProductResponseDTO getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productsPage = productRepository.findByCategory_CategoryNameContainingIgnoreCase(keyword, pageable);
        List<ProductDTO> productDTOList = productsPage.getContent().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        PageDetailsDTO pageDetailsDTO = new PageDetailsDTO(productsPage.getNumber(), productsPage.getSize(),
                productsPage.getTotalPages(), productsPage.getTotalElements(), productsPage.isLast());
        return new ProductResponseDTO(productDTOList, pageDetailsDTO);
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiRequestException("Product with id " + productId + " does not exist"));
        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setQuantity(productDTO.getQuantity());
        product.setPrice(productDTO.getPrice());
        product.setDiscount(productDTO.getDiscount());
        return modelMapper.map(productRepository.save(product), ProductDTO.class);
    }

    @Override
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ApiRequestException("Product with id " + productId + " does not exist");
        }
        productRepository.deleteById(productId);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        if (image.isEmpty()) {
            throw new ApiRequestException("Product image is empty");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiRequestException("Product with id " + productId + " does not exist"));
        String fileName = uploadImage(image);
        product.setImage(fileName);
        return modelMapper.map(productRepository.save(product), ProductDTO.class);
    }

    private String uploadImage(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new ApiRequestException("Invalid file name");
        }

        String path = "images";
        String randomId = UUID.randomUUID().toString();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String fileName = randomId + fileExtension;

        File folder = new File(path);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Failed to create directory: " + path);
        }

        Path destination = Paths.get(path, fileName);
        Files.copy(image.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private Pageable createPageable(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
