package org.controller;

import lombok.RequiredArgsConstructor;
import org.entity.Car;
import org.enums.Operation;
import org.service.FilterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.utils.filter.FilterUtils;
import org.utils.filter.SearchCriteria;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cars/filter")
@RequiredArgsConstructor
public class CarFilterController {
    
    private final FilterService<Car> carFilterService;
    
    @PostMapping
    public ResponseEntity<List<Car>> filterCars(@RequestBody List<SearchCriteria> criteria) {
        return ResponseEntity.ok(carFilterService.filter(criteria));
    }
    
    @PostMapping("/operation")
    public ResponseEntity<List<Car>> filterCarsWithOperation(
            @RequestBody List<SearchCriteria> criteria,
            @RequestParam(defaultValue = "AND") Operation operation) {
        return ResponseEntity.ok(carFilterService.filter(criteria, operation));
    }
    
    @PostMapping("/map")
    public ResponseEntity<List<Car>> filterCarsWithMap(@RequestBody Map<String, Object> filters) {
        return ResponseEntity.ok(carFilterService.filterByMap(filters));
    }
    
    @PostMapping("/map/operation")
    public ResponseEntity<List<Car>> filterCarsWithMapAndOperation(
            @RequestBody Map<String, Object> filters,
            @RequestParam(defaultValue = "AND") Operation operation) {
        return ResponseEntity.ok(carFilterService.filterByMap(filters, operation));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Car>> searchCars(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) String status) {
        
        List<SearchCriteria> criteria = FilterUtils.buildSearchCriteria(
                type, brand, color, minPrice, maxPrice, minYear, maxYear, capacity, status);
        
        return ResponseEntity.ok(carFilterService.filter(criteria));
    }
} 