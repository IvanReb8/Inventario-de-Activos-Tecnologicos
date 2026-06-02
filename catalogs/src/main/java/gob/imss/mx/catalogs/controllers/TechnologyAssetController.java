package gob.imss.mx.catalogs.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gob.imss.mx.catalogs.dto.ZipResponseDTO;
import gob.imss.mx.catalogs.entities.TechnologyAsset;
import gob.imss.mx.catalogs.dto.AssetRequestDTO;
import gob.imss.mx.catalogs.services.AssetService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/assets")
@CrossOrigin(origins = "*")
public class TechnologyAssetController {

    private final AssetService assetService;

    public TechnologyAssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping
    public ResponseEntity<TechnologyAsset> createAsset(@Valid @RequestBody AssetRequestDTO dto) {
        TechnologyAsset newAsset = assetService.registerAsset(dto);
        return new ResponseEntity<>(newAsset, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TechnologyAsset> updateAsset(@PathVariable String id, @Valid @RequestBody AssetRequestDTO dto) {
        TechnologyAsset updatedAsset = assetService.updateAsset(id, dto);
        return ResponseEntity.ok(updatedAsset);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TechnologyAsset> updateStatus(@PathVariable String id, @RequestParam String status) {
        TechnologyAsset updatedAsset = assetService.updateAssetStatus(id, status);
        return ResponseEntity.ok(updatedAsset);
    }

    @GetMapping
    public ResponseEntity<Page<TechnologyAsset>> getAssets(
            @RequestParam(required = false) String serialNumber,
            @RequestParam(required = false) String brandModel,
            @RequestParam(required = false) Long idCategory,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal costMin,
            @RequestParam(required = false) BigDecimal costMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "entryDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = buildSort(sortBy, sortDir);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TechnologyAsset> result = assetService.findAssets(serialNumber, brandModel, idCategory, status, costMin, costMax, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/export")
    public ResponseEntity<ZipResponseDTO> exportReport(
            @RequestParam(required = false) String serialNumber,
            @RequestParam(required = false) String brandModel,
            @RequestParam(required = false) Long idCategory,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal costMin,
            @RequestParam(required = false) BigDecimal costMax,
            @RequestParam(defaultValue = "entryDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Principal principal) throws IOException {

        Sort sort = buildSort(sortBy, sortDir);
        String username = (principal != null) ? principal.getName() : "SYSTEM_AUDITOR";
        ZipResponseDTO report = assetService.generateZipReport(serialNumber, brandModel, idCategory, status, costMin, costMax, sort, username);
        return ResponseEntity.ok(report);
    }

    private Sort buildSort(String sortBy, String sortDir) {
        String normalizedDirection = (sortDir == null || !sortDir.equalsIgnoreCase("asc")) ? "desc" : "asc";
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "entryDate";
        }

        switch (sortBy) {
            case "inventoryFolio":
            case "serialNumber":
            case "brandModel":
            case "status":
            case "acquisitionCost":
            case "entryDate":
            case "category.categoryName":
                break;
            default:
                sortBy = "entryDate";
                break;
        }

        return Sort.by(Sort.Direction.fromString(normalizedDirection), sortBy);
    }

}
