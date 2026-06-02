package gob.imss.mx.catalogs.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gob.imss.mx.catalogs.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Permite buscar categorías por su código para validaciones o inicializaciones
    Optional<Category> findByPrefixCode(String prefixCode);
}
