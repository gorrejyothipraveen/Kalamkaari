package com.kalamkaari.catalog;

import com.kalamkaari.product.ProductService;
import com.kalamkaari.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class CatalogController {

    private final ProductService productService;

    @GetMapping
    public Page<ProductResponse> getProducts(@PageableDefault(size = 12) Pageable pageable) {
        return productService.getAvailableProducts(pageable);
    }
}
