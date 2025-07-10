package org.selenium;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.selenium.tests.TestReportGenerator;

import java.io.PrintWriter;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

/**
 * Main class to run all tests programmatically
 */
public class TestRunner {
    
    public static void main(String[] args) {
        // Create a launcher
        Launcher launcher = LauncherFactory.create();
        
        // Register listeners
        TestReportGenerator reportGenerator = new TestReportGenerator();
        SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
        
        launcher.registerTestExecutionListeners(reportGenerator);
        launcher.registerTestExecutionListeners(summaryListener);
        
        // Create discovery request
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage("org.selenium.tests"))
                .build();
        
        // Execute tests
        System.out.println("Running Car Rental System Tests...");
        launcher.execute(request);
        
        // Print results to console
        TestExecutionSummary summary = summaryListener.getSummary();
        summary.printTo(new PrintWriter(System.out));
        
        // Print report location
        System.out.println("\nTest report has been generated in the test-reports directory.");
    }
} 