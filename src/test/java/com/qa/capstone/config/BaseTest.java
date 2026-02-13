package com.qa.capstone.config;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;

/**
 * Base Test class for all API test classes.
 * Configures REST Assured with base URI, logging, and default settings.
 */
public class BaseTest {

    protected static final String BASE_URI = "https://jsonplaceholder.typicode.com";

    /**
     * Setup method to configure REST Assured before running tests.
     * Sets base URI, enables request and response logging, and configures content type.
     */
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.basePath = "";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        // Set default request specification
        RestAssured.requestSpecification = RestAssured.given()
                .contentType("application/json")
                .accept("application/json");
    }

    /**
     * Reset REST Assured configuration after tests if needed.
     */
    public static void teardown() {
        RestAssured.reset();
    }
}
