package com.rumi.safety.integration;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Runs the Gherkin scenarios of src/test/resources/com/rumi/safety. Every scenario is tagged with its user story.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com/rumi/safety")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.rumi.safety.integration")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, summary")
public class SafetyCucumberTest {
}
