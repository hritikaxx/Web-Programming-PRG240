package com.example.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FEATURES_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Cucumber test runner — discovers and runs all .feature files.
 *
 * Configuration:
 * - FEATURES: where to find .feature files (src/test/resources/features)
 * - GLUE: where to find step definition classes (com.example.cucumber package)
 * - PLUGIN: output format (pretty prints to console)
 */
@Suite
@IncludeEngines("cucumber")
@SelectPackages("com.example.cucumber")
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "src/test/resources/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class RunCucumberTest {
    // This class is just a runner — Cucumber uses the annotations above
    // to find features and step definitions automatically.
}
