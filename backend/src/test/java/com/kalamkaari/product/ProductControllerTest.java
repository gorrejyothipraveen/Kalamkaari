package com.kalamkaari.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalamkaari.product.dto.CreateProductRequest;
import com.kalamkaari.product.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    void createProduct_validRequest_returns201() throws Exception {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Kalamkaari Saree");
        request.setDescription("Handcrafted Kalamkaari cotton saree");
        request.setPrice(250000L);
        request.setImageUrls(List.of("https://example.com/saree.jpg"));

        ProductResponse response = ProductResponse.builder()
                .id("abc123")
                .name("Kalamkaari Saree")
                .description("Handcrafted Kalamkaari cotton saree")
                .price(250000L)
                .imageUrls(List.of("https://example.com/saree.jpg"))
                .createdAt(Instant.now())
                .build();

        when(productService.createProduct(any())).thenReturn(response);

        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("abc123"))
                .andExpect(jsonPath("$.name").value("Kalamkaari Saree"))
                .andExpect(jsonPath("$.price").value(250000));
    }

    @Test
    void createProduct_missingName_returns400() throws Exception {
        CreateProductRequest request = new CreateProductRequest();
        request.setPrice(250000L);

        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void createProduct_missingPrice_returns400() throws Exception {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Test Product");

        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    void createProduct_negativePrice_returns400() throws Exception {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Test Product");
        request.setPrice(-500L);

        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    void getAllProducts_returnsEmptyList() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
