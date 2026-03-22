package org.controller.fx;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.entity.Car;
import org.entity.CarProducer;
import org.service.CarService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;

@Controller
@Scope("prototype")
public class CarFormController {

    @FXML private TextField nameField;
    @FXML private TextField modelYearField;
    @FXML private TextField colorField;
    @FXML private TextField capacityField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Label errorLabel;
    @FXML private Label titleLabel;

    private final CarService carService;
    private Car currentCar;
    private Runnable onSaveCallback;

    public CarFormController(CarService carService) {
        this.carService = carService;
    }

    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList("Active", "Maintenance", "Rented"));
        statusComboBox.setValue("Active");
    }

    public void setCarInfo(Car car, Runnable onSaveCallback) {
        this.onSaveCallback = onSaveCallback;
        if (car != null) {
            this.currentCar = car;
            titleLabel.setText("Edit Car: " + car.getCarName());
            nameField.setText(car.getCarName());
            modelYearField.setText(String.valueOf(car.getCarModelYear()));
            colorField.setText(car.getColor());
            capacityField.setText(String.valueOf(car.getCapacity()));
            priceField.setText(String.valueOf(car.getRentPrice()));
            statusComboBox.setValue(car.getStatus());
        } else {
            this.currentCar = new Car();
            titleLabel.setText("Add New Car");
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            currentCar.setCarName(nameField.getText().trim());
            currentCar.setCarModelYear(Integer.parseInt(modelYearField.getText().trim()));
            currentCar.setColor(colorField.getText().trim());
            currentCar.setCapacity(Integer.parseInt(capacityField.getText().trim()));
            currentCar.setRentPrice(Double.parseDouble(priceField.getText().trim()));
            currentCar.setStatus(statusComboBox.getValue());
            
            // Dummy values for unimplemented UI fields to satisfy DB constraints
            if (currentCar.getCarID() == null) {
                currentCar.setDescription(currentCar.getCarName() + " description");
                currentCar.setImportDate(LocalDate.now());
                currentCar.setImageUrl("default.jpg");
                // Note: car requires a producer, assuming producer id=1 exists
                CarProducer p = new CarProducer();
                p.setProducerID(1);
                currentCar.setProducer(p);
            }

            if (currentCar.getCarID() == null) {
                carService.save(currentCar);
            } else {
                carService.update(currentCar.getCarID(), currentCar);
            }
            
            if (onSaveCallback != null) onSaveCallback.run();
            closeWindow(event);
        } catch (Exception e) {
            System.err.println("Error saving car: " + e.getMessage());
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
