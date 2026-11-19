package com.rumi.safety.application;

/**
 * A requested resource does not exist. The REST layer maps it to 404.
 */
public abstract class ResourceNotFoundException extends RuntimeException {

    protected ResourceNotFoundException(String message) {
        super(message);
    }
}
