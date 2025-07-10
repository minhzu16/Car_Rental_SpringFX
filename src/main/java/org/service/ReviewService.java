package org.service;

import lombok.RequiredArgsConstructor;

import org.entity.Review;
import org.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository repository;
    public Review save(Review r){return repository.save(r);}    
    public List<Review> findAll(){return repository.findAll();}
}
