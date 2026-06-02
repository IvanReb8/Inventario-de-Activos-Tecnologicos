package gob.imss.mx.catalogs.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetRequestDTO {

    @NotBlank(message = "El número de serie es obligatorio")
    private String serialNumber;

    @NotBlank(message = "La marca y modelo son obligatorios")
    private String brandModel;

    @NotBlank(message = "El estado es obligatorio")
    private String status;

    @NotNull(message = "El costo de adquisición es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El costo no puede ser negativo")
    private BigDecimal acquisitionCost;

    @NotNull(message = "La categoría es obligatoria")
    private Long idCategory;

}
