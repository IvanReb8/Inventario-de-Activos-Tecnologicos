package gob.imss.mx.catalogs.services;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import gob.imss.mx.catalogs.dto.ZipResponseDTO;
import gob.imss.mx.catalogs.entities.TechnologyAsset;
import gob.imss.mx.catalogs.dto.AssetRequestDTO;

public interface AssetService {

    TechnologyAsset registerAsset(AssetRequestDTO dto);
    TechnologyAsset updateAsset(String id, AssetRequestDTO dto);
    TechnologyAsset updateAssetStatus(String id, String newStatus);
    
    Page<TechnologyAsset> findAssets(
            String serial, String model, Long catId, String status, 
            BigDecimal costMin, BigDecimal costMax, Pageable pageable);
            
    ZipResponseDTO generateZipReport(
            String serial, String model, Long catId, String status, 
            BigDecimal costMin, BigDecimal costMax, Sort sort, String requesterUser) throws IOException;

}
