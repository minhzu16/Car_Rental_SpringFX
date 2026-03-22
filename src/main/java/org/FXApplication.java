package org;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.config.SpringFXMLLoader;

import java.io.IOException;

public class FXApplication extends Application {
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(CarRentalManagementApplication.class).run();
    }

    @Override
    public void start(Stage stage) throws IOException {
        SpringFXMLLoader loader = applicationContext.getBean(SpringFXMLLoader.class);
        Parent root = loader.load("/fxml/login.fxml");
        stage.setScene(new Scene(root, 800, 500));
        stage.setTitle("FUCarRentingSystem - Login");
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }
}
