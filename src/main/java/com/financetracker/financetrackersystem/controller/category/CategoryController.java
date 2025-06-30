package com.financetracker.financetrackersystem.controller.category;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.financetrackersystem.dto.CategoryRequestDTO;
import com.financetracker.financetrackersystem.dto.CategoryResponseDTO;
import com.financetracker.financetrackersystem.entity.CategoryEntity;
import com.financetracker.financetrackersystem.service.CategoryService;
import com.financetracker.financetrackersystem.service.JWTService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final JWTService jwtService;

    @PostMapping("/createCategory")
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO req,
            HttpServletRequest request) {
        String jwtToken = jwtService.extractJwtFromCookie(request);
        CategoryResponseDTO res = categoryService.createCategory(req, jwtToken);
        if ("Unauthorized".equals(res.getError())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        } else if (res.getError() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/get-category")
    public List<CategoryEntity> getAllCategories(HttpServletRequest req) {
        String userToken = jwtService.extractJwtFromCookie(req);
        return categoryService.getAllCategories(userToken);
    }

    @PutMapping("/update-category/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@RequestBody CategoryRequestDTO req, HttpServletRequest request, @PathVariable String categoryId ){
        String jwt_Token = jwtService.extractJwtFromCookie(request);
        CategoryResponseDTO res = categoryService.updateCategory(req, jwt_Token, categoryId);
        if("Unauthorized".equals(res.getError())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }else if(res.getError()!=null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @DeleteMapping("/delete-category/{categoryId}/{verify}")
    public ResponseEntity<CategoryResponseDTO> deleteCategory(
        HttpServletRequest request, 
        @PathVariable String verify, 
        @PathVariable String categoryId
        ){
        String jwt_Token = jwtService.extractJwtFromCookie(request);
        CategoryResponseDTO res = categoryService.deleteCategory(jwt_Token, verify, categoryId);
        if("Unauthorized".equals(res.getError())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }else if(res.getError()!=null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }



}
