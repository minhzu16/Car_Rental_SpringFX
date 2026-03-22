package org.controller.fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.entity.Customer;
import org.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import org.config.SpringFXMLLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Scope;

@Controller
@Scope("prototype")
public class CustomerManagementController {

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> colID;
    @FXML
    private TableColumn<Customer, String> colName;
    @FXML
    private TableColumn<Customer, String> colEmail;
    @FXML
    private TableColumn<Customer, String> colMobile;
    @FXML
    private TableColumn<Customer, LocalDate> colBirthday;
    @FXML
    private TableColumn<Customer, String> colLicence;
    
    @FXML
    private TextField searchField;

    private final CustomerService customerService;
    private final SpringFXMLLoader fxmlLoader;
    private ObservableList<Customer> customerList = FXCollections.observableArrayList();

    @Autowired
    public CustomerManagementController(CustomerService customerService, SpringFXMLLoader fxmlLoader) {
        this.customerService = customerService;
        this.fxmlLoader = fxmlLoader;
    }

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colMobile.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        colBirthday.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        colLicence.setCellValueFactory(new PropertyValueFactory<>("licenceNumber"));

        loadCustomers();
    }

    private void loadCustomers() {
        customerList.clear();
        customerList.addAll(customerService.findAll());
        customerTable.setItems(customerList);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadCustomers();
        } else {
            // Implement searching
            // customerService doesn't have a simple search without Pagination, let's use findAll and filter locally for now
            List<Customer> filtered = customerService.findAll().stream()
                .filter(c -> c.getCustomerName().toLowerCase().contains(query.toLowerCase()) || 
                             c.getEmail().toLowerCase().contains(query.toLowerCase()))
                .toList();
            customerList.setAll(filtered);
        }
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        openCustomerDialog(null);
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a customer to edit.");
            return;
        }
        openCustomerDialog(selected);
    }

    private void openCustomerDialog(Customer customer) {
        try {
            FXMLLoader loader = fxmlLoader.getLoader("/fxml/customer_form.fxml");
            Parent root = loader.load();
            
            CustomerFormController controller = loader.getController();
            controller.setCustomerInfo(customer, this::loadCustomers);
            
            Stage stage = new Stage();
            stage.setTitle(customer == null ? "Add Customer" : "Edit Customer");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load dialog.");
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a customer to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Are you sure you want to delete this customer?");
        Optional<ButtonType> result = confirm.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            customerService.delete(selected.getCustomerID());
            loadCustomers();
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadCustomers();
        searchField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
