@echo off
echo Car Rental Management System - Test Runner
echo =========================================
echo.

:menu
echo Choose a test option:
echo 1. Run Simple Test (Quick environment check)
echo 2. Run Login Test (Requires running application)
echo 3. Run Car Registration Tests (Requires running application)
echo 4. Run Mock Tests (No application required)
echo 5. Run Demo Tests (Console simulation)
echo 6. Run All Tests (Test Suite)
echo 7. Exit
echo.

set /p choice=Enter your choice (1-7): 

if "%choice%"=="1" goto simple
if "%choice%"=="2" goto login
if "%choice%"=="3" goto car
if "%choice%"=="4" goto mock
if "%choice%"=="5" goto demo
if "%choice%"=="6" goto all
if "%choice%"=="7" goto end

echo Invalid choice. Please try again.
echo.
goto menu

:simple
echo.
echo Running Simple Test...
call mvn test -Dtest=SimpleTest
echo.
goto continue

:login
echo.
echo Running Login Test...
call mvn test -Dtest=LoginTest
echo.
goto continue

:car
echo.
echo Running Car Registration Tests...
call mvn test -Dtest=CarRegistrationTest
echo.
goto continue

:mock
echo.
echo Running Mock Tests...
call mvn test -Dtest=MockLoginTest,MockCarRegistrationTest
echo.
goto continue

:demo
echo.
echo Running Demo Tests...
call mvn test -Dtest=DemoSeleniumTest
echo.
goto continue

:all
echo.
echo Running All Tests...
call mvn test -Dtest=TestSuite
echo.
goto continue

:continue
echo.
echo Test execution completed.
echo.
echo 1. Return to menu
echo 2. Exit
echo.
set /p next=Enter your choice (1-2): 

if "%next%"=="1" goto menu
if "%next%"=="2" goto end

echo Invalid choice. Returning to menu...
goto menu

:end
echo.
echo Thank you for using the Test Runner.
echo. 