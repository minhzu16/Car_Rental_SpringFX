package org.service;

import org.entity.Account;
import org.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> findAll();
    Page<Customer> findAll(Pageable pageable);
    Optional<Customer> findById(Integer id);
    Customer save(Customer customer);
    void delete(Integer id);
    Customer update(Integer id, Customer customer);
    Customer findByEmail(String email);
    Customer findByAccount(Account account);
    Page<Customer> findByNameOrEmailContaining(String searchTerm, Pageable pageable);
}
