package com.sunil45.ecommerce.service;

import com.sunil45.ecommerce.dto.CategoryDTO;
import com.sunil45.ecommerce.dto.CategoryResponseDTO;
import com.sunil45.ecommerce.exceptions.ApiRequestException;
import com.sunil45.ecommerce.model.Category;
import com.sunil45.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryResponseDTO getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty()) {
            throw new ApiRequestException("No categories found");
        }
        List<CategoryDTO> categoryDTOList = categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class)).toList();
        return new CategoryResponseDTO(categoryDTOList, categoryPage.getNumber(), categoryPage.getSize(), categoryPage.getTotalPages(), categoryPage.getTotalElements(), categoryPage.isLast());
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category dbCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (dbCategory != null) {
            throw new ApiRequestException("Category with name " + category.getCategoryName() + " already exists");
        }
        categoryRepository.save(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            throw new ApiRequestException("Category with id " + categoryId + " does not exist");
        }
        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category dbCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiRequestException("Category with id " + categoryId + " does not exist"));
        Category dupCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (dupCategory != null) {
            throw new ApiRequestException("Category with name " + category.getCategoryName() + " already exists");
        }
        dbCategory.setCategoryName(category.getCategoryName());
        categoryRepository.save(dbCategory);
        return modelMapper.map(dbCategory, CategoryDTO.class);
    }
}
