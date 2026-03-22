package org.controller.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.config.SessionManager;
import org.entity.Customer;
import org.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.context.annotation.Scope;

@Controller
@Scope("prototype")
public class CustomerProfileController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private DatePicker birthdayPicker;
    @FXML
    private TextField idCardField;
    @FXML
    private TextField licenseField;
    @FXML
    private Label statusLabel;

    private final CustomerService customerService;
    private final SessionManager sessionManager;

    @Autowired
    public CustomerProfileController(CustomerService customerService, SessionManager sessionManager) {
        this.customerService = customerService;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        try {
            System.out.println("DEBUG: Initializing CustomerProfileController...");
            loadProfile();
            System.out.println("DEBUG: CustomerProfileController Initialized Successfully.");
        } catch (Exception e) {
            System.err.println("DEBUG: CustomerProfileController Initialization FAILED");
            e.printStackTrace();
        }
    }

    private void loadProfile() {
        Customer current = sessionManager.getCurrentCustomer();
        System.out.println("DEBUG: Loading profile for customer: " + current);
        if (current != null) {
            System.out.println("DEBUG: Customer Name: " + current.getCustomerName());
            nameField.setText(current.getCustomerName());
            emailField.setText(current.getEmail());
            phoneField.setText(current.getMobile());
            addressField.setText(current.getAddress());
            birthdayPicker.setValue(current.getBirthday());
            idCardField.setText(current.getIdentityCard());
            licenseField.setText(current.getLicenceNumber());
        } else {
            System.err.println("DEBUG: NO CUSTOMER IN SESSION");
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        Customer current = sessionManager.getCurrentCustomer();
        if (current == null) return;

        current.setCustomerName(nameField.getText());
        current.setEmail(emailField.getText());
        current.setMobile(phoneField.getText());
        current.setAddress(addressField.getText());
        current.setBirthday(birthdayPicker.getValue());
        current.setIdentityCard(idCardField.getText());
        current.setLicenceNumber(licenseField.getText());

        try {
            customerService.update(current.getCustomerID(), current);
            statusLabel.setText("Profile updated successfully!");
            statusLabel.setVisible(true);
        } catch (Exception e) {
            statusLabel.setText("Error updating profile.");
            statusLabel.setStyle("-fx-text-fill: -fx-danger-color;");
            statusLabel.setVisible(true);
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadProfile();
        statusLabel.setVisible(false);
    }
}
