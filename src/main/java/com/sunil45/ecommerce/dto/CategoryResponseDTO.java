package com.sunil45.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDTO {
    private List<CategoryDTO> content;
    Integer pageNumber;
    Integer pageSize;
    Integer totalPages;
    Long totalElements;
    Boolean lastPage;
}
