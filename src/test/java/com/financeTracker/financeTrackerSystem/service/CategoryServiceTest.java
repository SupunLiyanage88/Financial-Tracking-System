package com.financeTracker.financeTrackerSystem.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.financetracker.financetrackersystem.dto.CategoryRequestDTO;
import com.financetracker.financetrackersystem.dto.CategoryResponseDTO;
import com.financetracker.financetrackersystem.entity.CategoryEntity;
import com.financetracker.financetrackersystem.repository.CategoryRepository;
import com.financetracker.financetrackersystem.service.CategoryService;
import com.financetracker.financetrackersystem.service.CategoryService.UnauthorizedException;
import com.financetracker.financetrackersystem.service.JWTService;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryEntity category;
    private CategoryRequestDTO categoryRequest;
    private String adminToken = "adminToken";
    private String userToken = "userToken";
    private String categoryId = "123";

    @BeforeEach
    void setUp() {
        category = new CategoryEntity();
        category.setId(categoryId);
        category.setCategoryName("Food");

        categoryRequest = new CategoryRequestDTO();
        categoryRequest.setCategoryName("Groceries");
    }

    // Test: Create Category - Success (Admin)
    @Test
    void testCreateCategory_Success_Admin() {
        when(jwtService.getUserRole(adminToken)).thenReturn("Admin");
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(category);

        CategoryResponseDTO response = categoryService.createCategory(categoryRequest, adminToken);

        assertNotNull(response);
        assertEquals("Category created successfully", response.getMessage());
        assertNull(response.getError());
    }

    // Test: Create Category - Unauthorized (Non-Admin)
    @Test
    void testCreateCategory_Unauthorized_NonAdmin() {
        when(jwtService.getUserRole(userToken)).thenReturn("User");

        CategoryResponseDTO response = categoryService.createCategory(categoryRequest, userToken);

        assertNotNull(response);
        assertNull(response.getMessage());
        assertEquals("Unauthorized", response.getError());
    }

    @Test
    void testGetAllCategories() {
        // Mock dependencies
        String userToken = "mockedToken";
        when(jwtService.getUserRole(userToken)).thenReturn("Admin");
        CategoryEntity category = new CategoryEntity();
        category.setCategoryName("Food");
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category));

        // Call the method
        List<CategoryEntity> categories = categoryService.getAllCategories(userToken);

        // Assertions
        assertNotNull(categories);
        assertEquals(1, categories.size());
        assertEquals("Food", categories.get(0).getCategoryName());
    }

    @Test
    void testGetAllCategories_Unauthorized() {
        // Mock unauthorized access
        String userToken = "mockedToken";
        when(jwtService.getUserRole(userToken)).thenReturn("User");

        // Verify exception is thrown
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, 
            () -> categoryService.getAllCategories(userToken));

        assertEquals("Only Admin can get all categories", exception.getMessage());
    }

    // Test: Update Category - Success
    @Test
    void testUpdateCategory_Success() {
        when(jwtService.getUserRole(adminToken)).thenReturn("Admin");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(category);

        CategoryResponseDTO response = categoryService.updateCategory(categoryRequest, adminToken, categoryId);

        assertNotNull(response);
        assertEquals("Category updated successfully, changes saved.", response.getMessage());
        assertNull(response.getError());
    }

    // Test: Update Category - Not Found
    @Test
    void testUpdateCategory_NotFound() {
        when(jwtService.getUserRole(adminToken)).thenReturn("Admin");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        CategoryResponseDTO response = categoryService.updateCategory(categoryRequest, adminToken, categoryId);

        assertNotNull(response);
        assertNull(response.getMessage());
        assertEquals("Unable to found Category", response.getError());
    }

    // Test: Update Category - Unauthorized
    @Test
    void testUpdateCategory_Unauthorized() {
        when(jwtService.getUserRole(userToken)).thenReturn("User");

        CategoryResponseDTO response = categoryService.updateCategory(categoryRequest, userToken, categoryId);

        assertNotNull(response);
        assertEquals("Only Admin can update category", response.getMessage());
        assertEquals("Unauthorized", response.getError());
    }

    // Test: Delete Category - Success
    @Test
    void testDeleteCategory_Success() {
        when(jwtService.getUserRole(adminToken)).thenReturn("Admin");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        CategoryResponseDTO response = categoryService.deleteCategory(adminToken, "yes", categoryId);

        assertNotNull(response);
        assertEquals("Category deleted successfully", response.getMessage());
        assertNull(response.getError());
    }

    // Test: Delete Category - Category Not Found
    @Test
    void testDeleteCategory_NotFound() {
        when(jwtService.getUserRole(adminToken)).thenReturn("Admin");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        CategoryResponseDTO response = categoryService.deleteCategory(adminToken, "yes", categoryId);

        assertNotNull(response);
        assertEquals("The category you’re looking for does not exist.", response.getMessage());
        assertEquals("Unable to found", response.getError());
    }

    // Test: Delete Category - Verification Error
    @Test
    void testDeleteCategory_VerificationError() {
        when(jwtService.getUserRole(adminToken)).thenReturn("Admin");

        CategoryResponseDTO response = categoryService.deleteCategory(adminToken, "no", categoryId);

        assertNotNull(response);
        assertEquals("Type yes to delete", response.getMessage());
        assertEquals("Verify error", response.getError());
    }

    // Test: Delete Category - Unauthorized
    @Test
    void testDeleteCategory_Unauthorized() {
        when(jwtService.getUserRole(userToken)).thenReturn("User");

        CategoryResponseDTO response = categoryService.deleteCategory(userToken, "yes", categoryId);

        assertNotNull(response);
        assertEquals("Only Admin can delete category", response.getMessage());
        assertEquals("Unauthorized", response.getError());
    }
}

