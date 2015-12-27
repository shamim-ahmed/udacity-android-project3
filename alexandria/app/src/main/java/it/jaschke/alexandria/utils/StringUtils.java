package it.jaschke.alexandria.utils;

public class StringUtils {
    public static boolean isBlank(String inputStr) {
        return inputStr == null || inputStr.trim().equals(Constants.EMPTY_STRING);
    }

    private StringUtils() {
    }
}
