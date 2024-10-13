package dev.httpmarco.osgan.networking;

import lombok.extern.log4j.Log4j2;

public final class DefaultClassSupplier implements ClassSupplier{
    @Override
    public Class<?> classByName(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }
}
