package com.example.main.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to indicate that an attempt was made to create a resource
 * that already exists (e.g., unique constraint violation).
 * This exception will automatically result in an HTTP 409 Conflict response.
 */
@ResponseStatus(HttpStatus.CONFLICT) // Maps this exception to HTTP 409 Conflict status
public class DuplicateEntryException extends RuntimeException{

    /**
     * Constructs a new DuplicateEntryException with the specified detail message.
     * @param message The detail message.
     */
    public DuplicateEntryException(String message) {
        super(message);
    }

    /**
     * Constructs a new DuplicateEntryException with the specified detail message and cause.
     * @param message The detail message.
     * @param cause The cause (which is saved for later retrieval by the getCause() method).
     */
    public DuplicateEntryException(String message, Throwable cause) {
        super(message, cause);
    }
}
