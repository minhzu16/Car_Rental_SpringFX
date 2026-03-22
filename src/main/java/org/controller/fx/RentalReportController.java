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
import org.service.CarRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

@Controller
public class RentalReportController {

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label totalRevenueLabel;
    @FXML
    private TableView<CarRental> reportTable;
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

    private final CarRentalService rentalService;
    private ObservableList<CarRental> reportList = FXCollections.observableArrayList();

    @Autowired
    public RentalReportController(CarRentalService rentalService) {
        this.rentalService = rentalService;
    }

    @FXML
    public void initialize() {
        colCar.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCar().getCarName()));
        colCustomer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomer().getCustomerName()));
        colPickupDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId().getPickupDate()));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("rentPrice"));

        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());
        
        handleGenerateReport(null);
    }

    @FXML
    private void handleGenerateReport(ActionEvent event) {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start == null || end == null) {
            showAlert("Invalid Dates", "Please select both start and end dates.");
            return;
        }

        List<CarRental> rentals = rentalService.findByDateRange(start, end);
        reportList.setAll(rentals);
        reportTable.setItems(reportList);

        double totalRevenue = rentals.stream()
                .mapToDouble(CarRental::getRentPrice)
                .sum();
        totalRevenueLabel.setText(String.format("$%.2f", totalRevenue));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
