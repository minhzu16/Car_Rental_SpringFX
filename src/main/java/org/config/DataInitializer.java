package org.config;

import org.entity.Account;
import org.entity.Customer;
import org.repository.AccountRepository;
import org.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final org.repository.CarProducerRepository producerRepository;
    private final org.repository.CarRepository carRepository;
    private final CustomPasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(AccountRepository accountRepository, CustomerRepository customerRepository, 
                           org.repository.CarProducerRepository producerRepository, 
                           org.repository.CarRepository carRepository,
                           CustomPasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.producerRepository = producerRepository;
        this.carRepository = carRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        System.out.println("DEBUG: Data Initialization started...");
        
        // 1. Create Accounts & Customers
        createAccountIfNotExists("admin", "admin123", "ADMIN", "Admin User", 
                "admin@example.com", "123 Admin St", "0987654321", "123456789", "ADM-123456");
        
        createAccountIfNotExists("user", "user123", "USER", "Standard User", 
                "user@example.com", "456 Main St", "0912345678", "987654321", "USR-112233");

        // 2. Create Car Fleet
        if (carRepository.count() == 0) {
            org.entity.CarProducer p1 = new org.entity.CarProducer();
            p1.setProducerName("Toyota");
            p1.setAddress("Japan");
            p1.setCountry("Japan");
            p1 = producerRepository.save(p1);

            org.entity.Car car1 = new org.entity.Car();
            car1.setCarName("Toyota Camry");
            car1.setCarModelYear(2022);
            car1.setColor("White");
            car1.setCapacity(5);
            car1.setDescription("Reliable sedan for long trips.");
            car1.setRentPrice(100.0);
            car1.setStatus("Active");
            car1.setImportDate(LocalDate.now().minusMonths(6));
            car1.setProducer(p1);
            car1.setImageUrl("camry.jpg");
            carRepository.save(car1);

            org.entity.Car car2 = new org.entity.Car();
            car2.setCarName("Honda Civic");
            car2.setCarModelYear(2021);
            car2.setColor("Black");
            car2.setCapacity(5);
            car2.setDescription("Sporty and fuel efficient.");
            car2.setRentPrice(80.0);
            car2.setStatus("Active");
            car2.setImportDate(LocalDate.now().minusMonths(12));
            car2.setProducer(p1);
            car2.setImageUrl("civic.jpg");
            carRepository.save(car2);
            
            System.out.println("DEBUG: Car fleet initialized.");
        }
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