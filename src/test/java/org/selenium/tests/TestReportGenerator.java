package org.selenium.tests;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom TestExecutionListener that generates HTML test reports
 */
public class TestReportGenerator implements TestExecutionListener {
    private final Map<String, TestExecutionResult> testResults = new HashMap<>();
    private final AtomicInteger totalTests = new AtomicInteger(0);
    private final AtomicInteger passedTests = new AtomicInteger(0);
    private final AtomicInteger failedTests = new AtomicInteger(0);
    private final AtomicInteger skippedTests = new AtomicInteger(0);
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        startTime = LocalDateTime.now();
        System.out.println("Test execution started at: " + startTime);
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        endTime = LocalDateTime.now();
        System.out.println("Test execution finished at: " + endTime);
        generateReport();
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (testIdentifier.isTest()) {
            testResults.put(testIdentifier.getDisplayName(), testExecutionResult);
            totalTests.incrementAndGet();
            
            switch (testExecutionResult.getStatus()) {
                case SUCCESSFUL:
                    passedTests.incrementAndGet();
                    break;
                case FAILED:
                    failedTests.incrementAndGet();
                    break;
                case ABORTED:
                    skippedTests.incrementAndGet();
                    break;
            }
        }
    }

    private void generateReport() {
        try {
            // Create reports directory if it doesn't exist
            Path reportsDir = Paths.get("test-reports");
            if (!Files.exists(reportsDir)) {
                Files.createDirectories(reportsDir);
            }
            
            // Generate report filename with timestamp
            String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(endTime);
            String reportFilename = "test-reports/test-report-" + timestamp + ".html";
            
            try (FileWriter writer = new FileWriter(reportFilename)) {
                // HTML header
                writer.write("<!DOCTYPE html>\n");
                writer.write("<html lang=\"en\">\n");
                writer.write("<head>\n");
                writer.write("    <meta charset=\"UTF-8\">\n");
                writer.write("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
                writer.write("    <title>Car Rental System Test Report</title>\n");
                writer.write("    <style>\n");
                writer.write("        body { font-family: Arial, sans-serif; margin: 20px; }\n");
                writer.write("        h1 { color: #333; }\n");
                writer.write("        .summary { background-color: #f5f5f5; padding: 15px; border-radius: 5px; margin-bottom: 20px; }\n");
                writer.write("        .passed { color: green; }\n");
                writer.write("        .failed { color: red; }\n");
                writer.write("        .skipped { color: orange; }\n");
                writer.write("        table { width: 100%; border-collapse: collapse; }\n");
                writer.write("        th, td { padding: 8px; text-align: left; border-bottom: 1px solid #ddd; }\n");
                writer.write("        th { background-color: #f2f2f2; }\n");
                writer.write("        tr:hover { background-color: #f5f5f5; }\n");
                writer.write("    </style>\n");
                writer.write("</head>\n");
                writer.write("<body>\n");
                
                // Report header
                writer.write("    <h1>Car Rental System Test Report</h1>\n");
                writer.write("    <div class=\"summary\">\n");
                writer.write("        <p><strong>Start Time:</strong> " + startTime + "</p>\n");
                writer.write("        <p><strong>End Time:</strong> " + endTime + "</p>\n");
                writer.write("        <p><strong>Duration:</strong> " + 
                        java.time.Duration.between(startTime, endTime).toSeconds() + " seconds</p>\n");
                writer.write("        <p><strong>Total Tests:</strong> " + totalTests.get() + "</p>\n");
                writer.write("        <p><strong>Passed:</strong> <span class=\"passed\">" + passedTests.get() + "</span></p>\n");
                writer.write("        <p><strong>Failed:</strong> <span class=\"failed\">" + failedTests.get() + "</span></p>\n");
                writer.write("        <p><strong>Skipped:</strong> <span class=\"skipped\">" + skippedTests.get() + "</span></p>\n");
                writer.write("    </div>\n");
                
                // Test results table
                writer.write("    <h2>Test Results</h2>\n");
                writer.write("    <table>\n");
                writer.write("        <tr>\n");
                writer.write("            <th>Test Name</th>\n");
                writer.write("            <th>Status</th>\n");
                writer.write("            <th>Details</th>\n");
                writer.write("        </tr>\n");
                
                testResults.forEach((testName, result) -> {
                    try {
                        writer.write("        <tr>\n");
                        writer.write("            <td>" + testName + "</td>\n");
                        
                        String statusClass = "";
                        switch (result.getStatus()) {
                            case SUCCESSFUL:
                                statusClass = "passed";
                                break;
                            case FAILED:
                                statusClass = "failed";
                                break;
                            case ABORTED:
                                statusClass = "skipped";
                                break;
                        }
                        
                        writer.write("            <td class=\"" + statusClass + "\">" + result.getStatus() + "</td>\n");
                        
                        // Details column (exception if any)
                        writer.write("            <td>");
                        result.getThrowable().ifPresent(throwable -> {
                            try {
                                writer.write(throwable.getMessage());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        writer.write("</td>\n");
                        writer.write("        </tr>\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                
                writer.write("    </table>\n");
                writer.write("</body>\n");
                writer.write("</html>");
            }
            
            System.out.println("Test report generated: " + reportFilename);
            
        } catch (IOException e) {
            System.err.println("Failed to generate test report: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 