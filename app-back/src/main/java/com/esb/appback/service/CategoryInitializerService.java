package com.esb.appback.service;

import com.esb.appback.model.Category;
import com.esb.appback.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CategoryInitializerService {

    @Autowired
    private CategoryRepository categoryRepository;

    @PostConstruct
    public void init() {
        if (categoryRepository.count() == 0) {
            List<Category> defaultCategories = Arrays.asList(
                createDefaultCategory("Ordinateurs", "mdi-desktop-classic", "blue", "/admin/articles/ordinateurs"),
                createDefaultCategory("Moniteurs", "mdi-monitor", "indigo", "/admin/articles/moniteurs"),
                createDefaultCategory("Périphériques", "mdi-usb", "purple", "/admin/articles/peripheriques"),
                createDefaultCategory("Imprimantes", "mdi-printer", "teal", "/admin/articles/imprimantes"),
                createDefaultCategory("Téléphones", "mdi-phone", "green", "/admin/articles/telephones"),
                createDefaultCategory("Cartouches", "mdi-printer-pos", "orange", "/admin/articles/cartouches"),
                createDefaultCategory("Consommables", "mdi-package-variant", "amber", "/admin/articles/consommables"),
                createDefaultCategory("Bureaux", "mdi-desk", "brown", "/admin/articles/bureaux"),
                createDefaultCategory("Chaises", "mdi-seat", "red", "/admin/articles/chaises"),
                createDefaultCategory("Meubles", "mdi-wardrobe", "deep-orange", "/admin/articles/meubles"),
                createDefaultCategory("Étagères", "mdi-bookshelf", "grey", "/admin/articles/etageres")
            );
            categoryRepository.saveAll(defaultCategories);
            
            // Add some initial subcategories for Ordinateurs
            categoryRepository.findByName("Ordinateurs").ifPresent(parent -> {
                categoryRepository.save(createSubCategory("Fixe", "mdi-desktop-tower", "blue", parent));
                categoryRepository.save(createSubCategory("Portable", "mdi-laptop", "green", parent));
                categoryRepository.save(createSubCategory("Serveur", "mdi-server", "red", parent));
            });

            System.out.println("Default categories have been initialized !");
        }
    }

    private Category createDefaultCategory(String name, String icon, String color, String route) {
        Category cat = new Category();
        cat.setName(name);
        cat.setIcon(icon);
        cat.setColor(color);
        cat.setRoutePath(route);
        cat.setIsCustom(false);
        return cat;
    }

    private Category createSubCategory(String name, String icon, String color, Category parent) {
        Category cat = new Category();
        cat.setName(name);
        cat.setIcon(icon);
        cat.setColor(color);
        cat.setParent(parent);
        cat.setIsCustom(true);
        return cat;
    }
}
