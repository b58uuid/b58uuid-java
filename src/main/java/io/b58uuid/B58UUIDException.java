package io.b58uuid;

/**
 * Custom exception class for b58uuid operations.
 * Provides detailed error information for encoding/decoding failures.
 */
public class B58UUIDException extends Exception {
    
    /**
     * Exception type enumeration for different error scenarios.
     */
    public enum ErrorType {
        INVALID_UUID("Invalid UUID format"),
        INVALID_BASE58("Invalid Base58 string"),
        INVALID_LENGTH("Invalid length"),
        ENCODING_ERROR("Encoding error"),
        DECODING_ERROR("Decoding error"),
        OVERFLOW("Arithmetic overflow");
        
        private final String description;
        
        ErrorType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private final ErrorType errorType;
    private final String details;
    
    /**
     * Creates a new B58UUIDException with the specified error type and message.
     *
     * @param errorType The type of error that occurred
     * @param message The error message
     */
    public B58UUIDException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
        this.details = message;
    }
    
    /**
     * Creates a new B58UUIDException with the specified error type, message, and cause.
     *
     * @param errorType The type of error that occurred
     * @param message The error message
     * @param cause The underlying cause of the error
     */
    public B58UUIDException(ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
        this.details = message;
    }
    
    /**
     * Gets the error type for this exception.
     *
     * @return The error type
     */
    public ErrorType getErrorType() {
        return errorType;
    }
    
    /**
     * Gets additional details about the error.
     *
     * @return The error details
     */
    public String getDetails() {
        return details;
    }
    
    @Override
    public String toString() {
        return String.format("B58UUIDException[%s]: %s", errorType.name(), getMessage());
    }
}
