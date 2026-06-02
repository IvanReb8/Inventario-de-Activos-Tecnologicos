package gob.imss.mx.products.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "technology_assets")
@Data
public class TechnologyAsset {

    @Id
    @Column(name = "id_technical", length = 36)
    private String idTechnical;

    @Column(name = "inventory_folio", unique = true, nullable = false)
    private String inventoryFolio;

    @Column(name = "serial_number", unique = true, nullable = false)
    private String serialNumber;

    @Column(name = "brand_model", nullable = false)
    private String brandModel;

    @Column(nullable = false)
    private String status;

    @Column(name = "acquisition_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal acquisitionCost;

    @Column(name = "entry_date", nullable = false, updatable = false)
    private LocalDateTime entryDate;

    @ManyToOne
    @JoinColumn(name = "id_category", nullable = false)
    private Category category;

    @PrePersist
    protected void onCreate() {
        this.idTechnical = UUID.randomUUID().toString();
        this.entryDate = LocalDateTime.now();
    }

}
