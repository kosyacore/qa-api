#language: en

@GET
@REGRESSION
Feature: GET request

  Scenario: Get user
    * make GET request on URL "{base.url}/{url.path}/2934650" and save response by key "1"
    * response "1" have status code in body 200
    * in response body "1" value by JsonPath "$.data.id" equals "2934650" with type "int"
    * in response body "1" value by JsonPath "$.data.name" equals "Dipesh Kocchar" with type "string"
    * in response body "1" value by JsonPath "$.data.email" equals "kocchar_dipesh@luettgen.test" with type "string"
    * in response body "1" value by JsonPath "$.data.gender" equals "female" with type "string"
    * in response body "1" value by JsonPath "$.data.status" equals "inactive" with type "string"