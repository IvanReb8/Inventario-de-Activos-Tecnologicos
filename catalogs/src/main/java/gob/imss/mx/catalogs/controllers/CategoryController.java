package gob.imss.mx.catalogs.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gob.imss.mx.catalogs.services.CategoryService;
import gob.imss.mx.catalogs.entities.Category;

@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin(origins = "*") // Habilita la comunicación con el cliente Angular
public class CategoryController {

    private final CategoryService categoryService;

    // Inyección de dependencias mediante constructor (Buenas prácticas evaluadas)
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * GET /api/categories
     * Retorna la lista completa de categorías disponibles para llenar los catálogos del sistema.
     * Acceso: Público o Autenticado (Dependiendo de la configuración de seguridad).
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.listAllCategories();
        
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 si la tabla está vacía
        }
        
        return ResponseEntity.ok(categories); // Retorna 200 OK con el arreglo JSON
    }

    /**
     * GET /api/categories/{id}
     * Recupera una categoría específica mediante su ID numérico.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.findCategoryById(id);
        return ResponseEntity.ok(category);
    }

}
