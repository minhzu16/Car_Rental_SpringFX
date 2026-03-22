package org.controller.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.entity.Customer;
import org.service.CustomerService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;

@Controller
@Scope("prototype")
public class CustomerFormController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField mobileField;
    @FXML private TextField addressField;
    @FXML private TextField idCardField;
    @FXML private TextField licenceField;
    @FXML private DatePicker birthdayPicker;
    @FXML private Label errorLabel;
    @FXML private Label titleLabel;

    private final CustomerService customerService;
    private Customer currentCustomer;
    private Runnable onSaveCallback;

    public CustomerFormController(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void setCustomerInfo(Customer customer, Runnable onSaveCallback) {
        this.onSaveCallback = onSaveCallback;
        if (customer != null) {
            this.currentCustomer = customer;
            titleLabel.setText("Edit Customer: " + customer.getCustomerName());
            nameField.setText(customer.getCustomerName());
            emailField.setText(customer.getEmail());
            mobileField.setText(customer.getMobile());
            addressField.setText(customer.getAddress());
            idCardField.setText(customer.getIdentityCard());
            licenceField.setText(customer.getLicenceNumber());
            birthdayPicker.setValue(customer.getBirthday());
        } else {
            this.currentCustomer = new Customer();
            titleLabel.setText("Add New Customer");
            birthdayPicker.setValue(LocalDate.now().minusYears(18));
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            currentCustomer.setCustomerName(nameField.getText().trim());
            currentCustomer.setEmail(emailField.getText().trim());
            currentCustomer.setMobile(mobileField.getText().trim());
            currentCustomer.setAddress(addressField.getText().trim());
            currentCustomer.setIdentityCard(idCardField.getText().trim());
            currentCustomer.setLicenceNumber(licenceField.getText().trim());
            currentCustomer.setBirthday(birthdayPicker.getValue());

            // For new customers, we need to satisfy DB constraints. Password requires Account? 
            if (currentCustomer.getCustomerID() == null) {
                currentCustomer.setLicenceDate(LocalDate.now());
                currentCustomer.setPassword("default123"); 
                // In a perfect system, when admin creates a user, it also creates an Account.
                // Assuming Account creation handles it or cascading is applied.
            }

            if (currentCustomer.getCustomerID() == null) {
                customerService.save(currentCustomer);
            } else {
                customerService.update(currentCustomer.getCustomerID(), currentCustomer);
            }
            
            if (onSaveCallback != null) onSaveCallback.run();
            closeWindow(event);
        } catch (Exception e) {
            System.err.println("Error saving customer: " + e.getMessage());
            errorLabel.setText("Validation Error: Please check your inputs.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
