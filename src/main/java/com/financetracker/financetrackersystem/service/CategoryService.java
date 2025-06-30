package com.financetracker.financetrackersystem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.financetracker.financetrackersystem.dto.CategoryRequestDTO;
import com.financetracker.financetrackersystem.dto.CategoryResponseDTO;
import com.financetracker.financetrackersystem.entity.CategoryEntity;
import com.financetracker.financetrackersystem.repository.CategoryRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final JWTService jwtService;


    //Create Category
    public CategoryResponseDTO createCategory(CategoryRequestDTO req, String userToken){
        String userRole = jwtService.getUserRole(userToken);
        if(!"Admin".equals(userRole)){
            return new CategoryResponseDTO(
                null,
                "Unauthorized"
            );
        }

        CategoryEntity category = new CategoryEntity();
        category.setCategoryName(req.getCategoryName());
        CategoryEntity savedCategory = categoryRepository.save(category);

        categoryRepository.save(category);
        if (savedCategory.getId() == null) return new CategoryResponseDTO(
            null,
            "Failed to create category"
        );
        return new CategoryResponseDTO("Category created successfully", null);
    }

    //Get all categories
    public List<CategoryEntity> getAllCategories(String userToken) {
        String userRole = jwtService.getUserRole(userToken);
        if (!"Admin".equals(userRole)) {
            throw new UnauthorizedException("Only Admin can get all categories");
        }
        return categoryRepository.findAll();
    }

    public class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    //Update Category
    public CategoryResponseDTO updateCategory(CategoryRequestDTO req, String userToken, String CategoryId){
        String userRole = jwtService.getUserRole(userToken);
        if(!"Admin".equals(userRole)){
            return new CategoryResponseDTO(
                "Only Admin can update category",
                "Unauthorized"
            );
        }

        Optional<CategoryEntity> categoryOptional = categoryRepository.findById(CategoryId);
        if(categoryOptional.isEmpty()){
            return new CategoryResponseDTO(null, "Unable to found Category");
        }
        CategoryEntity category = categoryOptional.get();
        category.setCategoryName(req.getCategoryName());
        categoryRepository.save(category);
        return new CategoryResponseDTO("Category updated successfully, changes saved.", null);
    }

    //Delete Category
    public CategoryResponseDTO deleteCategory(
        String token, 
        String verify, 
        String categoryId
        ){
        String userRole = jwtService.getUserRole(token);

        if(!"Admin".equals(userRole)){
            return new CategoryResponseDTO(
                "Only Admin can delete category",
                "Unauthorized"
            );
        }

        if(!"yes".equalsIgnoreCase(verify)){
            return new CategoryResponseDTO(
                "Type yes to delete", 
                "Verify error"
            );
        }

        Optional<CategoryEntity> categoryOptional = categoryRepository.findById(categoryId);
        if(categoryOptional.isEmpty()){
            return new CategoryResponseDTO("The category you’re looking for does not exist.", "Unable to found");
        }
        categoryRepository.deleteById(categoryId);
        return new CategoryResponseDTO("Category deleted successfully", null);
    }

}
