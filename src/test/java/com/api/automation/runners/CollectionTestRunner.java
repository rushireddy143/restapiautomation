package com.api.automation.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Dedicated test runner for Postman collection automation
 * Focuses on collection-specific scenarios and endpoints
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.api.automation.stepdefinitions", "com.api.automation.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/collection-html",
                "json:target/cucumber-reports/collection-cucumber.json",
                "junit:target/cucumber-reports/collection-cucumber.xml",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@collection",
        monochrome = true,
        publish = true
)
public class CollectionTestRunner {
    // This class remains empty - it's just a runner for collection tests
}

/**
 * Collection smoke test runner - runs only collection smoke tests
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.api.automation.stepdefinitions", "com.api.automation.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/collection-smoke-html",
                "json:target/cucumber-reports/collection-smoke.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@collection and @smoke",
        monochrome = true
)
class CollectionSmokeTestRunner {
}

/**
 * Collection workflow test runner - runs end-to-end workflow tests
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.api.automation.stepdefinitions", "com.api.automation.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/collection-workflow-html",
                "json:target/cucumber-reports/collection-workflow.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@collection and @workflow",
        monochrome = true
)
class CollectionWorkflowTestRunner {
}

/**
 * Collection comprehensive test runner - runs all collection scenarios
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.api.automation.stepdefinitions", "com.api.automation.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/collection-comprehensive-html",
                "json:target/cucumber-reports/collection-comprehensive.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@collection and @comprehensive",
        monochrome = true
)
class CollectionComprehensiveTestRunner {
}
