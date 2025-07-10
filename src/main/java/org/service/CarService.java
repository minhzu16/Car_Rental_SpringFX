package org.service;

import org.entity.Car;
import org.enums.Operation;
import org.utils.filter.SearchCriteria;
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
    
    List<Car> findAll(List<SearchCriteria> criterias);
    
    List<Car> findAll(List<SearchCriteria> criterias, Operation logicalOperation);
    
    Page<Car> findByNameOrDescription(String query, Pageable pageable);
    
    Page<Car> findByStatus(String status, Pageable pageable);
}
