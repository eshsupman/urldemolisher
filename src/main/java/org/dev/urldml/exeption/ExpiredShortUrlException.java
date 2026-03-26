package org.dev.urldml.exeption;

public class ExpiredShortUrlException extends RuntimeException {
    public ExpiredShortUrlException(String message) {
        super(message);
    }
}
