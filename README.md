# REST Assured API Automation Framework

A comprehensive API automation testing framework built with Java, REST Assured, TestNG, and Maven. This framework provides a robust foundation for API testing with features like multi-environment support, detailed reporting, and parallel test execution.

## 🚀 Features

- **REST Assured Integration**: Powerful API testing with REST Assured
- **TestNG Framework**: Organized test execution with groups and parallel testing
- **Multi-Environment Support**: Configurable environments (dev, staging, prod)
- **Allure Reporting**: Beautiful and detailed test reports
- **Logging**: Comprehensive logging with Log4j2
- **JSON Utilities**: Easy JSON handling and validation
- **Performance Testing**: Built-in performance test capabilities
- **Negative Testing**: Comprehensive error scenario testing
- **CI/CD Ready**: Maven integration for continuous integration

## 📁 Project Structure

```
rest-assured-automation/
├── src/
│   ├── main/java/com/api/automation/
│   │   ├── config/
│   │   │   └── ConfigManager.java           # Environment configuration manager
│   │   └── utils/
│   │       ├── JsonUtils.java               # JSON manipulation utilities
│   │       └── SpecificationBuilder.java    # REST Assured specifications
│   └── test/
│       ├── java/com/api/automation/
│       │   ├── base/
│       │   │   └── BaseTest.java            # Base test class
│       │   └── tests/
│       │       ├── UserAPITests.java        # User management API tests
│       │       ├── AuthenticationTests.java # Authentication API tests
│       │       └── PerformanceTests.java    # Performance tests
│       └── resources/
│           ├── config/
│           │   ├── dev.properties           # Development environment config
│           │   ├── staging.properties       # Staging environment config
│           │   └── prod.properties          # Production environment config
│           ├── testdata/
│           │   └── test-data.json          # Test data files
│           ├── testng.xml                  # Main TestNG suite
│           ├── smoke-tests.xml             # Smoke test suite
│           ├── regression-tests.xml        # Regression test suite
│           └── log4j2.xml                  # Logging configuration
├── pom.xml                                 # Maven configuration
└── README.md                               # This file
```

## 🛠️ Prerequisites

- **Java 11** or higher
- **Maven 3.6** or higher
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code)

## ⚙️ Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd rest-assured-automation
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Configure environments**
   - Update configuration files in `src/test/resources/config/`
   - Set your API endpoints, credentials, and other environment-specific settings

## 🚀 Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Suite
```bash
# Run smoke tests
mvn clean test -Dsuite=smoke-tests.xml

# Run regression tests
mvn clean test -Dsuite=regression-tests.xml
```

### Run Tests for Specific Environment
```bash
# Run tests against staging environment
mvn clean test -Denv=staging

# Run tests against production environment
mvn clean test -Denv=prod
```

### Run Tests by Groups
```bash
# Run only smoke tests
mvn clean test -Dgroups=smoke

# Run smoke and regression tests
mvn clean test -Dgroups=smoke,regression

# Run negative tests
mvn clean test -Dgroups=negative
```

### Run Tests with Profiles
```bash
# Run API tests profile
mvn clean test -Papi-tests
```

## 📊 Test Reporting

### Allure Reports
Generate and view Allure reports:
```bash
# Generate Allure report
mvn allure:report

# Serve Allure report (opens in browser)
mvn allure:serve
```

### TestNG Reports
TestNG HTML reports are automatically generated in `target/surefire-reports/`

## 🔧 Configuration

### Environment Configuration
Each environment has its own configuration file:

**dev.properties** (Development)
```properties
base.url=https://reqres.in/api
username=testuser
password=testpass
api.key=your-dev-api-key
timeout=30
```

### Test Groups
Tests are organized using TestNG groups:

- **smoke**: Critical functionality tests
- **regression**: All functional tests
- **performance**: Performance and load tests
- **negative**: Error handling tests
- **auth**: Authentication tests
- **user**: User management tests

### Logging Configuration
Logging is configured in `log4j2.xml` with different levels:
- **DEBUG**: Detailed information for development
- **INFO**: General information
- **WARN**: Warning messages
- **ERROR**: Error messages

## 📝 Writing Tests

### Basic Test Structure
```java
@Test(groups = {"smoke", "regression"}, priority = 1)
@Story("Test story description")
@Description("Detailed test description")
public void testMethodName() {
    logTestInfo("testMethodName", "Test description");
    
    Response response = given()
            .spec(requestSpec)
            .when()
            .get("/endpoint")
            .then()
            .statusCode(200)
            .extract()
            .response();
    
    // Assertions
    Assert.assertEquals(response.statusCode(), 200);
}
```

### Using Test Data
```java
// Read test data from JSON
String testData = JsonUtils.readJsonFromFile("src/test/resources/testdata/test-data.json");
Map<String, Object> userData = JsonUtils.fromJsonString(testData, Map.class);
```

## 🎯 Best Practices

1. **Test Organization**: Use meaningful test names and group tests logically
2. **Data Management**: Keep test data separate from test logic
3. **Error Handling**: Always include negative test scenarios
4. **Assertions**: Use multiple assertions to validate responses thoroughly
5. **Logging**: Include detailed logging for debugging
6. **Reusability**: Create reusable utility methods and specifications

## 🔍 Troubleshooting

### Common Issues

1. **Java Version**: Ensure Java 11+ is installed and configured
2. **Maven Dependencies**: Run `mvn clean install` to resolve dependencies
3. **Environment Configuration**: Verify API endpoints and credentials are correct
4. **Test Data**: Ensure test data files are in the correct format and location

### Debug Mode
Run tests with debug logging:
```bash
mvn clean test -Dlog.level=DEBUG
```

## 🤝 Contributing

1. Follow the existing code structure and naming conventions
2. Add appropriate test groups and Allure annotations
3. Include comprehensive assertions and error handling
4. Update documentation for new features
5. Run all tests before submitting changes

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For questions or issues, please:
1. Check the troubleshooting section
2. Review existing test examples
3. Create an issue in the repository

---

**Happy Testing! 🎉**
