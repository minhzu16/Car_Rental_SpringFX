package org.controller.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.config.SpringFXMLLoader;
import org.service.CarService;
import org.service.CustomerService;
import org.service.CarRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@Scope("prototype")
public class AdminDashboardController {

    @FXML
    private Label adminNameLabel;
    @FXML
    private Label totalCustomersLabel;
    @FXML
    private Label totalCarsLabel;
    @FXML
    private Label activeRentalsLabel;
    @FXML
    private AnchorPane mainContentArea;

    private final SpringFXMLLoader fxmlLoader;
    private final CustomerService customerService;
    private final CarService carService;
    private final CarRentalService carRentalService;

    @Autowired
    public AdminDashboardController(SpringFXMLLoader fxmlLoader, CustomerService customerService, 
                                   CarService carService, CarRentalService carRentalService) {
        this.fxmlLoader = fxmlLoader;
        this.customerService = customerService;
        this.carService = carService;
        this.carRentalService = carRentalService;
    }

    @FXML
    public void initialize() {
        updateStats();
    }

    private void updateStats() {
        totalCustomersLabel.setText(String.valueOf(customerService.findAll().size()));
        totalCarsLabel.setText(String.valueOf(carService.findAll().size()));
        activeRentalsLabel.setText(String.valueOf(carRentalService.findAll().size())); // Simple count for now
    }

    @FXML
    private void handleDashboard(ActionEvent event) {
        // Redraw current dashboard or navigate home
        updateStats();
    }

    @FXML
    private void handleCustomerManagement(ActionEvent event) {
        loadContent("/fxml/customer_management.fxml");
    }

    @FXML
    private void handleCarManagement(ActionEvent event) {
        loadContent("/fxml/car_management.fxml");
    }

    @FXML
    private void handleRentalManagement(ActionEvent event) {
        loadContent("/fxml/rental_management.fxml");
    }

    @FXML
    private void handleReports(ActionEvent event) {
        loadContent("/fxml/rental_report.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
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
        try {
            Node node = fxmlLoader.load(fxmlPath);
            mainContentArea.getChildren().setAll(node);
            
            // Re-anchor to fill the area
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
