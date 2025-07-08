package com.example.main.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to indicate that a requested resource was not found.
 * This exception will automatically result in an HTTP 404 Not Found response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) 
// Maps this exception to HTTP 404 status
public class ResourceNotFoundException extends RuntimeException{

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     * @param message The detail message.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     * @param message The detail message.
     * @param cause The cause (which is saved for later retrieval by the getCause() method).
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
