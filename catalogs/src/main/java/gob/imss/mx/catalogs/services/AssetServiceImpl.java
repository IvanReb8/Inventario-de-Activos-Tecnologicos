package gob.imss.mx.catalogs.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gob.imss.mx.catalogs.dto.ZipResponseDTO;
import gob.imss.mx.catalogs.entities.TechnologyAsset;
import gob.imss.mx.catalogs.exceptions.BadRequestException;
import gob.imss.mx.catalogs.exceptions.NotFoundResourceException;
import gob.imss.mx.catalogs.repositories.TechnologyAssetRepository;
import gob.imss.mx.catalogs.dto.AssetRequestDTO;
import gob.imss.mx.catalogs.entities.Category;
import gob.imss.mx.catalogs.repositories.CategoryRepository;

@Service
public class AssetServiceImpl implements AssetService {

    private final TechnologyAssetRepository assetRepository;
    private final CategoryRepository categoryRepository;

    public AssetServiceImpl(TechnologyAssetRepository assetRepository, CategoryRepository categoryRepository) {
        this.assetRepository = assetRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public TechnologyAsset registerAsset(AssetRequestDTO dto) {
       if (assetRepository.existsBySerialNumber(dto.getSerialNumber())) {
            throw new BadRequestException("El número de serie ya se encuentra registrado.");
        }

        Category category = categoryRepository.findById(dto.getIdCategory())
                .orElseThrow(() -> new NotFoundResourceException("Categoría no encontrada."));

        TechnologyAsset asset = new TechnologyAsset();
        asset.setSerialNumber(dto.getSerialNumber());
        asset.setBrandModel(dto.getBrandModel());
        asset.setStatus(dto.getStatus());
        asset.setAcquisitionCost(dto.getAcquisitionCost());
        asset.setCategory(category);
        asset.setInventoryFolio(generateFolio(category));

        return assetRepository.save(asset);
    }

    @Override
    @Transactional
    public TechnologyAsset updateAsset(String id, AssetRequestDTO dto) {
        TechnologyAsset asset = assetRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourceException("Activo no encontrado."));

        if (assetRepository.existsBySerialNumberAndIdTechnicalNot(dto.getSerialNumber(), id)) {
            throw new BadRequestException("El número de serie ya pertenece a otro activo.");
        }

        if ("DISPOSED".equalsIgnoreCase(asset.getStatus()) && !"DISPOSED".equalsIgnoreCase(dto.getStatus())) {
            throw new BadRequestException("Un activo con estado Baja no podrá regresar a un estado operativo posterior.");
        }

        asset.setSerialNumber(dto.getSerialNumber());
        asset.setBrandModel(dto.getBrandModel());
        asset.setStatus(dto.getStatus());
        asset.setAcquisitionCost(dto.getAcquisitionCost());

        return assetRepository.save(asset);
    }

    @Override
    @Transactional
    public TechnologyAsset updateAssetStatus(String id, String newStatus) {
        TechnologyAsset asset = assetRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourceException("Activo no encontrado."));

        if ("DISPOSED".equalsIgnoreCase(asset.getStatus())) {
            throw new BadRequestException("Un activo con estado Baja no podrá regresar a un estado operativo posterior.");
        }

        asset.setStatus(newStatus);
        return assetRepository.save(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TechnologyAsset> findAssets(String serial, String model, Long catId, String status, BigDecimal costMin,
            BigDecimal costMax, Pageable pageable) {
        return assetRepository.findWithFilters(serial, model, catId, status, costMin, costMax, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ZipResponseDTO generateZipReport(String serial, String model, Long catId, String status, BigDecimal costMin,
            BigDecimal costMax, Sort sort, String requesterUser) throws IOException {
       List<TechnologyAsset> assets = assetRepository.listForReport(serial, model, catId, status, costMin, costMax, sort);
        ByteArrayOutputStream zipBos = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(zipBos)) {
            
            // 📊 1. Generar Excel en Memoria
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Assets Inventory");
            Row header = sheet.createRow(0);
            String[] headers = {"Folio", "Serial Number", "Brand & Model", "Status", "Cost"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            int r = 1;
            for (TechnologyAsset asset : assets) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(asset.getInventoryFolio());
                row.createCell(1).setCellValue(asset.getSerialNumber());
                row.createCell(2).setCellValue(asset.getBrandModel());
                row.createCell(3).setCellValue(asset.getStatus());
                row.createCell(4).setCellValue(asset.getAcquisitionCost().doubleValue());
            }

            ByteArrayOutputStream excelBos = new ByteArrayOutputStream();
            workbook.write(excelBos);
            workbook.close();

            ZipEntry excelEntry = new ZipEntry("assets_report.xlsx");
            zos.putNextEntry(excelEntry);
            zos.write(excelBos.toByteArray());
            zos.closeEntry();

            // 📝 2. Generar TXT de Auditoría
            String auditContent = "Fecha y hora de generación: " + LocalDateTime.now() + "\n" +
                                 "Usuario solicitante: " + requesterUser + "\n" +
                                 "Total de registros exportados: " + assets.size();

            ZipEntry txtEntry = new ZipEntry("audit_log.txt");
            zos.putNextEntry(txtEntry);
            zos.write(auditContent.getBytes());
            zos.closeEntry();
        }

        String base64Zip = Base64.getEncoder().encodeToString(zipBos.toByteArray());
        return new ZipResponseDTO(HttpStatus.OK.value(), "Reporte generado correctamente", "inventario.zip", base64Zip);
    }

    private String generateFolio(Category category) {
        long currentConsecutive = assetRepository.countByCategory(category) + 1;
        return String.format("%s-%d-%03d", category.getPrefixCode(), Year.now().getValue(), currentConsecutive);
    }

}
