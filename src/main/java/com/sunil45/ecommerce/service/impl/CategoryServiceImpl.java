package com.sunil45.ecommerce.service.impl;

import com.sunil45.ecommerce.dto.CategoryDTO;
import com.sunil45.ecommerce.dto.CategoryResponseDTO;
import com.sunil45.ecommerce.dto.PageDetailsDTO;
import com.sunil45.ecommerce.exceptions.ApiRequestException;
import com.sunil45.ecommerce.model.Category;
import com.sunil45.ecommerce.repository.CategoryRepository;
import com.sunil45.ecommerce.service.CategoryService;
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
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<CategoryDTO> categoryDTOList = categoryPage.getContent().stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class)).toList();
        PageDetailsDTO pageDetailsDTO = new PageDetailsDTO(categoryPage.getNumber(), categoryPage.getSize(),
                categoryPage.getTotalPages(), categoryPage.getTotalElements(), categoryPage.isLast());
        return new CategoryResponseDTO(categoryDTOList, pageDetailsDTO);
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category dbCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (dbCategory != null) {
            throw new ApiRequestException("Category with name " + category.getCategoryName() + " already exists");
        }
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ApiRequestException("Category with id " + categoryId + " does not exist");
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category dbCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiRequestException("Category with id " + categoryId + " does not exist"));
        Category dupCategory = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if (dupCategory != null && !dupCategory.getCategoryId().equals(categoryId)) {
            throw new ApiRequestException("Category with name " + categoryDTO.getCategoryName() + " already exists");
        }
        dbCategory.setCategoryName(categoryDTO.getCategoryName());
        Category savedCategory = categoryRepository.save(dbCategory);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }
}
