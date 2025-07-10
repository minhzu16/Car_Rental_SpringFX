package org.service.impl;

import org.entity.Car;
import org.repository.CarRepository;
import org.service.CarService;
import org.enums.Operation;
import org.utils.filter.FilterSpecification;
import org.utils.filter.SearchCriteria;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final FilterSpecification<Car> filterSpecification;
    
    @Autowired
    public CarServiceImpl(CarRepository carRepository, FilterSpecification<Car> filterSpecification) {
        this.carRepository = carRepository;
        this.filterSpecification = filterSpecification;
    }

    @Override
    public List<Car> findAll() {
        return carRepository.findAll();
    }

    @Override
    public Page<Car> findAll(Pageable pageable) {
        return carRepository.findAll(pageable);
    }

    @Override
    public Optional<Car> findById(Integer id) {
        return carRepository.findById(id);
    }

    @Override
    public Car save(Car car) {
        return carRepository.save(car);
    }

    @Override
    public void delete(Integer id) {
        carRepository.deleteById(id);
    }

    @Override
    public Car update(Integer id, Car car) {
        Car existingCar = carRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Car not found with id " + id));
        BeanUtils.copyProperties(car, existingCar, "carID");
        return carRepository.save(existingCar);
    }

    @Override
    public List<Car> findAllAvailable() {
        return carRepository.findByStatus("Available", Pageable.unpaged()).getContent();
    }

    @Override
    public List<Car> findAll(List<SearchCriteria> criterias) {
        return findAll(criterias, Operation.AND);
    }

    @Override
    public List<Car> findAll(List<SearchCriteria> criterias, Operation logicalOperation) {
        if (criterias == null || criterias.isEmpty()) {
        return carRepository.findAll();
        }
        
        FilterSpecification<Car> spec = new FilterSpecification<>();
        
        for (SearchCriteria criteria : criterias) {
            spec.addSearchCriteria(criteria, logicalOperation);
        }
        
        return carRepository.findAll(spec.getSpecification());
    }
    
    @Override
    public Page<Car> findByNameOrDescription(String query, Pageable pageable) {
        return carRepository.findByNameOrDescriptionContainingIgnoreCase(query, pageable);
    }
    
    @Override
    public Page<Car> findByStatus(String status, Pageable pageable) {
        return carRepository.findByStatus(status, pageable);
    }
}
