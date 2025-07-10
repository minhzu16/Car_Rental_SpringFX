package org.service.impl;

import lombok.RequiredArgsConstructor;
import org.entity.Car;
import org.enums.Operation;
import org.repository.CarRepository;
import org.service.FilterService;
import org.springframework.stereotype.Service;
import org.utils.filter.FilterSpecification;
import org.utils.filter.FilterUtils;
import org.utils.filter.SearchCriteria;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CarFilterServiceImpl implements FilterService<Car> {
    
    private final CarRepository carRepository;
    
    @Override
    public List<Car> filter(List<SearchCriteria> criteria) {
        return filter(criteria, Operation.AND);
    }
    
    @Override
    public List<Car> filter(List<SearchCriteria> criteria, Operation operation) {
        if (criteria == null || criteria.isEmpty()) {
            return carRepository.findAll();
        }
        
        FilterSpecification<Car> spec = new FilterSpecification<>();
        
        for (SearchCriteria criterion : criteria) {
            spec.addSearchCriteria(criterion, operation);
        }
        
        return carRepository.findAll(spec.getSpecification());
    }
    
    @Override
    public List<Car> filterByMap(Map<String, Object> filters) {
        return filterByMap(filters, Operation.AND);
    }
    
    @Override
    public List<Car> filterByMap(Map<String, Object> filters, Operation operation) {
        List<SearchCriteria> criteria = FilterUtils.convertToSearchCriteria(filters);
        return filter(criteria, operation);
    }
} 