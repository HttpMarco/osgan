package dev.httpmarco.osgan.networking;

public interface ClassSupplier {
    Class<?> classByName(String name) throws ClassNotFoundException;
}
