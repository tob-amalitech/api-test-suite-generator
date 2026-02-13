package com.qa.capstone.tests;

import com.qa.capstone.config.BaseTest;
import com.qa.capstone.models.Post;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * REST Assured test suite for JSONPlaceholder API Posts endpoint.
 * Tests positive scenarios, negative scenarios, and edge cases.
 */
@DisplayName("Posts API Test Suite")
public class PostsAPITest extends BaseTest {

    private static final String POSTS_ENDPOINT = "/posts";
    private static final String POSTS_ID_ENDPOINT = "/posts/{id}";
    private static final int VALID_POST_ID = 1;
    private static final int NON_EXISTENT_POST_ID = 999999;
    private static final int VALID_USER_ID = 1;
    private static final int TOTAL_POSTS = 100;

    /**
     * Initialize test setup before running tests.
     */
    @BeforeAll
    public static void initTest() {
        setup();
    }

    // ===================== POSITIVE TEST CASES =====================

    /**
     * Test Case 1: GET All Posts
     * Verifies that all 100 posts are returned with valid status code
     */
    @Test
    @DisplayName("GET All Posts - Verify 200 response and 100 posts returned")
    public void testGetAllPosts() {
        given()
                .when()
                .get(POSTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("size()", equalTo(TOTAL_POSTS))
                .body("[0].userId", notNullValue())
                .body("[0].id", notNullValue())
                .body("[0].title", notNullValue())
                .body("[0].body", notNullValue());
    }

    /**
     * Test Case 2: GET Single Post
     * Verifies individual post retrieval and response structure
     */
    @Test
    @DisplayName("GET Single Post - Verify 200 response and valid post structure")
    public void testGetSinglePost() {
        given()
                .when()
                .get(POSTS_ID_ENDPOINT, VALID_POST_ID)
                .then()
                .statusCode(200)
                .body("userId", equalTo(1))
                .body("id", equalTo(VALID_POST_ID))
                .body("title", notNullValue())
                .body("body", notNullValue())
                .body("title", isA(String.class))
                .body("body", isA(String.class));
    }

    /**
     * Test Case 3: GET Posts by User
     * Verifies filtering functionality by userId parameter
     */
    @Test
    @DisplayName("GET Posts by User - Verify filtering works correctly")
    public void testGetPostsByUser() {
        given()
                .queryParam("userId", VALID_USER_ID)
                .when()
                .get(POSTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].userId", equalTo(VALID_USER_ID))
                .body("[1].userId", equalTo(VALID_USER_ID));
    }

    /**
     * Test Case 4: CREATE New Post
     * Verifies POST operation and response contains generated ID
     */
    @Test
    @DisplayName("POST New Post - Verify 201 response and ID generation")
    public void testCreateNewPost() {
        Post newPost = new Post(1, "Test Title", "Test Body Content");

        given()
                .body(newPost)
                .when()
                .post(POSTS_ENDPOINT)
                .then()
                .statusCode(201)
                .body("userId", equalTo(1))
                .body("title", equalTo("Test Title"))
                .body("body", equalTo("Test Body Content"))
                .body("id", notNullValue())
                .body("id", isA(Integer.class));
    }

    /**
     * Test Case 5: UPDATE Post (PUT)
     * Verifies full update of an existing post
     */
    @Test
    @DisplayName("PUT Post - Verify 200 response and fields updated")
    public void testUpdatePost() {
        Post updatedPost = new Post(1, 1, "Updated Title", "Updated Body Content");

        given()
                .body(updatedPost)
                .when()
                .put(POSTS_ID_ENDPOINT, VALID_POST_ID)
                .then()
                .statusCode(200)
                .body("id", equalTo(VALID_POST_ID))
                .body("userId", equalTo(1))
                .body("title", equalTo("Updated Title"))
                .body("body", equalTo("Updated Body Content"));
    }

    /**
     * Test Case 6: PARTIAL UPDATE Post (PATCH)
     * Verifies partial update with only some fields changed
     */
    @Test
    @DisplayName("PATCH Post - Verify 200 response and partial update")
    public void testPatchPost() {
        Post patchPost = new Post();
        patchPost.setTitle("Partially Updated Title");

        given()
                .body(patchPost)
                .when()
                .patch(POSTS_ID_ENDPOINT, VALID_POST_ID)
                .then()
                .statusCode(200)
                .body("title", equalTo("Partially Updated Title"))
                .body("id", notNullValue());
    }

    /**
     * Test Case 7: DELETE Post
     * Verifies deletion of a post returns success
     */
    @Test
    @DisplayName("DELETE Post - Verify 200 response")
    public void testDeletePost() {
        given()
                .when()
                .delete(POSTS_ID_ENDPOINT, VALID_POST_ID)
                .then()
                .statusCode(200);
    }

    // ===================== NEGATIVE TEST CASES =====================

    /**
     * Test Case 8: GET Non-existent Post
     * Verifies graceful handling of non-existent resource
     */
    @Test
    @DisplayName("GET Non-existent Post - Verify graceful handling")
    public void testGetNonExistentPost() {
        given()
                .when()
                .get(POSTS_ID_ENDPOINT, NON_EXISTENT_POST_ID)
                .then()
                .statusCode(404);
    }

    /**
     * Test Case 9: CREATE Post with Empty Body
     * Verifies API response when creating post with minimal/empty data
     */
    @Test
    @DisplayName("POST with Empty Body - Verify API handling")
    public void testCreatePostWithEmptyBody() {
        String emptyBody = "{}";

        Response response = given()
                .body(emptyBody)
                .when()
                .post(POSTS_ENDPOINT)
                .then()
                .statusCode(anyOf(equalTo(200), equalTo(201), equalTo(400)))
                .extract()
                .response();

        // Verify response has an ID even with empty body (JSONPlaceholder behavior)
        assert response.getStatusCode() == 201 || response.getStatusCode() == 200;
    }

    /**
     * Test Case 10: GET with Invalid Query Parameters
     * Verifies API still returns data even with unsupported parameters
     */
    @Test
    @DisplayName("GET with Invalid Params - Verify API resilience")
    public void testInvalidQueryParams() {
        given()
                .queryParam("invalidParam", "invalidValue")
                .queryParam("randomKey", "randomValue")
                .when()
                .get(POSTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("size()", equalTo(TOTAL_POSTS));
    }

    // ===================== EDGE CASE TEST CASES =====================

    /**
     * Test Case 11: Boundary ID Values
     * Verifies behavior with edge case ID values
     */
    @Test
    @DisplayName("GET with Boundary IDs - Verify edge case handling")
    public void testBoundaryIds() {
        // Test ID = 1 (first post)
        given()
                .when()
                .get(POSTS_ID_ENDPOINT, 1)
                .then()
                .statusCode(200)
                .body("id", equalTo(1));

        // Test ID = 100 (last post)
        given()
                .when()
                .get(POSTS_ID_ENDPOINT, 100)
                .then()
                .statusCode(200)
                .body("id", equalTo(100));

        // Test ID = 0 (boundary - does not exist)
        given()
                .when()
                .get(POSTS_ID_ENDPOINT, 0)
                .then()
                .statusCode(404);
    }

    /**
     * Test Case 12: Special Characters in Request
     * Verifies proper encoding and handling of special characters
     */
    @Test
    @DisplayName("POST with Special Characters - Verify encoding/escaping")
    public void testSpecialCharacters() {
        String titleWithSpecialChars = "Test Title with Special Chars: !@#$%^&*()_+-=[]{}|;:',.<>?/~`";
        String bodyWithSpecialChars = "Body with quotes \"test\" and apostrophe it's working. \n New line and tabs \t are included.";

        Post postWithSpecialChars = new Post(1, titleWithSpecialChars, bodyWithSpecialChars);

        given()
                .body(postWithSpecialChars)
                .when()
                .post(POSTS_ENDPOINT)
                .then()
                .statusCode(201)
                .body("title", equalTo(titleWithSpecialChars))
                .body("body", equalTo(bodyWithSpecialChars));
    }

    /**
     * Test Case 13: Large Payload
     * Verifies handling of very long strings in request body
     */
    @Test
    @DisplayName("POST with Large Payload - Verify size limit handling")
    public void testLargePayload() {
        // Create a very long string (1000 characters)
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longString.append("This is a test string. ");
        }

        Post largePost = new Post(1, "Large Title - " + longString.substring(0, 100), longString.toString());

        given()
                .body(largePost)
                .when()
                .post(POSTS_ENDPOINT)
                .then()
                .statusCode(anyOf(equalTo(201), equalTo(200)))
                .body("id", notNullValue())
                .body("body", notNullValue());
    }

    /**
     * Test Case 14: Multiple Posts Verification
     * Verifies consistency across multiple post retrievals
     */
    @Test
    @DisplayName("GET Multiple Posts - Verify all have required fields")
    public void testMultiplePostsConsistency() {
        given()
                .when()
                .get(POSTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("every { it.userId != null }", is(true))
                .body("every { it.id != null }", is(true))
                .body("every { it.title != null }", is(true))
                .body("every { it.body != null }", is(true));
    }

    /**
     * Test Case 15: Response Time Performance
     * Verifies that API responds within acceptable timeframe
     */
    @Test
    @DisplayName("GET All Posts - Verify response time performance")
    public void testResponseTimePerformance() {
        given()
                .when()
                .get(POSTS_ENDPOINT)
                .then()
                .statusCode(200)
                .time(lessThan(5000L)); // Response should be within 5 seconds
    }
}
