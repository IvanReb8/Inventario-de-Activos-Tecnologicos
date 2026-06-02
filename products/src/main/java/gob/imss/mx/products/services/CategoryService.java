package gob.imss.mx.products.services;

import java.util.List;

import gob.imss.mx.products.entities.Category;

public interface CategoryService {
    List<Category> listAllCategories();
    Category findCategoryById(Long id);
}
