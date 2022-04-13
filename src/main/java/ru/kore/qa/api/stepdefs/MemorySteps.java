package ru.kore.qa.api.stepdefs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.But;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.kore.qa.api.exceptions.AutotestError;
import ru.kore.qa.api.memory.ResponseMemory;
import ru.kore.qa.api.memory.ValueMemory;
import ru.kore.qa.api.utils.JsonGenerator;
import ru.kore.qa.api.utils.RequestManager;
import ru.kore.qa.api.utils.VariableManager;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Random;

@Slf4j
public class MemorySteps {

    @Step
    @And("^create json-file with parameters$")
    public void createApplicationJsonFile(DataTable dataTable) {
        JsonElement json = new JsonObject();
        if (dataTable != null) {
            for (List<String> requestParam : dataTable.asLists()) {
                String path = requestParam.get(0);
                String type = requestParam.get(1).toLowerCase();
                String value = requestParam.get(2);

                switch (type) {
                    case "array":
                        if (path.isEmpty()) {
                            json = new JsonArray();
                        }
                        JsonGenerator.putJson(json, path, "[" + VariableManager.replaceValues(value) + "]");
                        break;
                    case "string":
                        JsonGenerator.putJson(json, path, VariableManager.replaceValues(value));
                        break;
                    case "object":
                        JsonGenerator.putJson(json, path, value);
                        break;
                    case "number":
                        JsonGenerator.putJson(json, path, Long.valueOf(VariableManager.replaceValues(value)));
                        break;
                    case "json":
                        if (path.isEmpty() && VariableManager.hasValue(value)) {
                            json = new JsonParser().parse(VariableManager.replaceValues(value)).getAsJsonObject();
                        } else if (json instanceof JsonObject) {
                            json = new JsonParser().parse(RequestManager.getJsonAsString(value)).getAsJsonObject();
                        } else if (json instanceof JsonArray) {
                            json = new JsonParser().parse(RequestManager.getJsonAsString(value)).getAsJsonArray();
                        }
                        break;
                    case "boolean":
                        JsonGenerator.putJson(json, path, Boolean.valueOf(VariableManager.replaceValues(value)));
                        break;
                    case "null":
                        JsonGenerator.putJson(json, path, "null");
                        break;
                }
            }
            log.debug(json.toString());
            ValueMemory.putValue("jsonBody", json.toString());
        }
    }

    @Step
    @And("^from response body \"([^\"]*)\" got value by JsonPath \"(.*)\" and saved in memory by key \"([^\"]*)\"(| with type \"([^\"]*)\")$")
    @But("^from variable \"([^\"]*)\" got value by JsonPath \"([^\"]*)\" and saved in variable \"([^\"]*)\"(| with type \"([^\"]*)\")$")
    public void saveStringValueFromResponseOrVariable(String receivedVariable, String jPath, String variableName, String format) {
        String value;
        jPath = VariableManager.replaceValues(jPath);
        if (ResponseMemory.isContainsResponse(receivedVariable)) {
            value = ResponseMemory.getResponse(receivedVariable).body().asString();
        } else {
            value = VariableManager.replaceValues(receivedVariable);
        }
        value = new JsonGenerator().createByJsonPath(value, jPath).toString();
        if (format != null) {
            switch (format) {
                case "int":
                    try {
                        value = value.replaceAll("\\D+", "");
                        Number number = NumberFormat.getInstance().parse(value);
                        ValueMemory.putValue(variableName, number.intValue());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        throw new AutotestError(String.format("Ошибка конвертации %s в формат %s", value, format));
                    }
                    return;
                case "boolean":
                    Boolean bool = Boolean.getBoolean(value);
                    ValueMemory.putValue(variableName, bool);
                case "json":
                case "string":
                    break;
            }
        }
        ValueMemory.putValue(variableName, value);
    }

    @Step
    @And("^generated random value with length \"([^\"]*)\" with format \"([^\"]*)\" and saved in variable \"([^\"]*)\"$")
    public void generateAndSaveValueInMemory(int size, String format, String variableName) {
        switch (format) {
            case "name" -> ValueMemory.putValue(variableName, generateName(size));
            case "email" -> ValueMemory.putValue(variableName, generateEmail(size));
        }
    }

    private String generateName(int size) {
        final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        while (stringBuilder.length() < size) {
            int index = (int) (random.nextFloat() * ALLOWED_CHARS.length());
            stringBuilder.append(ALLOWED_CHARS.charAt(index));
        }
        return StringUtils.capitalize(stringBuilder.toString());
    }

    private String generateEmail(int size) {
        final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        while (stringBuilder.length() < size) {
            int index = (int) (random.nextFloat() * ALLOWED_CHARS.length());
            stringBuilder.append(ALLOWED_CHARS.charAt(index));
        }
        return stringBuilder + "@gmail.com";
    }
}
