package com.kalamkaari.product.dto;

import com.kalamkaari.product.Product;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ProductResponse {

    private String id;
    private String name;
    private String description;
    private long price;
    private List<String> imageUrls;
    private List<String> categoryIds;
    private int stockQuantity;
    private boolean available;
    private Instant createdAt;
    private Instant updatedAt;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrls(product.getImageUrls())
                .categoryIds(product.getCategoryIds())
                .stockQuantity(product.getStockQuantity())
                .available(product.getStockQuantity() > 0)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
