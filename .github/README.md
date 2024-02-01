## Reflections

![Release Version](https://img.shields.io/maven-central/v/dev.httpmarco.reflections/reflections)

// TODO REWORK

### Dependencies
```kts
Only maven central snapshots
```


### Detector

The **Detector** functionality in the provided Java code snippet demonstrates the usage of a reflection-based approach to dynamically detect and retrieve classes within a specified package. The code snippet below illustrates the detection process, where the package name and a filter predicate are provided as parameters to the `ReflectionDetector.detect` method.

```java
// Example: Detect classes in the package "dev.example.package" that satisfy a custom filter predicate
List<Class<?>> classes = ReflectionDetector.detect("dev.example.package", customFilterPredicate);
```

In this example, the `customFilterPredicate` is a lambda expression or method reference that defines the conditions for selecting classes during the detection process. Users can tailor this predicate according to their specific criteria, allowing for a flexible and customized class detection mechanism.

### Allocate

The **Allocate** functionality showcases the dynamic instantiation of a class using reflection, providing a versatile means of object creation at runtime. In the code snippet below, the `ReflectionClassAllocater.allocate` method is employed to instantiate an object of a specified class.

```java
// Example: Dynamically allocate an object of a custom class (e.g., CustomClass)
CustomClass object = ReflectionClassAllocater.allocate(CustomClass.class);
```

Here, users can substitute the `CustomClass` with the desired class type they intend to instantiate dynamically. This approach allows for the creation of objects without a predefined instantiation code, adding flexibility to the program's structure and design.