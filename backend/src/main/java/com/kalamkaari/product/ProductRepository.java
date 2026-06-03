package com.kalamkaari.product;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    boolean existsByCategoryIdsContaining(String categoryId);
}
