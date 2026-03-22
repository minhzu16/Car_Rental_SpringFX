package org.service.impl;

import org.entity.Car;
import org.repository.CarRepository;
import org.repository.CarRentalRepository;
import org.service.CarService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarRentalRepository carRentalRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, CarRentalRepository carRentalRepository) {
        this.carRepository = carRepository;
        this.carRentalRepository = carRentalRepository;
    }

    @Override
    public List<Car> findAll() {
        return carRepository.findAll();
    }

    @Override
    public Page<Car> findAll(Pageable pageable) {
        return carRepository.findAll(pageable != null ? pageable : Pageable.unpaged());
    }

    @Override
    public Optional<Car> findById(Integer id) {
        return carRepository.findById(id != null ? id : -1);
    }

    @Override
    public Car save(Car car) {
        if (car == null) throw new IllegalArgumentException("Car cannot be null");
        return carRepository.save(car);
    }

    @Override
    public void delete(Integer id) {
        if (id == null) return;
        if (carRentalRepository.existsByCarCarID(id)) {
            Car car = carRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Car not found with id " + id));
            car.setStatus("Deleted");
            carRepository.save(car);
        } else {
            carRepository.deleteById(id);
        }
    }

    @Override
    public Car update(Integer id, Car car) {
        if (id == null || car == null) throw new IllegalArgumentException("ID and Car cannot be null");
        Car existingCar = carRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Car not found with id " + id));
        BeanUtils.copyProperties(car, (Object)existingCar, "carID");
        return carRepository.save(existingCar);
    }

    @Override
    public List<Car> findAllAvailable() {
        return carRepository.findByStatus("Available", Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Car> findByStatus(String status, Pageable pageable) {
        return carRepository.findByStatus(status, pageable != null ? pageable : Pageable.unpaged());
    }

    @Override
    public Page<Car> findByNameOrDescription(String query, Pageable pageable) {
        return carRepository.findByNameOrDescriptionContainingIgnoreCase(query, pageable != null ? pageable : Pageable.unpaged());
    }
}
