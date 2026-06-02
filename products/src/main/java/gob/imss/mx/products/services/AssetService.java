package gob.imss.mx.products.services;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gob.imss.mx.products.dto.AssetRequestDTO;
import gob.imss.mx.products.dto.ZipResponseDTO;
import gob.imss.mx.products.entities.TechnologyAsset;

public interface AssetService {

    TechnologyAsset registerAsset(AssetRequestDTO dto);
    TechnologyAsset updateAsset(String id, AssetRequestDTO dto);
    TechnologyAsset updateAssetStatus(String id, String newStatus);
    
    Page<TechnologyAsset> findAssets(
            String serial, String model, Long catId, String status, 
            BigDecimal costMin, BigDecimal costMax, Pageable pageable);
            
    ZipResponseDTO generateZipReport(
            String serial, String model, Long catId, String status, 
            BigDecimal costMin, BigDecimal costMax, String requesterUser) throws IOException;

}
