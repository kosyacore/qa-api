package ru.kore.qa.api.memory;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ValueMemory {

    private static Map<String, Object> memory = new HashMap<>();

    public static Object getValue(String key) {
        String errorMessage = String.format("Variable with key \"%s\" doesn't exist in memory", key);
        Assertions.assertTrue(isHaveValue(key), errorMessage);
        return memory.get(key);
    }

    public static void putValue(String key, Object value) {
        memory.put(key, value);
        log.debug("Saved value \"{}\" in memory by key {}", value, key);
    }

    public static boolean isHaveValue(String key) {
        return memory.containsKey(key);
    }
}
