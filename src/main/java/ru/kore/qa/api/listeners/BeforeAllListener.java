package ru.kore.qa.api.listeners;

import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestRunStarted;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import ru.kore.qa.api.hooks.CucumberHooks;
import ru.kore.qa.api.utils.PropertyScanner;

@Slf4j
public class BeforeAllListener implements EventListener {

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestRunStarted.class, event -> {
            log.debug("Setting up environment");
            PropertyScanner.scan(CucumberHooks.class);
            log.debug("Properties loaded");
            RestAssured.filters(new AllureRestAssured());
            log.debug("Added Allure Rest-Assured filter");
        });
    }
}
