package org.service;

import lombok.RequiredArgsConstructor;

import org.entity.CarProducer;
import org.repository.CarProducerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarProducerService {
    private final CarProducerRepository repository;

    public List<CarProducer> findAll(){return repository.findAll();}
    public Optional<CarProducer> findById(Integer id){return repository.findById(id);}    
    public CarProducer save(CarProducer p){return repository.save(p);}    
    public void delete(Integer id){repository.deleteById(id);}    
}
