package dev.httpmarco.osgan.utils.types;

import java.util.List;

public final class ListUtils {

    public static <T> List<T> append(List<T> list, T element) {
        list.add(element);
        return list;
    }
}
