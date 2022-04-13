# API testing framework

**Stack:** Java 16, Maven, JUnit 5, Rest-Assured, Cucumber, Allure

**Configuration:**
1. Place your API token in **config.properties** in _test/resources_ folder before running tests

**Run tests:**
mvn clean test allure:serve

This maven command will run all tests and generate Allure report.