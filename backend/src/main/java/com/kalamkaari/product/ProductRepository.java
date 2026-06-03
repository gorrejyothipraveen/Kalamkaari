package com.kalamkaari.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    boolean existsByCategoryIdsContaining(String categoryId);

    List<Product> findByStockQuantity(int quantity, Sort sort);

    List<Product> findByStockQuantityBetween(int min, int max, Sort sort);

    Page<Product> findByStockQuantityGreaterThan(int threshold, Pageable pageable);
}
