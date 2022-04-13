package ru.kore.qa.api.utils;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.jayway.jsonpath.JsonPath;
import ru.kore.qa.api.exceptions.AutotestError;

import java.io.StringReader;

public class JsonGenerator {

    private JsonElement jsonElement = null;

    public JsonGenerator createByString(String stringJson) {
        try {
            jsonElement = new Gson().fromJson(stringJson, JsonElement.class);
        } catch (JsonSyntaxException exception) {
            jsonElement = new Gson().fromJson("\"" + stringJson + "\"", JsonElement.class);
        }
        return this;
    }

    public JsonGenerator createByJsonElement(JsonElement jsonElement) {
        this.jsonElement = jsonElement;
        return this;
    }

    public JsonGenerator createByJsonPath(String json, String jPath) {
        String str = new Gson().toJson((Object) JsonPath.read(json, jPath));
        return createByString(str);
    }

    public static void putJson(JsonElement json, String path, Object value) {
        if (json instanceof JsonObject) {
            putJson((JsonObject) json, path, value);
        } else if (json instanceof JsonArray) {
            putJson((JsonArray) json, path, value);
        }
    }

    public static JsonObject putJson(JsonObject json, String path, Object value) {
        String[] namePath = path.split("\\.");
        if (namePath.length == 1) {
            if (value instanceof String) {
                putJsonString(json, path, (String) value);
            } else if (value instanceof Number) {
                putJsonNumber(json, path, (Number) value);
            } else if (value instanceof Boolean) {
                putJsonBoolean(json, path, (Boolean) value);
            }
            return json;
        }
        JsonObject jsonT = new JsonObject();
        if (json.has(namePath[0])) {
            if (json.get(namePath[0]) instanceof JsonPrimitive) {
                jsonT.addProperty(namePath[0], json.getAsJsonPrimitive(namePath[0]).toString());
            }
            if (json.get(namePath[0]) instanceof JsonArray) {
                json.add(namePath[0], putJson(json.get(namePath[0]).getAsJsonArray(), cutFirstNamePath(path), value));
                return json;
            } else {
                jsonT = json.get(namePath[0]).getAsJsonObject();
            }
            json.add(namePath[0], putJson(jsonT, cutFirstNamePath(path), value));
        } else {
            jsonT = new JsonObject();
            json.add(namePath[0], putJson(jsonT, cutFirstNamePath(path), value));
        }
        return json;
    }

    public static JsonArray putJson(JsonArray jsonArr, String path, Object value) {
        String[] namePath = path.split("\\.");
        JsonObject json = new JsonObject();
        if (namePath.length == 1) {
            if (value instanceof String) {
                putJsonString(json, path, (String) value);
                jsonArr.add(json);
            } else if (value instanceof Number) {
                putJsonNumber(json, path, (Number) value);
                jsonArr.add(json);
            } else if (value instanceof Boolean) {
                putJsonBoolean(json, path, (Boolean) value);
                jsonArr.add(json);
            }
            return jsonArr;
        }
        String pattern = "\\[\\d+\\]";
        if (namePath[0].matches(pattern)) {
            int number = Integer.parseInt(namePath[0].substring(1, namePath[0].length() - 1));
            if (jsonArr.size() > number) {
                putJson(jsonArr.get(number).getAsJsonObject(), cutFirstNamePath(path), value);
            } else {
                putJson(jsonArr, cutFirstNamePath(path), value);
            }
        } else {
            putJson(jsonArr, cutFirstNamePath(path), value);
        }
        return jsonArr;
    }

    private static String cutFirstNamePath(String value) {
        return value.substring(value.indexOf(".") + 1);
    }

    private static void putJsonString(JsonObject json, String name, String value) {
        JsonElement element;
        JsonReader valueReader = new JsonReader(new StringReader(value));
        valueReader.setLenient(true);
        try {
            element = new JsonParser().parse(valueReader);
            if ("".equals(value) || element instanceof JsonPrimitive) {
                json.addProperty(name, value);
            } else {
                json.add(name, new JsonParser().parse(value));
            }
        } catch (JsonSyntaxException e) {
            if (value.contains("{") && value.contains("}")) {
                json.addProperty(name, value);
                return;
            }
            if (value.contains(" ")) {
                json.addProperty(name, String.format("\"%s\"", value));
                return;
            }
            throw new AutotestError(e.getMessage());
        }
    }

    private static void putJsonNumber(JsonObject json, String name, Number value) {
        json.addProperty(name, value);
    }

    private static void putJsonBoolean(JsonObject json, String name, Boolean value) {
        json.addProperty(name, value);
    }

    public JsonElement getJsonElement() {
        return this.jsonElement;
    }

    public boolean equals(String expected) {
        return equals(new JsonGenerator().createByString(expected));
    }

    public boolean equals(JsonElement expected) {
        return this.jsonElement.equals(expected);
    }

    public boolean equals(JsonGenerator expected) {
        return this.jsonElement.equals(expected.getJsonElement());
    }

    public boolean contains(String expected) {
        return contains(new JsonGenerator().createByString(expected));
    }

    public boolean contains(JsonGenerator expectedCustomJson) {
        return contains(expectedCustomJson.getJsonElement());
    }

    public boolean contains(JsonElement expected) {
        if (expected instanceof JsonPrimitive) {
            return this.contains(expected.getAsJsonPrimitive());
        } else if (expected instanceof JsonObject) {
            return this.contains(expected.getAsJsonObject());
        } else if (expected instanceof JsonArray) {
            return this.contains(expected.getAsJsonArray());
        }
        return jsonElement.equals(expected);
    }

    @Override
    public String toString() {
        if (jsonElement instanceof JsonPrimitive) {
            return jsonElement.getAsString();
        }
        return jsonElement.toString();
    }
}
