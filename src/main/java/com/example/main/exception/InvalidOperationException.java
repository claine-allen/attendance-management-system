package com.example.main.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to indicate that an operation is invalid or not allowed
 * given the current state of the resource or system.
 * This exception will automatically result in an HTTP 400 Bad Request response.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // Maps this exception to HTTP 400 Bad Request status
public class InvalidOperationException extends RuntimeException{
    /**
     * Constructs a new InvalidOperationException with the specified detail message.
     * @param message The detail message.
     */
    public InvalidOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidOperationException with the specified detail message and cause.
     * @param message The detail message.
     * @param cause The cause (which is saved for later retrieval by the getCause() method).
     */
    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
