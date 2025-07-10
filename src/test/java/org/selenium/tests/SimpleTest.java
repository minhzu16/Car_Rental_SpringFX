package org.selenium.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleTest {
    
    @Test
    @DisplayName("Simple assertion test")
    public void simpleTest() {
        assertTrue(true, "This test should always pass");
    }
} 