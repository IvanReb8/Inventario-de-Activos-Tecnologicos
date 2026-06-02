package gob.imss.mx.catalogs.services;

import java.util.List;

import gob.imss.mx.catalogs.entities.Category;

public interface CategoryService {
    List<Category> listAllCategories();
    Category findCategoryById(Long id);
}
