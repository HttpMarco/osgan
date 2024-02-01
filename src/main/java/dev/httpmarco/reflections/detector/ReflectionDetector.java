package dev.httpmarco.reflections.detector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ReflectionDetector {

    public static List<Class<?>> detect(String packageName, Predicate<Class<?>>... filters) {
        var implementingClasses = new ArrayList<Class<?>>();
        var path = packageName.replace('.', '/');
        var classLoader = Thread.currentThread().getContextClassLoader();
        var packageDir = new File(classLoader.getResource(path).getFile());

        if (packageDir.exists()) {
            for (var file : Objects.requireNonNull(packageDir.listFiles())) {
                if (file.isFile() && file.getName().endsWith(".class")) {
                    var className = packageName + "." + file.getName().replace(".class", "");

                    try {
                        var cls = Class.forName(className);
                        if (Arrays.stream(filters).allMatch(classPredicate -> classPredicate.test(cls))) {
                            implementingClasses.add(cls);
                        }
                    } catch (ClassNotFoundException ignore) {
                    }
                }
            }
        }
        return implementingClasses;
    }
}
