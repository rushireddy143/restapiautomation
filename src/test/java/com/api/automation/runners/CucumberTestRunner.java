package com.api.automation.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Cucumber test runner for BDD scenarios
 * Configures feature files, step definitions, and reporting
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.api.automation.stepdefinitions", "com.api.automation.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/html",
                "json:target/cucumber-reports/cucumber.json",
                "junit:target/cucumber-reports/cucumber.xml",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@smoke or @regression",
        monochrome = true,
        publish = true
)
public class CucumberTestRunner {
    // This class remains empty - it's just a runner
}

/**
 * Smoke test runner - runs only smoke tests
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.api.automation.stepdefinitions", "com.api.automation.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/smoke-html",
                "json:target/cucumber-reports/smoke-cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@smoke",
        monochrome = true
)
class SmokeTestRunner {
}

/**
 * Regression test runner - runs regression tests
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.api.automation.stepdefinitions", "com.api.automation.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/regression-html",
                "json:target/cucumber-reports/regression-cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@regression",
        monochrome = true
)
class RegressionTestRunner {
}

/**
 * Negative test runner - runs negative test scenarios
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.api.automation.stepdefinitions", "com.api.automation.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/negative-html",
                "json:target/cucumber-reports/negative-cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@negative",
        monochrome = true
)
class NegativeTestRunner {
}

/**
 * Performance test runner - runs performance test scenarios
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.api.automation.stepdefinitions", "com.api.automation.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/performance-html",
                "json:target/cucumber-reports/performance-cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@performance",
        monochrome = true
)
class PerformanceTestRunner {
}

/**
 * Security test runner - runs security test scenarios
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.api.automation.stepdefinitions", "com.api.automation.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/security-html",
                "json:target/cucumber-reports/security-cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@security",
        monochrome = true
)
class SecurityTestRunner {
}
