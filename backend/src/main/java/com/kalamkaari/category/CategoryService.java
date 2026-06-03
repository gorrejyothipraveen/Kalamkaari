package com.kalamkaari.category;

import com.kalamkaari.category.dto.CategoryRequest;
import com.kalamkaari.category.dto.CategoryResponse;
import com.kalamkaari.product.ProductRepository;
import com.kalamkaari.shared.exception.CategoryInUseException;
import com.kalamkaari.shared.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .build();
        return CategoryResponse.from(categoryRepository.save(category));
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public CategoryResponse getCategoryById(String id) {
        return categoryRepository.findById(id)
                .map(CategoryResponse::from)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public CategoryResponse renameCategory(String id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        category.setName(request.getName());
        return CategoryResponse.from(categoryRepository.save(category));
    }

    public void deleteCategory(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        if (productRepository.existsByCategoryIdsContaining(id)) {
            throw new CategoryInUseException(id);
        }
        categoryRepository.deleteById(id);
    }
}
