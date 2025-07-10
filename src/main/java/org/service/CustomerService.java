package org.service;

import org.entity.Account;
import org.entity.Customer;
import org.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.utils.filter.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> findAll();
    
    Page<Customer> findAll(Pageable pageable);
    
    List<Customer> findAll(List<SearchCriteria> criterias);
    
    Optional<Customer> findById(Integer id);
    
    Customer save(Customer customer);
    
    void delete(Integer id);
    
    Customer update(Integer id, Customer customer);
    
    Customer findByEmail(String email);

    Customer findByAccount(Account account);
    
    Page<Customer> findByNameOrEmailContaining(String searchTerm, Pageable pageable);
}
