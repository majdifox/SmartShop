package org.majdifoxx.smartshop.exception;

/**
 * Thrown when user lacks permission (HTTP 403)
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
