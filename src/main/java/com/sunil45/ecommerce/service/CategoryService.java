package com.sunil45.ecommerce.service;

import com.sunil45.ecommerce.dto.CategoryDTO;
import com.sunil45.ecommerce.dto.CategoryResponseDTO;

public interface CategoryService {

    CategoryResponseDTO getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CategoryDTO addCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO);

}
