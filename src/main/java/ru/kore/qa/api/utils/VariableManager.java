package ru.kore.qa.api.utils;

import lombok.extern.slf4j.Slf4j;
import ru.kore.qa.api.memory.ValueMemory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class VariableManager {

    private static String replacePattern = "\\{([^{}]+)}";

    public static String replaceValues(String value) {
        log.debug("Search for variable replace {}", value);
        String resultString = value;
        Pattern p = Pattern.compile(replacePattern);
        Matcher m = p.matcher(value);
        while (m.find()) {
            String rawMath = m.group();
            String valueToReplace = search(rawMath.substring(1, rawMath.length() - 1));
            if (valueToReplace != null) {
                resultString = customReplaceFirst(resultString, rawMath, valueToReplace);
            } else {
                continue;
            }
            String matchString = m.replaceFirst("");
            m = p.matcher(matchString);
        }
        log.debug("Variable {} changed on value {}.", value, resultString);
        return resultString;
    }

    private static String search(String key) {
        String result;
        result = searchInValueMemory(key);
        if (result == null) {
            result = searchInProperty(key);
        }
        return result;
    }

    private static String searchInValueMemory(String key) {
        if (ValueMemory.isHaveValue(key)) {
            return String.valueOf(ValueMemory.getValue(key));
        } else return null;
    }

    private static String searchInProperty(String key) {
        try {
            return PropertyManager.get(key);
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static Boolean hasValue(String key) {
        Pattern p = Pattern.compile(replacePattern);
        Matcher m = p.matcher(key);
        if (m.matches()) {
            key = key.substring(1, key.length() - 1);
        }
        if (key != null) {
            return true;
        }
        return false;
    }

    protected static String customReplaceFirst(String resultString, String rawMath, String valueToReplace) {
        return resultString.substring(0, resultString.indexOf(rawMath))
                + valueToReplace
                + resultString.substring(resultString.indexOf(rawMath) + rawMath.length());
    }
}
