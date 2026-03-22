package org.controller.fx;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.entity.CarRental;
import org.enums.RentalStatus;
import org.service.CarRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.context.annotation.Scope;

@Controller
@Scope("prototype")
public class RentalManagementController {

    @FXML
    private TableView<CarRental> rentalTable;
    @FXML
    private TableColumn<CarRental, String> colCar;
    @FXML
    private TableColumn<CarRental, String> colCustomer;
    @FXML
    private TableColumn<CarRental, LocalDate> colPickupDate;
    @FXML
    private TableColumn<CarRental, LocalDate> colReturnDate;
    @FXML
    private TableColumn<CarRental, Double> colPrice;
    @FXML
    private TableColumn<CarRental, String> colStatus;

    private final CarRentalService rentalService;
    private ObservableList<CarRental> rentalList = FXCollections.observableArrayList();

    @Autowired
    public RentalManagementController(CarRentalService rentalService) {
        this.rentalService = rentalService;
    }

    @FXML
    public void initialize() {
        colCar.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCar().getCarName()));
        colCustomer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomer().getCustomerName()));
        colPickupDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId().getPickupDate()));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("rentPrice"));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));

        loadRentals();
    }

    private void loadRentals() {
        rentalList.setAll(rentalService.findAll());
        rentalTable.setItems(rentalList);
    }

    @FXML
    private void handleApprove(ActionEvent event) {
        CarRental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getStatus() != RentalStatus.PENDING) {
            showAlert(Alert.AlertType.WARNING, "Invalid Selection", "Please select a PENDING rental to approve.");
            return;
        }
        
        selected.setStatus(RentalStatus.ACTIVE);
        rentalService.save(selected);
        loadRentals();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Rental approved and now ACTIVE.");
    }

    @FXML
    private void handleComplete(ActionEvent event) {
        CarRental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected == null || (selected.getStatus() != RentalStatus.ACTIVE && selected.getStatus() != RentalStatus.EARLY_RETURN)) {
            showAlert(Alert.AlertType.WARNING, "Invalid Selection", "Only ACTIVE or EARLY_RETURN rentals can be completed.");
            return;
        }
        
        selected.setStatus(RentalStatus.COMPLETED);
        rentalService.save(selected);
        loadRentals();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Rental marked as COMPLETED.");
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        CarRental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a rental to cancel.");
            return;
        }

        if (selected.getStatus() == RentalStatus.COMPLETED || selected.getStatus() == RentalStatus.CANCELLED) {
            showAlert(Alert.AlertType.ERROR, "Status Error", "Cannot cancel a completed or already cancelled rental.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Cancellation");
        confirm.setContentText("Are you sure you want to cancel this rental?");
        Optional<ButtonType> result = confirm.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            selected.setStatus(RentalStatus.CANCELLED);
            rentalService.save(selected);
            loadRentals();
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadRentals();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
