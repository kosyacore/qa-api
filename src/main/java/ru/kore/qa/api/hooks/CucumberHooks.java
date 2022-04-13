package ru.kore.qa.api.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import ru.kore.qa.api.annotations.PropertiesSource;

@Slf4j
@PropertiesSource({"src/test/resources/config.properties"})
public class CucumberHooks {

    @Before
    public void setUp(Scenario scenario) {
        log.info("Starting \"{}\" test", scenario.getName());
    }
}
