package ru.kore.qa.api.memory;

import io.restassured.response.Response;
import ru.kore.qa.api.exceptions.MemoryError;

import java.util.HashMap;
import java.util.Map;

public class ResponseMemory {

    private static Map<String, Response> memory = new HashMap<>();

    public static void saveResponse(String responseName, Response response) {
        memory.put(responseName, response);
    }

    public static Response getResponse(String responseName) {
        if (isContainsResponse(responseName)) {
            return memory.get(responseName);
        } else {
            throw new MemoryError("ResponseMemory don't have value with key " + responseName);
        }
    }

    public static boolean isContainsResponse(String responseName) {
        return memory.containsKey(responseName);
    }
}
