package kz.narxoz.backend.exception;

public class AccessDeniiedBusinessException extends RuntimeException {
    public AccessDeniiedBusinessException(String message) {
        super(message);
    }
}