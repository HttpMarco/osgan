package dev.httpmarco.osgan.utils;

import java.util.regex.Pattern;

public class Patterns {
    /**
     * Pattern for a double. Source: <a href="https://stackoverflow.com/a/16078719/13034634">...</a>
     */

    public static final Pattern DOUBLE_PATTERN = Pattern.compile(
            "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
                    "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" +
                    "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
                    "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");

    /**
     * Pattern for a {@link java.util.UUID}. Source: <a href="https://www.code4copy.com/java/validate-uuid-string-java/">...</a>
     */
    public static final Pattern UUID_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

}