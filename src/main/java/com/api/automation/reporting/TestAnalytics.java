package com.api.automation.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Advanced test analytics and reporting
 * Generates comprehensive test execution analytics and trends
 */
public class TestAnalytics {
    
    private static final Logger logger = LogManager.getLogger(TestAnalytics.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private List<TestExecution> testExecutions = new ArrayList<>();
    private Map<String, List<Double>> performanceMetrics = new HashMap<>();
    private Map<String, Integer> testCategoryStats = new HashMap<>();
    
    /**
     * Record test execution
     */
    public void recordTestExecution(String testName, String category, boolean passed, 
                                  long duration, Map<String, Object> additionalData) {
        TestExecution execution = new TestExecution(
            testName, category, passed, duration, LocalDateTime.now(), additionalData
        );
        testExecutions.add(execution);
        
        // Update category statistics
        testCategoryStats.merge(category, 1, Integer::sum);
        
        // Record performance metrics
        performanceMetrics.computeIfAbsent(testName, k -> new ArrayList<>()).add((double) duration);
        
        logger.info("Recorded test execution: {} - {} - {}", testName, category, passed ? "PASSED" : "FAILED");
    }
    
    /**
     * Generate comprehensive analytics report
     */
    public TestAnalyticsReport generateAnalyticsReport() {
        TestAnalyticsReport report = new TestAnalyticsReport();
        
        // Basic statistics
        report.totalTests = testExecutions.size();
        report.passedTests = (int) testExecutions.stream().filter(TestExecution::isPassed).count();
        report.failedTests = report.totalTests - report.passedTests;
        report.passRate = report.totalTests > 0 ? (report.passedTests * 100.0) / report.totalTests : 0.0;
        
        // Execution time statistics
        OptionalDouble avgDuration = testExecutions.stream().mapToLong(TestExecution::getDuration).average();
        report.averageExecutionTime = avgDuration.orElse(0.0);
        report.totalExecutionTime = testExecutions.stream().mapToLong(TestExecution::getDuration).sum();
        
        // Category breakdown
        report.categoryBreakdown = testCategoryStats;
        
        // Performance trends
        report.performanceTrends = calculatePerformanceTrends();
        
        // Failure analysis
        report.failureAnalysis = analyzeFailures();
        
        // Test stability metrics
        report.stabilityMetrics = calculateStabilityMetrics();
        
        // Execution timeline
        report.executionTimeline = createExecutionTimeline();
        
        logger.info("Generated analytics report with {} test executions", testExecutions.size());
        return report;
    }
    
    /**
     * Calculate performance trends
     */
    private Map<String, PerformanceTrend> calculatePerformanceTrends() {
        Map<String, PerformanceTrend> trends = new HashMap<>();
        
        for (Map.Entry<String, List<Double>> entry : performanceMetrics.entrySet()) {
            String testName = entry.getKey();
            List<Double> durations = entry.getValue();
            
            if (durations.size() >= 2) {
                PerformanceTrend trend = new PerformanceTrend();
                trend.testName = testName;
                trend.averageDuration = durations.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                trend.minDuration = durations.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                trend.maxDuration = durations.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                
                // Calculate trend direction
                double firstHalf = durations.subList(0, durations.size() / 2).stream()
                        .mapToDouble(Double::doubleValue).average().orElse(0.0);
                double secondHalf = durations.subList(durations.size() / 2, durations.size()).stream()
                        .mapToDouble(Double::doubleValue).average().orElse(0.0);
                
                if (secondHalf > firstHalf * 1.1) {
                    trend.trendDirection = "DEGRADING";
                } else if (secondHalf < firstHalf * 0.9) {
                    trend.trendDirection = "IMPROVING";
                } else {
                    trend.trendDirection = "STABLE";
                }
                
                trends.put(testName, trend);
            }
        }
        
        return trends;
    }
    
    /**
     * Analyze test failures
     */
    private FailureAnalysis analyzeFailures() {
        FailureAnalysis analysis = new FailureAnalysis();
        
        List<TestExecution> failures = testExecutions.stream()
                .filter(te -> !te.isPassed())
                .collect(Collectors.toList());
        
        // Failure rate by category
        analysis.failureRateByCategory = testCategoryStats.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> {
                        String category = entry.getKey();
                        long categoryFailures = failures.stream()
                                .filter(f -> category.equals(f.getCategory()))
                                .count();
                        return entry.getValue() > 0 ? (categoryFailures * 100.0) / entry.getValue() : 0.0;
                    }
                ));
        
