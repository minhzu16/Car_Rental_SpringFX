package org.service;
// package org.Service;

// // Deprecated composite file. All classes split to individual files.

// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.stereotype.Controller;
// import org.springframework.stereotype.Service;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;

// /* ======== SERVICES ======== */

// @Service
// @RequiredArgsConstructor
// class CarProducerService {
//     private final CarProducerRepository repo;
//     public List<CarProducer> findAll() {return repo.findAll();}
//     public Optional<CarProducer> findById(Long id){return repo.findById(id);}    
//     public CarProducer save(CarProducer e){return repo.save(e);}    
//     public void delete(Long id){repo.deleteById(id);} }

// @Service
// @RequiredArgsConstructor
// class CustomerService {
//     private final CustomerRepository repo;
//     public List<Customer> findAll(){return repo.findAll();}
//     public Optional<Customer> findById(Long id){return repo.findById(id);}    
//     public Customer save(Customer c){return repo.save(c);}    
//     public void delete(Long id){repo.deleteById(id);}    
//     public Customer findByEmail(String email){return repo.findByEmail(email);} }

// @Service
// @RequiredArgsConstructor
// class AccountService {
//     private final AccountRepository repo;
//     public Account save(Account a){return repo.save(a);}    
//     public Account findByAccountName(String n){return repo.findByAccountName(n);} }

// @Service
// @RequiredArgsConstructor
// class CarRentalService {
//     private final CarRentalRepository repo;
//     public CarRental save(CarRental r){return repo.save(r);}    
//     public List<CarRental> findAll(){return repo.findAll();}
//     public List<CarRental> generateRentalReport(LocalDate s, LocalDate e){return repo.findByPickupDateBetweenOrderByPickupDateDesc(s,e);}    
//     public List<CarRental> findByCustomerId(Long id){return repo.findByCustomerCustomerID(id);} }

// @Service
// @RequiredArgsConstructor
// class ReviewService {
//     private final ReviewRepository repo;
//     public Review save(Review r){return repo.save(r);}    
//     public List<Review> findAll(){return repo.findAll();} }

// /* ======== CONTROLLERS ======== */

// @Controller
// class HomeController {
//     @GetMapping({"/","/home"})
//     public String home(){return "html/home";}
// }

// @Controller
// @RequestMapping("/admin")
// class AdminController {
//     @GetMapping
//     public String adminPage(){return "html/admin";}
// }

// @Controller
// @RequiredArgsConstructor
// class AuthController {
//     private final AccountService accountService;
//     private final CustomerService customerService;
//     private final BCryptPasswordEncoder encoder;

//     @GetMapping("/login")
//     public String login(){return "html/login";}

//     @GetMapping("/register")
//     public String registerForm(Model model){
//         model.addAttribute("customer", new Customer());
//         return "html/register";     
//     }

//     @PostMapping("/register")
//     public String register(@ModelAttribute Customer customer, @RequestParam String accountName){
//         Account acc=new Account();
//         acc.setAccountName(accountName);
//         acc.setRole("Customer");
//         accountService.save(acc);
//         customer.setAccount(acc);
//         // encode password before persisting
//         customer.setPassword(encoder.encode(customer.getPassword()));
//         customerService.save(customer);
//         return "redirect:/login";
//     }
// }

// /* ======== SECURITY CONFIG ======== */
// @Configuration
// @EnableWebSecurity
// class SecurityConfig {
//     @Bean
//     BCryptPasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}

//     @Bean
//     SecurityFilterChain filterChain(HttpSecurity http, AccountRepository accountRepo) throws Exception {
//         http.csrf().disable();
//         http.authorizeHttpRequests(auth->auth
//                 .requestMatchers("/css/**","/js/**","/imgs/**","/login","/register","/").permitAll()
//                 .requestMatchers("/admin/**").authenticated()
//                 .anyRequest().permitAll());
//         http.formLogin(form->form.loginPage("/login").defaultSuccessUrl("/admin",true).permitAll());
//         http.logout(l->l.logoutSuccessUrl("/login?logout").permitAll());
//         http.userDetailsService(userDetailsService(accountRepo));
//         return http.build();
//     }

//     @Bean
//     UserDetailsService userDetailsService(AccountRepository accountRepo){
//         return username->{
//             Account a=accountRepo.findByAccountName(username);
//             if(a==null) throw new UsernameNotFoundException("User not found");
//             String pwd=a.getCustomer()!=null?a.getCustomer().getPassword():"";
//             return org.springframework.security.core.userdetails.User.withUsername(a.getAccountName())
//                     .password(pwd)
//                     .roles(a.getRole())
//                     .build();
//         };}
// }
