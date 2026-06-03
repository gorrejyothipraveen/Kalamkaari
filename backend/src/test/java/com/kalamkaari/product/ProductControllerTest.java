package com.kalamkaari.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalamkaari.product.dto.CreateProductRequest;
import com.kalamkaari.product.dto.ProductResponse;
import com.kalamkaari.product.dto.StockUpdateRequest;
import com.kalamkaari.product.dto.UpdateProductRequest;
import com.kalamkaari.shared.exception.InsufficientStockException;
import com.kalamkaari.shared.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    // ── POST /api/admin/products ──────────────────────────────────────────────

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

    // ── GET /api/admin/products ───────────────────────────────────────────────

    @Test
    void getAllProducts_noParams_returnsAllProducts() throws Exception {
        when(productService.getAllProducts(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAllProducts_sortStockAsc_passesParamToService() throws Exception {
        ProductResponse p1 = ProductResponse.builder().id("a").name("A").price(100L)
                .stockQuantity(2).imageUrls(List.of()).categoryIds(List.of())
                .createdAt(Instant.now()).build();
        ProductResponse p2 = ProductResponse.builder().id("b").name("B").price(200L)
                .stockQuantity(10).imageUrls(List.of()).categoryIds(List.of())
                .createdAt(Instant.now()).build();

        when(productService.getAllProducts("stock_asc", null)).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/admin/products?sort=stock_asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockQuantity").value(2))
                .andExpect(jsonPath("$[1].stockQuantity").value(10));
    }

    @Test
    void getAllProducts_filterOutOfStock_returnsZeroStockProducts() throws Exception {
        ProductResponse oos = ProductResponse.builder().id("c").name("C").price(150L)
                .stockQuantity(0).imageUrls(List.of()).categoryIds(List.of())
                .createdAt(Instant.now()).build();

        when(productService.getAllProducts(null, "out_of_stock")).thenReturn(List.of(oos));

        mockMvc.perform(get("/api/admin/products?filter=out_of_stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockQuantity").value(0));
    }

    @Test
    void getAllProducts_filterLowStock_returnsLowStockProducts() throws Exception {
        ProductResponse low = ProductResponse.builder().id("d").name("D").price(200L)
                .stockQuantity(3).imageUrls(List.of()).categoryIds(List.of())
                .createdAt(Instant.now()).build();

        when(productService.getAllProducts(null, "low_stock")).thenReturn(List.of(low));

        mockMvc.perform(get("/api/admin/products?filter=low_stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockQuantity").value(3));
    }

    // ── GET /api/admin/products/{id} ──────────────────────────────────────────

    @Test
    void getProductById_found_returns200() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id("abc123")
                .name("Kalamkaari Saree")
                .price(250000L)
                .imageUrls(List.of())
                .createdAt(Instant.now())
                .build();

        when(productService.getProductById("abc123")).thenReturn(response);

        mockMvc.perform(get("/api/admin/products/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("abc123"))
                .andExpect(jsonPath("$.name").value("Kalamkaari Saree"));
    }

    @Test
    void getProductById_notFound_returns404() throws Exception {
        when(productService.getProductById("nonexistent"))
                .thenThrow(new ProductNotFoundException("nonexistent"));

        mockMvc.perform(get("/api/admin/products/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // ── PUT /api/admin/products/{id} ──────────────────────────────────────────

    @Test
    void updateProduct_validRequest_returns200() throws Exception {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("Updated Saree");
        request.setPrice(300000L);

        ProductResponse response = ProductResponse.builder()
                .id("abc123")
                .name("Updated Saree")
                .price(300000L)
                .imageUrls(List.of())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(productService.updateProduct(eq("abc123"), any())).thenReturn(response);

        mockMvc.perform(put("/api/admin/products/abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Saree"))
                .andExpect(jsonPath("$.price").value(300000));
    }

    @Test
    void updateProduct_missingName_returns400() throws Exception {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setPrice(300000L);

        mockMvc.perform(put("/api/admin/products/abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void updateProduct_notFound_returns404() throws Exception {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("Updated Saree");
        request.setPrice(300000L);

        when(productService.updateProduct(eq("nonexistent"), any()))
                .thenThrow(new ProductNotFoundException("nonexistent"));

        mockMvc.perform(put("/api/admin/products/nonexistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // ── DELETE /api/admin/products/{id} ───────────────────────────────────────

    @Test
    void deleteProduct_found_returns204() throws Exception {
        doNothing().when(productService).deleteProduct("abc123");

        mockMvc.perform(delete("/api/admin/products/abc123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_notFound_returns404() throws Exception {
        doThrow(new ProductNotFoundException("nonexistent"))
                .when(productService).deleteProduct("nonexistent");

        mockMvc.perform(delete("/api/admin/products/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // ── PATCH /api/admin/products/{id}/stock ──────────────────────────────────

    @Test
    void updateStock_validQuantity_returns200() throws Exception {
        StockUpdateRequest request = new StockUpdateRequest();
        request.setQuantity(50);

        ProductResponse response = ProductResponse.builder()
                .id("abc123")
                .name("Kalamkaari Saree")
                .price(250000L)
                .stockQuantity(50)
                .imageUrls(List.of())
                .categoryIds(List.of())
                .createdAt(Instant.now())
                .build();

        when(productService.updateStock("abc123", 50)).thenReturn(response);

        mockMvc.perform(patch("/api/admin/products/abc123/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(50));
    }

    @Test
    void updateStock_zeroQuantity_returns200() throws Exception {
        StockUpdateRequest request = new StockUpdateRequest();
        request.setQuantity(0);

        ProductResponse response = ProductResponse.builder()
                .id("abc123")
                .name("Kalamkaari Saree")
                .price(250000L)
                .stockQuantity(0)
                .imageUrls(List.of())
                .categoryIds(List.of())
                .createdAt(Instant.now())
                .build();

        when(productService.updateStock("abc123", 0)).thenReturn(response);

        mockMvc.perform(patch("/api/admin/products/abc123/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(0));
    }

    @Test
    void updateStock_negativeQuantity_returns400() throws Exception {
        StockUpdateRequest request = new StockUpdateRequest();
        request.setQuantity(-10);

        mockMvc.perform(patch("/api/admin/products/abc123/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.quantity").exists());
    }

    @Test
    void updateStock_missingQuantity_returns400() throws Exception {
        mockMvc.perform(patch("/api/admin/products/abc123/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.quantity").exists());
    }

    @Test
    void updateStock_productNotFound_returns404() throws Exception {
        StockUpdateRequest request = new StockUpdateRequest();
        request.setQuantity(10);

        when(productService.updateStock(eq("nonexistent"), eq(10)))
                .thenThrow(new ProductNotFoundException("nonexistent"));

        mockMvc.perform(patch("/api/admin/products/nonexistent/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // ── available flag (US-033) ───────────────────────────────────────────────

    @Test
    void getProductById_inStock_availableIsTrue() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id("abc123")
                .name("Saree")
                .price(100L)
                .stockQuantity(10)
                .available(true)
                .imageUrls(List.of())
                .categoryIds(List.of())
                .createdAt(Instant.now())
                .build();

        when(productService.getProductById("abc123")).thenReturn(response);

        mockMvc.perform(get("/api/admin/products/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getProductById_outOfStock_availableIsFalse() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id("xyz999")
                .name("Sold Out Item")
                .price(100L)
                .stockQuantity(0)
                .available(false)
                .imageUrls(List.of())
                .categoryIds(List.of())
                .createdAt(Instant.now())
                .build();

        when(productService.getProductById("xyz999")).thenReturn(response);

        mockMvc.perform(get("/api/admin/products/xyz999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }
}
