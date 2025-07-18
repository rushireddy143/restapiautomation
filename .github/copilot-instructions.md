<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# REST Assured API Automation Framework

This is a comprehensive API automation testing framework built with REST Assured, TestNG, and Maven.

## Framework Guidelines

### Code Structure
- Follow the Page Object Model pattern for API endpoints
- Use the BaseTest class for common setup and utilities
- Implement proper separation of concerns between test data, test logic, and configurations
- Use meaningful test method names that describe the test scenario

### Testing Best Practices
- Always use TestNG groups to categorize tests (smoke, regression, performance, negative, etc.)
- Include proper assertions for status codes, response body validation, and response time
- Use Allure annotations for better test reporting (@Epic, @Feature, @Story, @Description)
- Implement data-driven testing using JSON test data files
- Follow the AAA pattern: Arrange, Act, Assert

### REST Assured Usage
- Use RequestSpecification and ResponseSpecification for consistent API calls
- Leverage JsonPath for response validation and data extraction
- Implement proper authentication mechanisms (Bearer token, Basic auth)
- Use proper HTTP methods (GET, POST, PUT, DELETE) with appropriate request/response validation

### Configuration Management
- Use environment-specific property files (dev.properties, staging.properties, prod.properties)
- Implement ConfigManager for centralized configuration access
- Support multiple environments through Maven profiles and system properties

### Error Handling
- Implement comprehensive negative test scenarios
- Validate error responses and status codes
- Use proper exception handling in utility methods
- Log meaningful error messages for debugging

### Reporting and Logging
- Use Allure for comprehensive test reporting
- Implement Log4j2 for structured logging
- Include request/response details in logs for debugging
- Generate reports for CI/CD integration

### Test Data Management
- Store test data in JSON files under src/test/resources/testdata/
- Use JsonUtils for test data manipulation
- Implement data factories for dynamic test data generation
- Keep test data separate from test logic

When generating code for this framework:
- Follow Java naming conventions and best practices
- Include proper JavaDoc comments for public methods
- Use meaningful variable and method names
- Implement proper error handling and logging
- Follow the existing project structure and patterns
