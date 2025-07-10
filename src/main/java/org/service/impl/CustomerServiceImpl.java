package org.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.entity.Account;
import org.entity.Customer;
import org.repository.CustomerRepository;
import org.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.utils.filter.SearchCriteria;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    
    // Email validation regex
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    
    // Phone number validation regex (simple version)
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{10,15}$");

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
    
    @Override
    public Page<Customer> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    public List<Customer> findAll(List<SearchCriteria> criterias) {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findById(Integer id) {
        return customerRepository.findById(id);
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void delete(Integer id) {
        customerRepository.deleteById(id);
    }
    
    @Override
    public Customer update(Integer id, Customer customer) {
        Customer existingCustomer = customerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Customer not found with id " + id));
        
        // Validate customer data
        validateCustomerData(customer);
        
        // Only copy non-null, non-collection fields
        if (customer.getCustomerName() != null) {
            if (customer.getCustomerName().trim().isEmpty()) {
                throw new ValidationException("Customer name cannot be empty");
            }
            existingCustomer.setCustomerName(customer.getCustomerName().trim());
        }
        
        if (customer.getMobile() != null) {
            if (!PHONE_PATTERN.matcher(customer.getMobile()).matches()) {
                throw new ValidationException("Invalid phone number format");
            }
            existingCustomer.setMobile(customer.getMobile());
        }
        
        if (customer.getBirthday() != null) {
            LocalDate now = LocalDate.now();
            if (customer.getBirthday().isAfter(now)) {
                throw new ValidationException("Birthday cannot be in the future");
            }
            
            // Calculate age more precisely
            int age = now.getYear() - customer.getBirthday().getYear();
            // Adjust age if birthday hasn't occurred yet this year
            if (now.getMonthValue() < customer.getBirthday().getMonthValue() || 
                (now.getMonthValue() == customer.getBirthday().getMonthValue() && 
                 now.getDayOfMonth() < customer.getBirthday().getDayOfMonth())) {
                age--;
            }
            
            // Allow younger customers (16+) for learner permits
            if (age < 16) {
                throw new ValidationException("Customer must be at least 16 years old");
            }
            existingCustomer.setBirthday(customer.getBirthday());
        }
        
        if (customer.getIdentityCard() != null) {
            if (customer.getIdentityCard().trim().isEmpty()) {
                throw new ValidationException("Identity card number cannot be empty");
            }
            existingCustomer.setIdentityCard(customer.getIdentityCard().trim());
        }
        
        if (customer.getLicenceNumber() != null) {
            if (customer.getLicenceNumber().trim().isEmpty()) {
                throw new ValidationException("Licence number cannot be empty");
            }
            existingCustomer.setLicenceNumber(customer.getLicenceNumber().trim());
        }
        
        if (customer.getLicenceDate() != null) {
            LocalDate now = LocalDate.now();
            
            // Allow future license dates (for renewals or upcoming licenses)
            // But restrict to reasonable future (5 years max)
            if (customer.getLicenceDate().isAfter(now.plusYears(5))) {
                throw new ValidationException("License date cannot be more than 5 years in the future");
            }
            
            // Ensure license date is not before birthday + 16 years (for learner permits)
            if (customer.getBirthday() != null) {
                LocalDate minimumLicenseDate = customer.getBirthday().plusYears(16);
                if (customer.getLicenceDate().isBefore(minimumLicenseDate)) {
                    throw new ValidationException("License date cannot be before customer turns 16");
                }
            } else if (existingCustomer.getBirthday() != null) {
                LocalDate minimumLicenseDate = existingCustomer.getBirthday().plusYears(16);
                if (customer.getLicenceDate().isBefore(minimumLicenseDate)) {
                    throw new ValidationException("License date cannot be before customer turns 16");
                }
            }
            
            existingCustomer.setLicenceDate(customer.getLicenceDate());
        }
        
        if (customer.getEmail() != null) {
            if (!EMAIL_PATTERN.matcher(customer.getEmail()).matches()) {
                throw new ValidationException("Invalid email format");
            }
            existingCustomer.setEmail(customer.getEmail().trim());
        }
        
        if (customer.getPassword() != null && !customer.getPassword().trim().isEmpty()) {
            if (customer.getPassword().length() < 6) {
                throw new ValidationException("Password must be at least 6 characters long");
            }
            existingCustomer.setPassword(customer.getPassword());
        }
        
        // Don't touch account, rentals, or reviews
        
        logger.info("Updating customer with ID: {}", id);
        return customerRepository.save(existingCustomer);
    }
    
    private void validateCustomerData(Customer customer) {
        // Additional validation logic if needed
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
        System.out.println("DEBUG CustomerServiceImpl: Tìm khách hàng theo account ID " + account.getAccountID());
        Customer customer = customerRepository.findByAccount(account);
        if (customer == null) {
            System.out.println("DEBUG CustomerServiceImpl: Không tìm thấy khách hàng cho account ID " + account.getAccountID());
        } else {
            System.out.println("DEBUG CustomerServiceImpl: Tìm thấy khách hàng ID " + customer.getCustomerID() + " cho account ID " + account.getAccountID());
        }
        return customer;
    }
    
    @Override
    public Page<Customer> findByNameOrEmailContaining(String searchTerm, Pageable pageable) {
        return customerRepository.findByCustomerNameContainingOrEmailContaining(searchTerm, searchTerm, pageable);
    }
}
