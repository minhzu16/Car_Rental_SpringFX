@echo off
echo Car Rental Management System - Test Suite Runner
echo =============================================
echo.

echo Compiling tests...
call mvn compile test-compile

echo.
echo Running test suite...
call mvn exec:java -Dexec.mainClass="org.selenium.TestRunner" -Dexec.classpathScope=test

echo.
echo Test execution completed.
echo.
pause 