package com.brightway.brightway_dropout.util;

public class TypeSafeUtils {
    public static Integer safeToInteger(Object[] array, int index) {
        if (array == null || index >= array.length || array[index] == null) {
            return 0;
        }
        Object value = array[index];
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static Double safeToDouble(Object[] array, int index) {
        if (array == null || index >= array.length || array[index] == null) {
            return 0.0;
        }
        Object value = array[index];
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static String safeToString(Object[] array, int index) {
        if (array == null || index >= array.length || array[index] == null) {
            return "";
        }
        return array[index].toString();
    }
}
