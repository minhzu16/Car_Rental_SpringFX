package org.controller;

import lombok.RequiredArgsConstructor;
import org.entity.Customer;
import org.entity.CarRental;
import org.service.CustomerService;
import org.service.CarRentalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCustomerController {
    private final CustomerService customerService;
    private final CarRentalService carRentalService;

    @GetMapping("/customers")
    public String listCustomers(Model model, 
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customersPage;
        
        // Nếu có từ khóa tìm kiếm
        if (search != null && !search.isEmpty()) {
            customersPage = customerService.findByNameOrEmailContaining(search, pageable);
        } else {
            customersPage = customerService.findAll(pageable);
        }
        
        List<Customer> customers = customersPage.getContent();
        
        // Lấy lịch sử thuê xe cho mỗi khách hàng
        Map<Integer, List<CarRental>> customerRentals = new HashMap<>();
        for (Customer customer : customers) {
            List<CarRental> rentals = carRentalService.findByCustomerId(customer.getCustomerID());
            customerRentals.put(customer.getCustomerID(), rentals);
        }
        
        model.addAttribute("customers", customers);
        model.addAttribute("customerRentals", customerRentals);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customersPage.getTotalPages());
        model.addAttribute("totalItems", customersPage.getTotalElements());
        model.addAttribute("search", search);
        
        // Thêm flag để phân biệt trang
        model.addAttribute("isCustomersPage", true);
        
        return "html/customers";
    }
    
    @GetMapping("/customer/{id}")
    public String viewCustomerDetails(@PathVariable("id") Integer customerId, Model model) {
        Optional<Customer> customerOpt = customerService.findById(customerId);
        
        if (customerOpt.isEmpty()) {
            return "redirect:/admin/customers";
        }
        
        Customer customer = customerOpt.get();
        List<CarRental> rentals = carRentalService.findByCustomerId(customerId);
        
        model.addAttribute("customer", customer);
        model.addAttribute("rentals", rentals);
        model.addAttribute("isCustomersPage", true);
        
        return "html/customer-details";
    }
    
    @GetMapping("/customer/{id}/rentals")
    public String viewCustomerRentals(@PathVariable("id") Integer customerId, Model model) {
        Optional<Customer> customerOpt = customerService.findById(customerId);
        
        if (customerOpt.isEmpty()) {
            return "redirect:/admin/customers";
        }
        
        Customer customer = customerOpt.get();
        List<CarRental> rentals = carRentalService.findByCustomerId(customerId);
        
        model.addAttribute("customer", customer);
        model.addAttribute("rentals", rentals);
        model.addAttribute("isCustomersPage", true);
        
        return "html/customer-rentals";
    }
} 