package org.controller.fx;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.config.SessionManager;
import org.entity.CarRental;
import org.entity.Customer;
import org.service.CarRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.context.annotation.Scope;
import java.time.LocalDate;
import java.util.List;

@Controller
@Scope("prototype")
public class CustomerRentalHistoryController {

    @FXML
    private TableView<CarRental> rentalTable;
    @FXML
    private TableColumn<CarRental, String> colCar;
    @FXML
    private TableColumn<CarRental, LocalDate> colPickupDate;
    @FXML
    private TableColumn<CarRental, LocalDate> colReturnDate;
    @FXML
    private TableColumn<CarRental, Double> colPrice;
    @FXML
    private TableColumn<CarRental, String> colStatus;

    private final CarRentalService rentalService;
    private final SessionManager sessionManager;
    private ObservableList<CarRental> rentalList = FXCollections.observableArrayList();

    @Autowired
    public CustomerRentalHistoryController(CarRentalService rentalService, SessionManager sessionManager) {
        this.rentalService = rentalService;
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        colCar.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCar().getCarName()));
        colPickupDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId().getPickupDate()));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("rentPrice"));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));

        loadRentals();
    }

    private void loadRentals() {
        Customer current = sessionManager.getCurrentCustomer();
        if (current != null) {
            List<CarRental> rentals = rentalService.findByCustomer(current);
            rentalList.setAll(rentals);
            rentalTable.setItems(rentalList);
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        CarRental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a rental to cancel.");
            return;
        }

        if (selected.getStatus() != org.enums.RentalStatus.PENDING) {
            showAlert(Alert.AlertType.ERROR, "Status Error", "Only Pending rentals can be cancelled.");
            return;
        }

        selected.setStatus(org.enums.RentalStatus.CANCELLED);
        rentalService.save(selected);
        loadRentals();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Rental cancelled successfully.");
    }

    @FXML
    private void handleReturn(ActionEvent event) {
        CarRental selected = rentalTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a rental to return.");
            return;
        }

        if (selected.getStatus() != org.enums.RentalStatus.ACTIVE) {
            showAlert(Alert.AlertType.ERROR, "Status Error", "Only Active rentals can be returned.");
            return;
        }

        selected.setStatus(org.enums.RentalStatus.EARLY_RETURN);
        rentalService.save(selected);
        loadRentals();
        showAlert(Alert.AlertType.INFORMATION, "Return Request", "Return request sent for approval.");
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadRentals();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
