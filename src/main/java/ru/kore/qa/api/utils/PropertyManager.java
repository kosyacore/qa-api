package ru.kore.qa.api.utils;

import java.util.HashMap;
import java.util.Map;

public class PropertyManager {

    private static Map<String, String> propertiesMap = new HashMap<>();

    public static void load(Map<String, String> propertiesMap) {
        if (null != propertiesMap) {
            PropertyManager.propertiesMap = propertiesMap;
        }
    }

    public static String get(String propertyName) {
        return propertiesMap.get(propertyName);
    }

    public static boolean containsKey(String propertyName) {
        return propertiesMap.containsKey(propertyName);
    }
}
