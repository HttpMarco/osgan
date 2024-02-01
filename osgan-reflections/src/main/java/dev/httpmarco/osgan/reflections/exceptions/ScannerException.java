package dev.httpmarco.osgan.reflections.exceptions;

import javax.management.RuntimeErrorException;

public class ScannerException extends RuntimeErrorException {
    public ScannerException(Error e) {
        super(e);
    }
}
