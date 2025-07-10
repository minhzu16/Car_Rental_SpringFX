#!/bin/bash

echo "Car Rental Management System - Test Runner"
echo "========================================="
echo

menu() {
  echo "Choose a test option:"
  echo "1. Run Simple Test (Quick environment check)"
  echo "2. Run Login Test (Requires running application)"
  echo "3. Run Car Registration Tests (Requires running application)"
  echo "4. Run Mock Tests (No application required)"
  echo "5. Run Demo Tests (Console simulation)"
  echo "6. Run All Tests (Test Suite)"
  echo "7. Exit"
  echo
  
  read -p "Enter your choice (1-7): " choice
  
  case $choice in
    1) run_simple ;;
    2) run_login ;;
    3) run_car ;;
    4) run_mock ;;
    5) run_demo ;;
    6) run_all ;;
    7) exit_script ;;
    *) 
      echo "Invalid choice. Please try again."
      echo
      menu
      ;;
  esac
}

run_simple() {
  echo
  echo "Running Simple Test..."
  mvn test -Dtest=SimpleTest
  continue_prompt
}

run_login() {
  echo
  echo "Running Login Test..."
  mvn test -Dtest=LoginTest
  continue_prompt
}

run_car() {
  echo
  echo "Running Car Registration Tests..."
  mvn test -Dtest=CarRegistrationTest
  continue_prompt
}

run_mock() {
  echo
  echo "Running Mock Tests..."
  mvn test -Dtest=MockLoginTest,MockCarRegistrationTest
  continue_prompt
}

run_demo() {
  echo
  echo "Running Demo Tests..."
  mvn test -Dtest=DemoSeleniumTest
  continue_prompt
}

run_all() {
  echo
  echo "Running All Tests..."
  mvn test -Dtest=TestSuite
  continue_prompt
}

continue_prompt() {
  echo
  echo "Test execution completed."
  echo
  echo "1. Return to menu"
  echo "2. Exit"
  echo
  
  read -p "Enter your choice (1-2): " next
  
  case $next in
    1) menu ;;
    2) exit_script ;;
    *)
      echo "Invalid choice. Returning to menu..."
      menu
      ;;
  esac
}

exit_script() {
  echo
  echo "Thank you for using the Test Runner."
  echo
  exit 0
}

# Start the script
menu 