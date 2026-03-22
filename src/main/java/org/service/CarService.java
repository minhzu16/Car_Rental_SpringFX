package org.service;

import org.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CarService {
    List<Car> findAll();
    Page<Car> findAll(Pageable pageable);
    Optional<Car> findById(Integer id);
    Car save(Car car);
    void delete(Integer id);
    Car update(Integer id, Car car);
    List<Car> findAllAvailable();
    Page<Car> findByStatus(String status, Pageable pageable);
    Page<Car> findByNameOrDescription(String query, Pageable pageable);
}
