package org.model.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarResponse {
    private Long id;
    private String brand;
    private String model;
    private String plateNumber;
    private String type;
    private int year;
    private int seating;
    private String transmission;
    private double ratePerDay;
    private String region;
    private String powertrain;
    private String color;
    private String status;

}
