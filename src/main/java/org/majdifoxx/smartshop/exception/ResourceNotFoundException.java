package org.majdifoxx.smartshop.exception;

/**
 * Thrown when a requested resource doesn't exist (HTTP 404)
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
