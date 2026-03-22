package org.service.impl;

import java.util.List;
import java.util.Optional;

import org.entity.Account;
import org.entity.Customer;
import org.repository.CustomerRepository;
import org.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
    
    @Override
    public Page<Customer> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable != null ? pageable : Pageable.unpaged());
    }

    @Override
    public Optional<Customer> findById(Integer id) {
        return customerRepository.findById(id != null ? id : -1);
    }

    @Override
    public Customer save(Customer customer) {
        if (customer == null) throw new IllegalArgumentException("Customer cannot be null");
        return customerRepository.save(customer);
    }

    @Override
    public void delete(Integer id) {
        if (id == null) return;
        customerRepository.deleteById(id);
    }
    
    @Override
    public Customer update(Integer id, Customer customer) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        Customer existingCustomer = customerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Customer not found with id " + id));
        
        validateCustomerData(customer);
        
        if (customer.getCustomerName() != null) {
            existingCustomer.setCustomerName(customer.getCustomerName().trim());
        }
        
        if (customer.getMobile() != null) {
            existingCustomer.setMobile(customer.getMobile());
        }
        
        if (customer.getBirthday() != null) {
            existingCustomer.setBirthday(customer.getBirthday());
        }
        
        if (customer.getIdentityCard() != null) {
            existingCustomer.setIdentityCard(customer.getIdentityCard().trim());
        }
        
        if (customer.getLicenceNumber() != null) {
            existingCustomer.setLicenceNumber(customer.getLicenceNumber().trim());
        }
        
        if (customer.getLicenceDate() != null) {
            existingCustomer.setLicenceDate(customer.getLicenceDate());
        }
        
        if (customer.getEmail() != null) {
            existingCustomer.setEmail(customer.getEmail().trim());
        }
        
        if (customer.getPassword() != null && !customer.getPassword().trim().isEmpty()) {
            existingCustomer.setPassword(customer.getPassword());
        }
        
        logger.info("Updating customer with ID: {}", id);
        return customerRepository.save(existingCustomer);
    }
    
    private void validateCustomerData(Customer customer) {
        if (customer == null) {
            throw new ValidationException("Customer data cannot be null");
        }
    }

    @Override
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email).orElse(null);
    }
    
    @Override
    public Customer findByAccount(Account account) {
        if (account == null) return null;
        return customerRepository.findByAccount(account);
    }
    
    @Override
    public Page<Customer> findByNameOrEmailContaining(String searchTerm, Pageable pageable) {
        return customerRepository.findByCustomerNameContainingOrEmailContaining(searchTerm, searchTerm, pageable != null ? pageable : Pageable.unpaged());
    }
}
