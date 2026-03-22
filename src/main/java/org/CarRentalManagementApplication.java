package org;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = {
                "org.controller.fx",
                "org.service",
                "org.repository",
                "org.utils",
                "org.enums",
                "org.config",
                "org.exception",
                "org.entity"
        })
@EntityScan("org.entity")
@EnableJpaRepositories("org.repository")
public class CarRentalManagementApplication {

    public static void main(String[] args) {
        Application.launch(FXApplication.class, args);
    }
}
