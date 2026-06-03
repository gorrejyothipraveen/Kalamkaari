package com.kalamkaari.catalog;

import com.kalamkaari.product.ProductService;
import com.kalamkaari.product.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
@ActiveProfiles("test")
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void getProducts_returnsAvailableProducts() throws Exception {
        ProductResponse product = ProductResponse.builder()
                .id("p1")
                .name("Kalamkaari Saree")
                .price(250000L)
                .stockQuantity(10)
                .available(true)
                .imageUrls(List.of("https://example.com/img.jpg"))
                .categoryIds(List.of())
                .createdAt(Instant.now())
                .build();

        var page = new PageImpl<>(List.of(product), PageRequest.of(0, 12), 1);
        when(productService.getAvailableProducts(any())).thenReturn(page);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("p1"))
                .andExpect(jsonPath("$.content[0].name").value("Kalamkaari Saree"))
                .andExpect(jsonPath("$.content[0].available").value(true))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getProducts_emptyPage_returnsEmptyContent() throws Exception {
        var emptyPage = new PageImpl<ProductResponse>(List.of(), PageRequest.of(0, 12), 0);
        when(productService.getAvailableProducts(any())).thenReturn(emptyPage);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getProducts_withPaginationParams_passesPageableToService() throws Exception {
        ProductResponse p1 = ProductResponse.builder()
                .id("p2").name("Dupatta").price(80000L)
                .stockQuantity(5).available(true)
                .imageUrls(List.of()).categoryIds(List.of())
                .createdAt(Instant.now()).build();
        ProductResponse p2 = ProductResponse.builder()
                .id("p3").name("Kurta").price(120000L)
                .stockQuantity(3).available(true)
                .imageUrls(List.of()).categoryIds(List.of())
                .createdAt(Instant.now()).build();

        var page = new PageImpl<>(List.of(p1, p2), PageRequest.of(1, 2), 4);
        when(productService.getAvailableProducts(any())).thenReturn(page);

        mockMvc.perform(get("/api/products?page=1&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(1));
    }
}
