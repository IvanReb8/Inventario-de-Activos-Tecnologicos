package gob.imss.mx.catalogs.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message){
        super(message);
    }

}
