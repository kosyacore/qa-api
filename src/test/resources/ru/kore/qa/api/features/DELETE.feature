#language: en

@POST
@REGRESSION
Feature: DELETE request

  Scenario: Create user and delete
    * generated random value with length "8" with format "email" and saved in variable "generatedEmail"
    * generated random value with length "5" with format "name" and saved in variable "generatedName"

    * create json-file with parameters
      | email  | string | {generatedEmail} |
      | name   | string | {generatedName}  |
      | gender | string | male             |
      | status | string | active           |

    * make POST request on URL "{base.url}/{url.path}" with headers and parameters from table and save response by key "1"
      | ACCESS_TOKEN | Authorization | {auth.token} |
      | BODY         | {jsonBody}    | {jsonBody}   |

    * response "1" have status code in body 201
    * in response body "1" value by JsonPath "$.data.name" equals "{generatedName}" with type "string"
    * in response body "1" value by JsonPath "$.data.email" equals "{generatedEmail}" with type "string"
    * in response body "1" value by JsonPath "$.data.gender" equals "male" with type "string"
    * in response body "1" value by JsonPath "$.data.status" equals "active" with type "string"

    * from response body "1" got value by JsonPath ".data.id" and saved in memory by key "userId" with type "int"

    * make DELETE request on URL "{base.url}/{url.path}/{userId}" with headers and parameters from table and save response by key "3"
      | ACCESS_TOKEN | Authorization | {auth.token} |
    * response "3" have status code in body 204