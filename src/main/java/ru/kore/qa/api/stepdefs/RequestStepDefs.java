package ru.kore.qa.api.stepdefs;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.qameta.allure.Step;
import io.restassured.http.Method;
import io.restassured.response.Response;
import ru.kore.qa.api.memory.ResponseMemory;
import ru.kore.qa.api.utils.RequestManager;

public class RequestStepDefs {

    @Step
    @And("^make (GET|PUT|POST|DELETE|HEAD|TRACE|OPTIONS|PATCH) request on URL \"([^\"]*)\"$")
    public void sendHttpRequestWithoutParams(Method method, String address) {
        RequestManager.sendRequest(method, address, null);
    }

    @Step
    @And("^make (GET|PUT|POST|DELETE|HEAD|TRACE|OPTIONS|PATCH) request on URL \"([^\"]*)\" and save response by key \"([^\"]*)\"$")
    public void sendHttpRequestWithoutParamsAndSave(Method method, String address, String saveVariable) {
        Response response = RequestManager.sendRequest(method, address, null);
        ResponseMemory.saveResponse(saveVariable, response);
    }

    @Step
    @And("^make (GET|PUT|POST|DELETE|HEAD|TRACE|OPTIONS|PATCH) request on URL \"([^\"]*)\" with headers and parameters from table and save response by key \"([^\"]*)\"$")
    public void sendHttpRequestSaveResponseAndSave(Method method, String address, String saveVariable, DataTable dataTable) {
        Response response = RequestManager.sendRequest(method, address, dataTable);
        ResponseMemory.saveResponse(saveVariable, response);
    }
}
