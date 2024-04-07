package dev.httpmarco.osgan.utils.types;

import dev.httpmarco.osgan.utils.Patterns;
import dev.httpmarco.osgan.utils.RandomUtils;
import dev.httpmarco.osgan.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


public final class MessageUtils {

    private static final String ALPHABET = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String randomString(int length) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt(RandomUtils.getRandomNumber(ALPHABET.length())));
        }

        return builder.toString();
    }

    public static int countMatches(@NotNull final CharSequence str, final char ch) {
        if (str.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (ch == str.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    public static String replaceLast(String string, String substring, String replacement) {
        int index = string.lastIndexOf(substring);

        if (index == -1) {
            return string;
        }

        return string.substring(0, index) + replacement + string.substring(index + substring.length());
    }

    public static boolean isValidDouble(String string) {
        if (string == null || string.isEmpty()) {
            return false;
        }

        return Patterns.DOUBLE_PATTERN.matcher(string).matches();
    }

    public static boolean isValidInteger(String string) {
        if (string == null || string.isEmpty()) {
            return false;
        }

        return NumberUtils.isParsable(string);
    }

    public static boolean isAlphabeticWithNumbers(String string) {
        return isAlphabeticWithNumbers(string);
    }

    public static boolean isAlphabeticWithNumbers(String string, String... allowedChars) {
        List<String> list = Arrays.asList(allowedChars);

        for (int i = 0, n = string.length(); i < n; i++) {
            char charAt = string.charAt(i);

            if (!list.contains(String.valueOf(charAt))) {
                if (!Character.isDigit(charAt) && !Character.isAlphabetic(charAt)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isAlphabetic(String string) {
        return isAlphabetic(string);
    }

    public static boolean isAlphabetic(String string, String... allowedChars) {
        List<String> list = Arrays.asList(allowedChars);

        for (int i = 0, n = string.length(); i < n; i++) {
            char charAt = string.charAt(i);

            if (!list.contains(String.valueOf(charAt))) {
                if (!Character.isAlphabetic(charAt)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isValidDate(String dateToCheck) {
        return isValidDate(dateToCheck, Utils.DATE_FORMAT);
    }

    public static boolean isValidDateAndTime(String dateToCheck) {
        return isValidDate(dateToCheck, Utils.DATE_AND_TIME_FORMAT);
    }

    public static boolean isValidDateAndTimeWithSeconds(String dateToCheck) {
        return isValidDate(dateToCheck, Utils.DATE_AND_TIME_FORMAT_WITH_SECONDS);
    }

    public static boolean isValidDate(String dateToCheck, String dateFormat) {
        return isValidDate(dateToCheck, new SimpleDateFormat(dateFormat));
    }

    public static boolean isValidDate(String dateToCheck, SimpleDateFormat dateFormat) {
        dateFormat.setLenient(false);

        try {
            dateFormat.parse(dateToCheck.trim());
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    public static long convertDurationString(String durationStr) {
        TimeUnit timeUnit;

        if (durationStr.endsWith("d")) {
            durationStr = durationStr.replace("d", "");
            timeUnit = TimeUnit.DAYS;
        } else if (durationStr.endsWith("h")) {
            durationStr = durationStr.replace("h", "");
            timeUnit = TimeUnit.HOURS;
        } else if (durationStr.endsWith("m")) {
            durationStr = durationStr.replace("m", "");
            timeUnit = TimeUnit.MINUTES;
        } else if (durationStr.endsWith("s")) {
            durationStr = durationStr.replace("s", "");
            timeUnit = TimeUnit.SECONDS;
        } else {
            return -1;
        }

        if (isValidInteger(durationStr)) {
            int duration = Integer.parseInt(durationStr);

            return timeUnit.toMillis(duration);
        } else {
            return -1;
        }
    }

    public static String getDateFromMillis(long millis) {
        return getDateFromMillis(millis, Utils.DATE_FORMAT);
    }

    public static String getDateAndTimeFromMillis(long millis) {
        return getDateFromMillis(millis, Utils.DATE_AND_TIME_FORMAT);
    }

    public static String getDateAndTimeWithSecondsFromMillis(long millis) {
        return getDateFromMillis(millis, Utils.DATE_AND_TIME_FORMAT_WITH_SECONDS);
    }

    public static String getDateFromMillis(long millis, String dateFormat) {
        return getDateFromMillis(millis, new SimpleDateFormat(dateFormat));
    }

    public static String getDateFromMillis(long millis, SimpleDateFormat dateFormat) {
        if (millis == -1) {
            return "§4PERMANENT";
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        Date date = calendar.getTime();

        return dateFormat.format(date);
    }

    public static long dateToMillis(String dateString) {
        return dateToMillis(dateString, Utils.DATE_FORMAT);
    }

    public static long dateAndTimeToMillis(String dateString) {
        return dateToMillis(dateString, Utils.DATE_AND_TIME_FORMAT);
    }

    public static long dateAndTimeWithSecondsToMillis(String dateString) {
        return dateToMillis(dateString, Utils.DATE_AND_TIME_FORMAT_WITH_SECONDS);
    }

    public static long dateToMillis(String dateString, String dateFormat) {
        return dateToMillis(dateString, new SimpleDateFormat(dateFormat));
    }

    public static long dateToMillis(String dateString, SimpleDateFormat dateFormat) {
        Date date;

        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            return System.currentTimeMillis();
        }

        return date.getTime();
    }

    public static String argsToString(String[] args, int startingFrom) {
        StringBuilder builder = new StringBuilder();

        for (int i = startingFrom; i < args.length; i++) {
            if (i > startingFrom) {
                builder.append(" ");
            }

            builder.append(args[startingFrom]);
        }

        return builder.toString();
    }

    public static String getPercent(int amount, int percentOf) {
        return getPercent(amount, percentOf, 1, true);
    }

    public static String getPercent(int amount, int percentOf, int maxDigits, boolean hundredIsMax) {
        if (amount == 0 || percentOf == 0) {
            return "0%";
        }

        if (amount > percentOf && hundredIsMax) {
            return "100%";
        }

        NumberFormat format = NumberFormat.getInstance();
        double percent = ((double) amount / (double) percentOf) * 100;

        format.setMaximumFractionDigits(maxDigits);

        return format.format(percent) + "%";
    }

    public static String formatNumber(long value) {
        return NumberFormat.getInstance().format(value).replace(",", ".");
    }

    public static String formatNumber(double value) {
        return NumberFormat.getInstance().format(value);
    }
}
