package gob.imss.mx.products.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gob.imss.mx.products.entities.Category;
import gob.imss.mx.products.entities.TechnologyAsset;

@Repository
public interface TechnologyAssetRepository extends JpaRepository<TechnologyAsset, String> {

    long countByCategory(Category category);

    boolean existsBySerialNumberAndIdTechnicalNot(String serialNumber, String idTechnical);
    boolean existsBySerialNumber(String serialNumber);

    @Query("SELECT ta FROM TechnologyAsset ta WHERE " +
           "(:serial IS NULL OR LOWER(ta.serialNumber) LIKE LOWER(CONCAT('%', :serial, '%'))) AND " +
           "(:model IS NULL OR LOWER(ta.brandModel) LIKE LOWER(CONCAT('%', :model, '%'))) AND " +
           "(:catId IS NULL OR ta.category.idCategory = :catId) AND " +
           "(:status IS NULL OR ta.status = :status) AND " +
           "(:costMin IS NULL OR ta.acquisitionCost >= :costMin) AND " +
           "(:costMax IS NULL OR ta.acquisitionCost <= :costMax)")
    Page<TechnologyAsset> findWithFilters(
            @Param("serial") String serial, @Param("model") String model,
            @Param("catId") Long catId, @Param("status") String status,
            @Param("costMin") BigDecimal costMin, @Param("costMax") BigDecimal costMax,
            Pageable pageable);

    @Query("SELECT ta FROM TechnologyAsset ta WHERE " +
           "(:serial IS NULL OR LOWER(ta.serialNumber) LIKE LOWER(CONCAT('%', :serial, '%'))) AND " +
           "(:model IS NULL OR LOWER(ta.brandModel) LIKE LOWER(CONCAT('%', :model, '%'))) AND " +
           "(:catId IS NULL OR ta.category.idCategory = :catId) AND " +
           "(:status IS NULL OR ta.status = :status) AND " +
           "(:costMin IS NULL OR ta.acquisitionCost >= :costMin) AND " +
           "(:costMax IS NULL OR ta.acquisitionCost <= :costMax)")
    List<TechnologyAsset> listForReport(
            @Param("serial") String serial, @Param("model") String model,
            @Param("catId") Long catId, @Param("status") String status,
            @Param("costMin") BigDecimal costMin, @Param("costMax") BigDecimal costMax);

}
