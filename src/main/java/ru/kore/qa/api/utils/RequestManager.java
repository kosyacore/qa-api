package ru.kore.qa.api.utils;

import io.cucumber.datatable.DataTable;
import io.restassured.http.Method;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.response.Response;
import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static io.restassured.RestAssured.given;

@Slf4j
public class RequestManager {

    public static Response sendRequest(Method method, String url, DataTable dataTable) {
        url = VariableManager.replaceValues(url);
        log.debug("REQUEST URL: " + url);
        RequestSender request = createRequest(dataTable);
        log.debug("REQUEST QUERY: " + ((RequestSpecificationImpl) request).getQueryParams());
        log.debug("REQUEST BODY: " + ((RequestSpecificationImpl) request).getBody());
        Response response = request.request(method, url);
        log.debug("RESPONSE BODY:\n" + response.getBody().asString());
        return response;
    }

    public static RequestSender createRequest(DataTable dataTable) {
        String body;
        RequestSpecification request = given().contentType("application/json");
        if (dataTable != null) {
            for (List<String> requestParam : dataTable.asLists()) {
                String type = requestParam.get(0);
                String name = requestParam.get(1);
                String value = VariableManager.replaceValues(requestParam.get(2));
                switch (type.toUpperCase()) {
                    case "ACCESS_TOKEN" -> request.header(HttpHeaders.AUTHORIZATION, "Bearer " + value);
                    case "PARAMETER" -> request.queryParam(name, value);
                    case "FORM_PARAMETER" -> request.formParam(name, value);
                    case "PATH_PARAMETER" -> request.pathParam(name, value);
                    case "HEADER" -> request.header(name, value);
                    case "BODY" -> {
                        name = name.toLowerCase();
                        if (name.equals("Json") || name.equals("json")) {
                            body = getJsonAsString(value);
                            request.body(body);
                        } else {
                            body = value;
                            request.body(body);
                        }
                    }
                    default -> throw new IllegalArgumentException(String.format("Некорректно задан тип %s для параметра запроса %s ", type, name));
                }
            }
        }
        return request;
    }

    public static String getJsonAsString(String jsonName) {
        String path = "API_files/" + jsonName;
        InputStream is = RequestManager.class.getClassLoader().getResourceAsStream(path);
        StringWriter stringWriter = new StringWriter();
        try {
            IOUtils.copy(Objects.requireNonNull(is), stringWriter, String.valueOf(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return stringWriter.toString();
    }

    public static void checkStatusCode(Response response, int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    public static void getStatusCode(Response response) {
        response.getStatusCode();
    }
}
