package dev.httpmarco.osgan.reflections;

import dev.httpmarco.osgan.reflections.exceptions.ScannerException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassScanner {

    private final ClassLoader loader;
    private final String packageName; // current separator
    private final List<Predicate<Class<?>>> criteria = new ArrayList<>();

    public static ClassScanner of(ClassLoader loader, String packageName) {
        return new ClassScanner(loader, packageName);
    }

    public ClassScanner criteria(Predicate<Class<?>> criteria) {
        this.criteria.add(criteria);
        return this;
    }

    @SneakyThrows
    public Collection<Class<?>> scan() {
        var replacedPath = packageName.replace('.', '/');
        var packageUri = Objects.requireNonNull(loader.getResource(replacedPath)).toURI();
        var classes = new ArrayList<Class<?>>();

        Path path;
        if (packageUri.toString().startsWith("jar:")) {
            try {
                path = FileSystems.getFileSystem(packageUri).getPath(replacedPath);
            } catch (FileSystemNotFoundException e) {
                try {
                    path = FileSystems.newFileSystem(packageUri, Collections.emptyMap()).getPath(replacedPath);
                } catch (IOException ex) {
                    throw new ScannerException(new Error("Error during package scan: " + e.getMessage()));
                }
            }
        } else {
            path = Paths.get(packageUri);
        }
        try (Stream<Path> allPaths = Files.walk(path)) {
            allPaths.filter(Files::isRegularFile).forEach(file -> {
                String absolutePath = file.toString().replace(File.separator, ".").replace('/', '.');
                String className = absolutePath.substring(absolutePath.indexOf(packageName), absolutePath.length() - 6);
                try {
                    Class<?> clazz = loader.loadClass(className);
                    if (criteria.isEmpty() || criteria.stream().allMatch(classPredicate -> classPredicate.test(clazz))) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException | StringIndexOutOfBoundsException | NoClassDefFoundError e) {
                    System.out.println("Error whilst loading class '" + className + "' (exception: " + e.getClass().getName() + ") Ignoring class!");
                }
            });
        } catch (IOException e) {
            throw new ScannerException(new Error("Error during package scan: " + e.getMessage()));
        }
        return classes;
    }
}
