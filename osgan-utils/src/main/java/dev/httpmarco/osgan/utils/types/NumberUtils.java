package dev.httpmarco.osgan.utils.types;

public class NumberUtils {

    public static boolean isNumber(Class<?> clazz) {
        return Number.class.isAssignableFrom(clazz);
    }

    public static boolean isParsable(final String str) {
        if (str.isEmpty() || str.charAt(str.length() - 1) == '.') {
            return false;
        }
        if (str.charAt(0) == '-') {
            return str.length() > 1 && withDecimalsParsing(str, 1);
        }
        return withDecimalsParsing(str, 0);
    }

    public static boolean withDecimalsParsing(final String str, final int beginIdx) {
        var decimal = 0;
        for (var i = beginIdx; i < str.length(); i++) {
            var currentChar = str.charAt(i);
            if (currentChar == '.') {
                if (++decimal > 1) {
                    return false;
                }
            } else if (!Character.isDigit(currentChar)) {
                return false;
            }
        }
        return true;
    }
}