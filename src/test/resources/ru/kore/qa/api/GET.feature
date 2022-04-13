#language: en

@GET
@REGRESSION
Feature: GET request

  Scenario: Get user
    * make GET request on URL "{base.url}/{url.path}/3193" and save response by key "1"
    * response "1" have status code in body 200
    * in response body "1" value by JsonPath "$.data.id" equals "3193" with type "int"
    * in response body "1" value by JsonPath "$.data.name" equals "Purushottam Guneta" with type "string"
    * in response body "1" value by JsonPath "$.data.email" equals "guneta_purushottam@pagac.co" with type "string"
    * in response body "1" value by JsonPath "$.data.gender" equals "female" with type "string"
    * in response body "1" value by JsonPath "$.data.status" equals "active" with type "string"