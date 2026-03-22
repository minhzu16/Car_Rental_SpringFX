package org.controller.fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.config.SessionManager;
import org.entity.Car;
import org.entity.CarRental;
import org.entity.CarRentalKey;
import org.enums.RentalStatus;
import org.service.CarService;
import org.service.CarRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;

@Controller
@Scope("prototype")
public class CustomerBookingController {

    @FXML private TableView<Car> carTable;
    @FXML private TableColumn<Car, String> colName;
    @FXML private TableColumn<Car, Integer> colModel;
    @FXML private TableColumn<Car, String> colColor;
    @FXML private TableColumn<Car, Integer> colCapacity;
    @FXML private TableColumn<Car, Double> colPrice;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label statusLabel;

    private final CarService carService;
    private final CarRentalService rentalService;
    private final SessionManager sessionManager;
    private ObservableList<Car> availableCars = FXCollections.observableArrayList();

    @Autowired
    public CustomerBookingController(CarService carService, CarRentalService rentalService, SessionManager sessionManager) {
        this.carService = carService;
        this.rentalService = rentalService;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("carName"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("carModelYear"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("rentPrice"));

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(1));
        
        loadAllCars();
    }

    private void loadAllCars() {
        System.out.println("DEBUG: Loading all cars for customer booking view...");
        List<Car> all = carService.findAll().stream()
                .filter(c -> "Active".equalsIgnoreCase(c.getStatus()) || "Available".equalsIgnoreCase(c.getStatus()))
                .collect(Collectors.toList());
        System.out.println("DEBUG: Found " + all.size() + " available cars.");
        availableCars.setAll(all);
        carTable.setItems(availableCars);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        
        if (start == null || end == null || end.isBefore(start) || start.isBefore(LocalDate.now())) {
            showError("Invalid date range. Selecting future dates only.");
            return;
        }

        List<Car> available = carService.findAll().stream()
                .filter(c -> "Active".equalsIgnoreCase(c.getStatus()))
                .filter(c -> !rentalService.isCarRented(c.getCarID(), start, end))
                .collect(Collectors.toList());
                
        availableCars.setAll(available);
        statusLabel.setVisible(false);
    }

    @FXML
    private void handleBook(ActionEvent event) {
        Car selected = carTable.getSelectionModel().getSelectedItem();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (selected == null) {
            showError("Please select a car.");
            return;
        }
        if (start == null || end == null || end.isBefore(start)) {
            showError("Invalid dates selected.");
            return;
        }
        if (sessionManager.getCurrentCustomer() == null) {
            showError("Authentication Error. Please login again.");
            return;
        }

        try {
            if (rentalService.isCarRented(selected.getCarID(), start, end)) {
                showError("This car is no longer available for the selected dates.");
                return;
            }

            long days = ChronoUnit.DAYS.between(start, end);
            double totalPrice = Math.max(1, days) * selected.getRentPrice();
            
            CarRental rental = new CarRental();
            CarRentalKey id = new CarRentalKey(sessionManager.getCurrentCustomer().getCustomerID(), selected.getCarID(), start);
            rental.setId(id);
            rental.setCar(selected);
            rental.setCustomer(sessionManager.getCurrentCustomer());
            rental.setReturnDate(end);
            rental.setRentPrice(totalPrice);
            rental.setStatus(RentalStatus.PENDING);
            
            rentalService.save(rental);
            
            statusLabel.setText("Successfully booked for $" + totalPrice);
            statusLabel.setStyle("-fx-text-fill: -fx-success-color;");
            statusLabel.setVisible(true);
            
            // Optionally, refresh available cars list
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not complete booking.");
        }
    }

    private void showError(String msg) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: -fx-danger-color;");
        statusLabel.setVisible(true);
    }
}
