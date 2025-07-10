#!/bin/bash

echo "Car Rental Management System - Test Suite Runner"
echo "============================================="
echo

echo "Compiling tests..."
mvn compile test-compile

echo
echo "Running test suite..."
mvn exec:java -Dexec.mainClass="org.selenium.TestRunner" -Dexec.classpathScope=test

echo
echo "Test execution completed."
echo
read -p "Press Enter to continue..." 