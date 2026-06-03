package com.kalamkaari.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalamkaari.category.dto.CategoryRequest;
import com.kalamkaari.category.dto.CategoryResponse;
import com.kalamkaari.shared.exception.CategoryInUseException;
import com.kalamkaari.shared.exception.CategoryNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    // ── POST /api/admin/categories ────────────────────────────────────────────

    @Test
    void createCategory_validRequest_returns201() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Sarees");

        CategoryResponse response = CategoryResponse.builder()
                .id("cat1")
                .name("Sarees")
                .createdAt(Instant.now())
                .build();

        when(categoryService.createCategory(any())).thenReturn(response);

        mockMvc.perform(post("/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("cat1"))
                .andExpect(jsonPath("$.name").value("Sarees"));
    }

    @Test
    void createCategory_missingName_returns400() throws Exception {
        CategoryRequest request = new CategoryRequest();

        mockMvc.perform(post("/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    // ── GET /api/admin/categories ─────────────────────────────────────────────

    @Test
    void getAllCategories_returnsList() throws Exception {
        CategoryResponse cat = CategoryResponse.builder()
                .id("cat1").name("Sarees").createdAt(Instant.now()).build();

        when(categoryService.getAllCategories()).thenReturn(List.of(cat));

        mockMvc.perform(get("/api/admin/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sarees"));
    }

    // ── PUT /api/admin/categories/{id} ────────────────────────────────────────

    @Test
    void renameCategory_validRequest_returns200() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Silk Sarees");

        CategoryResponse response = CategoryResponse.builder()
                .id("cat1").name("Silk Sarees").createdAt(Instant.now()).build();

        when(categoryService.renameCategory(eq("cat1"), any())).thenReturn(response);

        mockMvc.perform(put("/api/admin/categories/cat1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Silk Sarees"));
    }

    @Test
    void renameCategory_notFound_returns404() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Silk Sarees");

        when(categoryService.renameCategory(eq("nonexistent"), any()))
                .thenThrow(new CategoryNotFoundException("nonexistent"));

        mockMvc.perform(put("/api/admin/categories/nonexistent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // ── DELETE /api/admin/categories/{id} ─────────────────────────────────────

    @Test
    void deleteCategory_notAssigned_returns204() throws Exception {
        doNothing().when(categoryService).deleteCategory("cat1");

        mockMvc.perform(delete("/api/admin/categories/cat1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategory_inUse_returns409() throws Exception {
        doThrow(new CategoryInUseException("cat1"))
                .when(categoryService).deleteCategory("cat1");

        mockMvc.perform(delete("/api/admin/categories/cat1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }
}
