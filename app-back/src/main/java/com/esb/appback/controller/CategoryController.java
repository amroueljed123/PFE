package com.esb.appback.controller;

import com.esb.appback.model.Category;
import com.esb.appback.repository.CategoryRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/roots")
    public ResponseEntity<List<Category>> getRootCategories() {
        return ResponseEntity.ok(categoryRepository.findByParentIsNull());
    }

    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<Category>> getSubcategories(@PathVariable Long parentId) {
        return categoryRepository.findById(parentId)
                .map(parent -> ResponseEntity.ok(categoryRepository.findByParent(parent)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setIcon(categoryDTO.getIcon() != null ? categoryDTO.getIcon() : "mdi-tag");
        category.setColor(categoryDTO.getColor() != null ? categoryDTO.getColor() : "#2196F3");
        category.setIsCustom(true);
        
        if (categoryDTO.getParentId() != null) {
            categoryRepository.findById(categoryDTO.getParentId()).ifPresent(category::setParent);
        } else {
            category.setRoutePath("/admin/articles/custom/" + category.getName());
        }
        
        Category saved = categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        return categoryRepository.findById(id).map(category -> {
            category.setName(categoryDTO.getName());
            if (categoryDTO.getIcon() != null) category.setIcon(categoryDTO.getIcon());
            if (categoryDTO.getColor() != null) category.setColor(categoryDTO.getColor());
            
            if (categoryDTO.getParentId() != null) {
                categoryRepository.findById(categoryDTO.getParentId()).ifPresent(category::setParent);
            } else {
                category.setParent(null);
            }
            
            return ResponseEntity.ok(categoryRepository.save(category));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class CategoryDTO {
        private String name;
        private String icon;
        private String color;
        private Long parentId;
    }
}
