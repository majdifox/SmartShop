package org.majdifoxx.smartshop.exception;

/**
 * Thrown when business rules are violated (HTTP 422)
 * Examples: insufficient stock, order already confirmed, cash limit exceeded
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
