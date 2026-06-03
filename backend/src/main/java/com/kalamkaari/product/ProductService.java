package com.kalamkaari.product;

import com.kalamkaari.product.dto.CreateProductRequest;
import com.kalamkaari.product.dto.ProductResponse;
import com.kalamkaari.product.dto.UpdateProductRequest;
import com.kalamkaari.shared.exception.InsufficientStockException;
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
                .categoryIds(request.getCategoryIds() != null ? request.getCategoryIds() : List.of())
                .stockQuantity(request.getStockQuantity())
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
        product.setCategoryIds(request.getCategoryIds() != null ? request.getCategoryIds() : List.of());
        product.setStockQuantity(request.getStockQuantity());

        return ProductResponse.from(productRepository.save(product));
    }

    public ProductResponse updateStock(String id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setStockQuantity(quantity);
        return ProductResponse.from(productRepository.save(product));
    }

    // Called by the order service when an order is placed (Sprint 5)
    public void decrementStock(String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(productId, quantity, product.getStockQuantity());
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
    }

    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }
}
