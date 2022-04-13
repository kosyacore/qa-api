package ru.kore.qa.api.utils;

import lombok.extern.slf4j.Slf4j;
import ru.kore.qa.api.annotations.PropertiesSource;
import ru.kore.qa.api.exceptions.MemoryError;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class PropertyScanner {

    public static void scan(Class<?> clazz) {
        Map<String, String> propertiesMap = new HashMap<>();
        PropertiesSource propertiesSource = clazz.getAnnotation(PropertiesSource.class);
        if (propertiesSource != null) {
            Arrays.stream(propertiesSource.value()).forEach(path -> readPropertiesToMap(path, propertiesMap));
            if (propertiesSource.exportProperties()) {
                propertiesMap.forEach(System::setProperty);
                log.debug("Properties from {} class was exported", clazz.getCanonicalName());
            }
        }
    }

    private static void readPropertiesToMap(String path, Map<String, String> propertiesMap) {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(path)) {
            properties.load(inputStream);
            properties.stringPropertyNames().forEach(
                    key -> {
                        String value = properties.getProperty(key);
                        propertiesMap.put(key, value);
                        log.debug("Added property \"{}\" value = {}", key, value);
                    }
            );
            PropertyManager.load(propertiesMap);
        } catch (Exception e) {
            throw new MemoryError("Can't read properties from path " + path);
        }
    }
}
