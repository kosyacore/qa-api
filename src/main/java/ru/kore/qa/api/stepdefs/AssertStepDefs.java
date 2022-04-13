package ru.kore.qa.api.stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.But;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import ru.kore.qa.api.exceptions.AutotestError;
import ru.kore.qa.api.memory.ResponseMemory;
import ru.kore.qa.api.utils.JsonGenerator;
import ru.kore.qa.api.utils.RequestManager;
import ru.kore.qa.api.utils.VariableManager;

import java.text.NumberFormat;
import java.text.ParseException;

@Slf4j
public class AssertStepDefs {

    @Step
    @And("^response \"([^\"]*)\" have status code (\\d+)$")
    public void verifyResponseStatusCode(String variableName, int expectedStatusCode) {
        Response response = ResponseMemory.getResponse(variableName);
        log.debug(response.getBody().asString());
        RequestManager.checkStatusCode(response, expectedStatusCode);
    }

    @Step
    @And("^response \"([^\"]*)\" have status code in body (\\d+)$")
    public void verifyResponseStatusCodeInBody(String variableName, int expectedStatusCode) {
        Response response = ResponseMemory.getResponse(variableName);
        log.debug(response.getBody().asString());
        String statusCodeFromResponse = new JsonGenerator().createByJsonPath(response.body().asString(), "$.code").toString();
        checkValueInMemory(statusCodeFromResponse, "equals", String.valueOf(expectedStatusCode), "int");
    }

    @Step
    @But("^in response body \"([^\"]*)\" value by JsonPath \"([^\"]*)\" (equals|not equals|contains|dont contains) \"(.*)\" with type \"([^\"]*)\"$")
    @And("^in variable \"([^\"]*)\" value by JsonPath \"([^\"]*)\" (equals|not equals|contains|dont contains) \"(.*)\" with type \"([^\"]*)\"$")
    public void checkValueInMemoryByJsonPath(String variableName, String jPath, String math, String expectedVariable, String format) {
        String value;
        expectedVariable = VariableManager.replaceValues(expectedVariable);
        jPath = VariableManager.replaceValues(jPath);
        if (ResponseMemory.isContainsResponse(variableName)) {
            value = ResponseMemory.getResponse(variableName).body().asString();
        } else {
            value = VariableManager.replaceValues(variableName);
        }
        value = new JsonGenerator().createByJsonPath(value, jPath).toString();
        checkValueInMemory(value, math, expectedVariable, format);
    }

    @Step
    @But("^in response body with key \"([^\"]*)\" value (equals|not equals|contains|dont contains) \"(.*)\" with type \"([^\"]*)\"$")
    @And("^in variable \"([^\"]*)\" value (equals|not equals|contains|dont contains) \"(.*)\" with type \"([^\"]*)\"$")
    public void checkValueInMemory(String variableName, String math, String expectedVariable, String format) {
        String value;
        expectedVariable = VariableManager.replaceValues(expectedVariable);
        if (ResponseMemory.isContainsResponse(variableName)) {
            value = ResponseMemory.getResponse(variableName).body().asString();
        } else {
            value = VariableManager.replaceValues(variableName);
        }
        switch (math) {
            case "equals" -> Assertions.assertTrue(checkEquals(format, value, expectedVariable),
                    String.format("value \"%s\" from variable / response body \"%s\" not equals with value \"%s\"", value, variableName, expectedVariable));
            case "not equals" -> Assertions.assertFalse(checkEquals(format, value, expectedVariable),
                    String.format("value \"%s\" from variable / response body \"%s\" equals with value \"%s\"", value, variableName, expectedVariable));
            case "contains" -> Assertions.assertTrue(checkContains(format, value, expectedVariable),
                    String.format("value \"%s\" from variable / response body \"%s\" dont contains value \"%s\"", value, variableName, expectedVariable));
            case "not contains" -> Assertions.assertFalse(checkContains(format, value, expectedVariable),
                    String.format("value \"%s\" from variable / response body \"%s\" contains value \"%s\"", value, variableName, expectedVariable));
        }
    }

    private boolean checkEquals(String format, String actualValue, String expectedVariable) {
        switch (format.toLowerCase()) {
            case "json":
                return new JsonGenerator().createByString(actualValue).equals(expectedVariable);
            case "int":
            case "number":
                try {
                    Number expected = NumberFormat.getInstance().parse(expectedVariable);
                    Number actual = NumberFormat.getInstance().parse(actualValue);
                    return expected.equals(actual);
                } catch (ParseException e) {
                    e.printStackTrace();
                    throw new AutotestError(String.format("Error with casting value \"%s\" and \"%s\" in type \"%s\"", actualValue, expectedVariable, format));
                }
            case "string":
                return expectedVariable.equals(actualValue);
            case "bool":
            case "boolean":
                Boolean actualBoolean = Boolean.valueOf(actualValue);
                Boolean expectedBoolean = Boolean.valueOf(expectedVariable);
                return expectedBoolean.equals(actualBoolean);
            default:
                Assertions.fail("Type check with \"" + format + "\" not implemented");
                return false;
        }
    }

    private boolean checkContains(String format, String actual, String expected) {
        switch (format.toLowerCase()) {
            case "json":
                return new JsonGenerator().createByString(actual).contains(expected);
            case "string":
                return actual.contains(expected);
            default:
                Assertions.fail("Type check with \"" + format + "\" not implemented");
                return false;
        }
    }
}
