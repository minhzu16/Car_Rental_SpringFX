package org.config;

import org.entity.Account;
import org.entity.Customer;
import org.repository.AccountRepository;
import org.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(AccountRepository accountRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create admin accounts only if they don't already exist
        // (SQL scripts might already have created some accounts)
        
        // Create admin account if it doesn't exist
        createAccountIfNotExists("admin", "admin123", "ADMIN", "Admin User", 
                "admin@example.com", "123 Admin St", "0987654321", "123456789", "ADM-123456");
        
        // Create manager account if it doesn't exist
        createAccountIfNotExists("user", "user123", "USER", "User", 
                "user@example.com", "456 Manager St", "0912345678", "987654321", "MNG-123456");
    }
    
    private void createAccountIfNotExists(String username, String password, String role, 
                                         String fullName, String email, String address, 
                                         String mobile, String identityCard, String licenceNumber) {
        // Check if account already exists
        if (accountRepository.findByAccountName(username) == null) {
            // Create new account
            Account account = new Account();
            account.setAccountName(username);
            account.setPassword(passwordEncoder.encode(password));
            account.setRole(role);
            
            // Save account to get AccountID
            Account savedAccount = accountRepository.save(account);
            
            // Create customer information
            Customer customer = new Customer();
            customer.setCustomerName(fullName);
            customer.setEmail(email);
            customer.setAddress(address);
            customer.setMobile(mobile);
            customer.setIdentityCard(identityCard);
            customer.setLicenceNumber(licenceNumber);
            customer.setLicenceDate(LocalDate.now());
            customer.setBirthday(LocalDate.now().minusYears(25));
            customer.setAccount(savedAccount);
            
            // Add password for Customer (using the same encoded password)
            customer.setPassword(passwordEncoder.encode(password));
            
            // Save customer information
            customerRepository.save(customer);
            
            System.out.println("Created " + role + " account: " + username);
        }
    }
} 