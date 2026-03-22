package org.controller.fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.entity.Car;
import org.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.config.SpringFXMLLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

import org.springframework.context.annotation.Scope;

@Controller
@Scope("prototype")
public class CarManagementController {

    @FXML
    private TableView<Car> carTable;
    @FXML
    private TableColumn<Car, Integer> colID;
    @FXML
    private TableColumn<Car, String> colName;
    @FXML
    private TableColumn<Car, String> colModel;
    @FXML
    private TableColumn<Car, String> colColor;
    @FXML
    private TableColumn<Car, Double> colPrice;
    @FXML
    private TableColumn<Car, String> colStatus;
    
    @FXML
    private TextField searchField;

    private final CarService carService;
    private final SpringFXMLLoader fxmlLoader;
    private ObservableList<Car> carList = FXCollections.observableArrayList();

    @Autowired
    public CarManagementController(CarService carService, SpringFXMLLoader fxmlLoader) {
        this.carService = carService;
        this.fxmlLoader = fxmlLoader;
    }

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("carID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("carName"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("carModelYear"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("rentPrice"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadCars();
    }

    private void loadCars() {
        carList.setAll(carService.findAll());
        carTable.setItems(carList);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadCars();
        } else {
            carList.setAll(carService.findByNameOrDescription(query, null).getContent());
        }
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        openCarDialog(null);
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        Car selected = carTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a car to edit.");
            return;
        }
        openCarDialog(selected);
    }

    private void openCarDialog(Car car) {
        try {
            FXMLLoader loader = fxmlLoader.getLoader("/fxml/car_form.fxml");
            Parent root = loader.load();
            
            CarFormController controller = loader.getController();
            controller.setCarInfo(car, this::loadCars);
            
            Stage stage = new Stage();
            stage.setTitle(car == null ? "Add Car" : "Edit Car");
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
        Car selected = carTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a car to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Are you sure you want to delete this car?");
        Optional<ButtonType> result = confirm.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            carService.delete(selected.getCarID());
            loadCars();
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadCars();
        searchField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
