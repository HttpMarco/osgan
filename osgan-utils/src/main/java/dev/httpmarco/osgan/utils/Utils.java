package dev.httpmarco.osgan.utils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

public class Utils {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public static final SimpleDateFormat DATE_AND_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public static final SimpleDateFormat DATE_AND_TIME_FORMAT_WITH_SECONDS = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static final Random RANDOM = new Random();

    public static final List<Class<?>> JAVA_ELEMENTS = List.of(String.class, Integer.class, Boolean.class, Short.class, Float.class, Byte.class, Double.class, Long.class);

    public static final String LINE_BREAK_ELEMENT = System.lineSeparator();

    public static final String SEMICOLON = ";";
    public static final String DOUBLETHINK = ":";
    public static final String SPACER = " ";
    public static final String EMTPY_STRING = "";

}
