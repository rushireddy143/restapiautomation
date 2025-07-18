Feature: User Management API
  As an API consumer
  I want to manage users through the API
  So that I can perform CRUD operations on user data

  Background:
    Given the API base URL is configured
    And I have valid authentication credentials

  @smoke @regression
  Scenario: Get all users successfully
    Given I have a valid API endpoint "/api/users"
    When I send a GET request with page "2"
    Then the response status code should be 200
    And the response should contain user data
    And the response time should be less than 3000 milliseconds

  @regression
  Scenario: Create a new user with valid data
    Given I have a valid API endpoint "/api/users"
    And I have user data:
      | name     | job               |
      | John Doe | Software Engineer |
    When I send a POST request with the user data
    Then the response status code should be 201
    And the response should contain the created user details
    And the response should have a valid user ID
    And the response should have a creation timestamp

  @regression
  Scenario Outline: Update user information
    Given I have a valid API endpoint "/api/users/<userId>"
    And I have updated user data:
      | name   | job   |
      | <name> | <job> |
    When I send a PUT request with the updated data
    Then the response status code should be 200
    And the response should contain the updated user details
    And the user name should be "<name>"
    And the user job should be "<job>"

    Examples:
      | userId | name        | job                    |
      | 2      | Jane Smith  | Senior Developer       |
      | 3      | Bob Johnson | Product Manager        |
      | 4      | Alice Brown | QA Engineer           |

  @regression
  Scenario: Partially update user job
    Given I have a valid API endpoint "/api/users/2"
    And I have partial update data:
      | job |
      | Lead QA Engineer |
    When I send a PATCH request with the partial data
    Then the response status code should be 200
    And the response should contain the updated job title
    And the job should be "Lead QA Engineer"

  @regression
  Scenario: Delete a user
    Given I have a valid API endpoint "/api/users/2"
    When I send a DELETE request
    Then the response status code should be 204
    And the response body should be empty

  @negative
  Scenario: Get user with invalid ID
    Given I have a valid API endpoint "/api/users/999"
    When I send a GET request
    Then the response status code should be 404
    And the response should contain an error message

  @negative
  Scenario: Create user with missing required fields
    Given I have a valid API endpoint "/api/users"
    And I have incomplete user data:
      | name |
      | John |
    When I send a POST request with the incomplete data
    Then the response status code should be 400
    And the response should contain validation errors

  @security
  Scenario: Access protected endpoint without authentication
    Given I have a valid API endpoint "/api/users"
    And I do not have authentication credentials
    When I send a GET request without authentication
    Then the response status code should be 401 or 403
    And the response should contain an authentication error

  @performance
  Scenario: API response time performance
    Given I have a valid API endpoint "/api/users"
    When I send multiple GET requests concurrently
    Then all responses should have status code 200
    And the average response time should be less than 2000 milliseconds
    And no response should take more than 5000 milliseconds

  @datadriven
  Scenario: Create multiple users with different data
    Given I have a valid API endpoint "/api/users"
    When I create users with the following data:
      | name          | job                |
      | Alice Johnson | Frontend Developer |
      | Bob Smith     | Backend Developer  |
      | Carol Brown   | DevOps Engineer    |
      | David Wilson  | Data Scientist     |
    Then all users should be created successfully
    And each user should have a unique ID
    And each user should have the correct name and job
