package org.controller.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.config.SpringFXMLLoader;
import org.config.SessionManager;
import org.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

import org.springframework.context.annotation.Scope;

@Controller
@Scope("prototype")
public class CustomerDashboardController {

    @FXML
    private Label customerNameLabel;
    @FXML
    private AnchorPane mainContentArea;

    private final SpringFXMLLoader fxmlLoader;
    private final SessionManager sessionManager;

    @Autowired
    public CustomerDashboardController(SpringFXMLLoader fxmlLoader, SessionManager sessionManager) {
        this.fxmlLoader = fxmlLoader;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        Customer current = sessionManager.getCurrentCustomer();
        if (current != null) {
            customerNameLabel.setText("Welcome, " + current.getCustomerName() + "!");
        }
    }

    @FXML
    private void handleBooking(ActionEvent event) {
        // Load car booking view for customers
        loadContent("/fxml/customer_booking.fxml");
    }

    @FXML
    private void handleRentalHistory(ActionEvent event) {
        loadContent("/fxml/customer_rental_history.fxml");
    }

    @FXML
    private void handleProfile(ActionEvent event) {
        loadContent("/fxml/customer_profile.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        sessionManager.logout();
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load("/fxml/login.fxml"));
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadContent(String fxmlPath) {
        javafx.application.Platform.runLater(() -> {
            try {
                System.out.println("DEBUG: Loading content for: " + fxmlPath);
                FXMLLoader loader = fxmlLoader.getLoader(fxmlPath);
                Node node = loader.load();
                System.out.println("DEBUG: Component loaded successfully.");
                
                mainContentArea.getChildren().setAll(node);
                
                AnchorPane.setTopAnchor(node, 0.0);
                AnchorPane.setBottomAnchor(node, 0.0);
                AnchorPane.setLeftAnchor(node, 0.0);
                AnchorPane.setRightAnchor(node, 0.0);
                
                mainContentArea.requestLayout();
            } catch (Exception e) {
                System.err.println("CRITICAL ERROR LOADING: " + fxmlPath);
                e.printStackTrace();
            }
        });
    }
}
