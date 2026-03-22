package org.controller.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.config.SpringFXMLLoader;
import org.config.SessionManager;
import org.entity.Account;
import org.entity.Customer;
import org.service.AccountService;
import org.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@Scope("prototype")
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final AccountService accountService;
    private final CustomerService customerService;
    private final SessionManager sessionManager;
    private final SpringFXMLLoader fxmlLoader;

    @Autowired
    public LoginController(AccountService accountService, CustomerService customerService, 
                           SessionManager sessionManager, SpringFXMLLoader fxmlLoader) {
        this.accountService = accountService;
        this.customerService = customerService;
        this.sessionManager = sessionManager;
        this.fxmlLoader = fxmlLoader;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        try {
            Account account = accountService.authenticate(username, password);

            if (account != null) {
                sessionManager.setCurrentAccount(account);
                
                String role = account.getRole().toUpperCase();
                
                if ("ADMIN".equals(role)) {
                    navigateTo(event, "/fxml/admin_dashboard.fxml", "Admin Dashboard");
                } else if ("USER".equals(role)) {
                    Customer customer = customerService.findByAccount(account);
                    sessionManager.setCurrentCustomer(customer);
                    navigateTo(event, "/fxml/customer_dashboard.fxml", "Customer Dashboard");
                } else {
                    showError("Access denied: Invalid role.");
                }
            } else {
                showError("Invalid account name or password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred during login. Check logs.");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // Implement navigation to registration screen if needed
        showError("Registration is coming soon!");
    }

    private void navigateTo(ActionEvent event, String fxmlPath, String title) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(fxmlPath));
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load the next screen.");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
