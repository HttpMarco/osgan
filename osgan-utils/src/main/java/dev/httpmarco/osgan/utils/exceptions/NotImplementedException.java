package dev.httpmarco.osgan.utils.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotImplementedException extends RuntimeException {

    public NotImplementedException(String message) {
        super(message);
    }
}