        // Most failing tests
        analysis.mostFailingTests = failures.stream()
                .collect(Collectors.groupingBy(TestExecution::getTestName, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
        
        // Failure patterns over time
        analysis.failureTimeline = failures.stream()
                .collect(Collectors.groupingBy(
                    te -> te.getExecutionTime().toLocalDate(),
                    Collectors.counting()
                ));
        
        return analysis;
    }
    
    /**
     * Calculate test stability metrics
     */
    private StabilityMetrics calculateStabilityMetrics() {
        StabilityMetrics metrics = new StabilityMetrics();
        
        // Calculate flaky tests (tests that sometimes pass, sometimes fail)
        Map<String, List<Boolean>> testResults = testExecutions.stream()
                .collect(Collectors.groupingBy(
                    TestExecution::getTestName,
                    Collectors.mapping(TestExecution::isPassed, Collectors.toList())
                ));
        
        metrics.flakyTests = testResults.entrySet().stream()
                .filter(entry -> {
                    List<Boolean> results = entry.getValue();
                    return results.size() > 1 && 
                           results.contains(true) && 
                           results.contains(false);
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        // Calculate consistency score
        long consistentTests = testResults.entrySet().stream()
                .filter(entry -> {
                    List<Boolean> results = entry.getValue();
                    return results.size() > 1 && 
                           (results.stream().allMatch(r -> r) || results.stream().allMatch(r -> !r));
                })
                .count();
        
        metrics.consistencyScore = testResults.size() > 0 ? 
                (consistentTests * 100.0) / testResults.size() : 100.0;
        
        return metrics;
    }
    
    /**
     * Create execution timeline
     */
    private List<TimelineEntry> createExecutionTimeline() {
        return testExecutions.stream()
                .collect(Collectors.groupingBy(
                    te -> te.getExecutionTime().toLocalDate(),
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        executions -> {
                            TimelineEntry entry = new TimelineEntry();
                            entry.date = executions.get(0).getExecutionTime().toLocalDate();
                            entry.totalTests = executions.size();
                            entry.passedTests = (int) executions.stream().filter(TestExecution::isPassed).count();
                            entry.failedTests = entry.totalTests - entry.passedTests;
                            entry.averageDuration = executions.stream()
                                    .mapToLong(TestExecution::getDuration)
                                    .average().orElse(0.0);
                            return entry;
                        }
                    )
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(te -> te.date))
                .collect(Collectors.toList());
    }
    
    /**
     * Export analytics to JSON file
     */
    public void exportToJson(String filePath) {
        try {
            TestAnalyticsReport report = generateAnalyticsReport();
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, report);
            logger.info("Analytics report exported to: {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to export analytics report", e);
        }
    }
    
    /**
     * Export analytics to HTML dashboard
     */
    public void exportToHtmlDashboard(String filePath) {
        try {
            TestAnalyticsReport report = generateAnalyticsReport();
            String html = generateHtmlDashboard(report);
            
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(html);
            }
            
            logger.info("HTML dashboard exported to: {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to export HTML dashboard", e);
        }
    }
    
    /**
     * Generate HTML dashboard
     */
    private String generateHtmlDashboard(TestAnalyticsReport report) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>API Test Analytics Dashboard</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
                    .container { max-width: 1200px; margin: 0 auto; }
                    .card { background: white; padding: 20px; margin: 20px 0; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .metric { display: inline-block; margin: 10px 20px; text-align: center; }
                    .metric-value { font-size: 2em; font-weight: bold; color: #2196F3; }
                    .metric-label { font-size: 0.9em; color: #666; }
                    .pass { color: #4CAF50; }
                    .fail { color: #F44336; }
                    .warning { color: #FF9800; }
                    table { width: 100%%; border-collapse: collapse; margin: 10px 0; }
                    th, td { padding: 8px; text-align: left; border-bottom: 1px solid #ddd; }
                    th { background-color: #f2f2f2; }
                    .chart { height: 300px; background: #f9f9f9; border: 1px solid #ddd; margin: 10px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>API Test Analytics Dashboard</h1>
                    <p>Generated on: %s</p>
                    
                    <div class="card">
                        <h2>Test Execution Summary</h2>
                        <div class="metric">
                            <div class="metric-value">%d</div>
                            <div class="metric-label">Total Tests</div>
                        </div>
                        <div class="metric">
                            <div class="metric-value pass">%d</div>
                            <div class="metric-label">Passed</div>
                        </div>
                        <div class="metric">
                            <div class="metric-value fail">%d</div>
                            <div class="metric-label">Failed</div>
                        </div>
                        <div class="metric">
                            <div class="metric-value">%.1f%%</div>
                            <div class="metric-label">Pass Rate</div>
                        </div>
                        <div class="metric">
                            <div class="metric-value">%.0fms</div>
                            <div class="metric-label">Avg Duration</div>
                        </div>
                    </div>
                    
                    <div class="card">
                        <h2>Test Categories</h2>
                        <table>
                            <tr><th>Category</th><th>Count</th><th>Percentage</th></tr>
                            %s
                        </table>
                    </div>
                    
                    <div class="card">
                        <h2>Stability Metrics</h2>
                        <div class="metric">
                            <div class="metric-value %.1f%%</div>
                            <div class="metric-label">Consistency Score</div>
                        </div>
                        <div class="metric">
                            <div class="metric-value warning">%d</div>
                            <div class="metric-label">Flaky Tests</div>
                        </div>
                    </div>
                    
                    <div class="card">
                        <h2>Performance Trends</h2>
                        <table>
                            <tr><th>Test</th><th>Avg Duration</th><th>Min</th><th>Max</th><th>Trend</th></tr>
                            %s
                        </table>
                    </div>
                </div>
            </body>
            </html>
            """,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            report.totalTests,
            report.passedTests,
            report.failedTests,
            report.passRate,
            report.averageExecutionTime,
            generateCategoryTableRows(report.categoryBreakdown, report.totalTests),
            report.stabilityMetrics.consistencyScore,
            report.stabilityMetrics.flakyTests.size(),
            generatePerformanceTableRows(report.performanceTrends)
        );
    }
    
    private String generateCategoryTableRows(Map<String, Integer> categories, int total) {
        return categories.entrySet().stream()
                .map(entry -> String.format("<tr><td>%s</td><td>%d</td><td>%.1f%%</td></tr>",
                        entry.getKey(), entry.getValue(), (entry.getValue() * 100.0) / total))
                .collect(Collectors.joining("\n"));
    }
    
    private String generatePerformanceTableRows(Map<String, PerformanceTrend> trends) {
        return trends.values().stream()
                .map(trend -> String.format("<tr><td>%s</td><td>%.0fms</td><td>%.0fms</td><td>%.0fms</td><td>%s</td></tr>",
                        trend.testName, trend.averageDuration, trend.minDuration, trend.maxDuration, trend.trendDirection))
                .collect(Collectors.joining("\n"));
    }
    
    // Data classes for analytics
    
    public static class TestExecution {
        private String testName;
        private String category;
        private boolean passed;
        private long duration;
        private LocalDateTime executionTime;
        private Map<String, Object> additionalData;
        
        public TestExecution(String testName, String category, boolean passed, long duration, 
                           LocalDateTime executionTime, Map<String, Object> additionalData) {
            this.testName = testName;
            this.category = category;
            this.passed = passed;
            this.duration = duration;
            this.executionTime = executionTime;
            this.additionalData = additionalData;
        }
        
        // Getters
        public String getTestName() { return testName; }
        public String getCategory() { return category; }
        public boolean isPassed() { return passed; }
        public long getDuration() { return duration; }
        public LocalDateTime getExecutionTime() { return executionTime; }
        public Map<String, Object> getAdditionalData() { return additionalData; }
    }
    
    public static class TestAnalyticsReport {
        public int totalTests;
        public int passedTests;
        public int failedTests;
        public double passRate;
        public double averageExecutionTime;
        public long totalExecutionTime;
        public Map<String, Integer> categoryBreakdown;
        public Map<String, PerformanceTrend> performanceTrends;
        public FailureAnalysis failureAnalysis;
        public StabilityMetrics stabilityMetrics;
        public List<TimelineEntry> executionTimeline;
    }
    
    public static class PerformanceTrend {
        public String testName;
        public double averageDuration;
        public double minDuration;
        public double maxDuration;
        public String trendDirection;
    }
    
    public static class FailureAnalysis {
        public Map<String, Double> failureRateByCategory;
        public Map<String, Long> mostFailingTests;
        public Map<java.time.LocalDate, Long> failureTimeline;
    }
    
    public static class StabilityMetrics {
        public List<String> flakyTests;
        public double consistencyScore;
    }
    
    public static class TimelineEntry {
        public java.time.LocalDate date;
        public int totalTests;
        public int passedTests;
        public int failedTests;
        public double averageDuration;
    }
}
