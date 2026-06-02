package com.kalamkaari.product;

import com.kalamkaari.product.dto.CreateProductRequest;
import com.kalamkaari.product.dto.ProductResponse;
import com.kalamkaari.product.dto.UpdateProductRequest;
import com.kalamkaari.shared.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrls(request.getImageUrls() != null ? request.getImageUrls() : List.of())
                .categoryId(request.getCategoryId())
                .build();

        return ProductResponse.from(productRepository.save(product));
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse getProductById(String id) {
        return productRepository.findById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public ProductResponse updateProduct(String id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageUrls(request.getImageUrls() != null ? request.getImageUrls() : List.of());
        product.setCategoryId(request.getCategoryId());

        return ProductResponse.from(productRepository.save(product));
    }
}
