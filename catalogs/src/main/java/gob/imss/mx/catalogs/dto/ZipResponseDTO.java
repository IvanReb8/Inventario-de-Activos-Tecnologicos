package gob.imss.mx.catalogs.dto;

import lombok.Data;

@Data
public class ZipResponseDTO {

    private int status;
    private String message;
    private String fileName;
    private String fileBase64;

    public ZipResponseDTO(int status, String message, String fileName, String fileBase64) {
        this.status = status;
        this.message = message;
        this.fileName = fileName;
        this.fileBase64 = fileBase64;
    }

}
