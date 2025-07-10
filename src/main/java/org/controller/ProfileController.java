package org.controller;

import lombok.RequiredArgsConstructor;
import org.entity.Account;
import org.entity.Customer;
import org.service.AccountService;
import org.service.CustomerService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.utils.UserProfileValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final CustomerService customerService;
    private final AccountService accountService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");

    @GetMapping("/profile")
    public String showProfile(Model model) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }

        // Lấy thông tin tài khoản
        Account account = accountService.findByAccountName(authentication.getName());
        if (account == null) {
            return "redirect:/login";
        }

        // Lấy thông tin khách hàng
        Customer customer = customerService.findByAccount(account);
        if (customer == null) {
            return "redirect:/login";
        }

        // Thêm thông tin khách hàng vào model
        model.addAttribute("customer", customer);
        
        return "html/profile";
    }

    @PostMapping("/profile/validate")
    public String validateProfile(
            @RequestParam("customerName") String customerName,
            @RequestParam("email") String email,
            @RequestParam("address") String address,
            @RequestParam("mobile") String mobile,
            @RequestParam("identityCard") String identityCard,
            @RequestParam("licenceNumber") String licenceNumber,
            @RequestParam("licenceDate") String licenceDate,
            @RequestParam("birthday") String birthday,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }

        // Lấy thông tin tài khoản
        Account account = accountService.findByAccountName(authentication.getName());
        if (account == null) {
            return "redirect:/login";
        }

        // Lấy thông tin khách hàng
        Customer customer = customerService.findByAccount(account);
        if (customer == null) {
            return "redirect:/login";
        }

        // Validate tất cả các trường (đổi tên tham số để match với test case)
        Map<String, String> errors = UserProfileValidator.validateUserProfile(
                customerName, email, address, mobile, 
                identityCard, licenceNumber, licenceDate, birthday);

        // Nếu có lỗi, quay lại form với thông báo lỗi
        if (!errors.isEmpty()) {
            // Add back submitted form data
            model.addAttribute("customer", customer);
            model.addAttribute("customerName", customerName);
            model.addAttribute("email", email);
            model.addAttribute("address", address);
            model.addAttribute("mobile", mobile);
            model.addAttribute("identityCard", identityCard);
            model.addAttribute("licenceNumber", licenceNumber);
            model.addAttribute("licenceDate", licenceDate);
            model.addAttribute("birthday", birthday);
            
            // Add all errors to the model
            model.addAllAttributes(errors);
            
            return "html/profile";
        }

        try {
            // Update the existing customer object instead of creating a new one
            customer.setCustomerName(customerName);
            customer.setEmail(email);
            customer.setAddress(address);
            customer.setMobile(mobile);
            customer.setIdentityCard(identityCard);
            customer.setLicenceNumber(licenceNumber);
            
            // Parse and set dates
            if (licenceDate != null && !licenceDate.isEmpty()) {
                customer.setLicenceDate(LocalDate.parse(licenceDate, DATE_FORMATTER));
            }
            
            if (birthday != null && !birthday.isEmpty()) {
                customer.setBirthday(LocalDate.parse(birthday, DATE_FORMATTER));
            }
            
            // Save customer directly
            customerService.save(customer);
            
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Error updating profile: " + e.getMessage());
            // Add back submitted form data in case of error
            model.addAttribute("customer", customer);
            model.addAttribute("customerName", customerName);
            model.addAttribute("email", email);
            model.addAttribute("address", address);
            model.addAttribute("mobile", mobile);
            model.addAttribute("identityCard", identityCard);
            model.addAttribute("licenceNumber", licenceNumber);
            model.addAttribute("licenceDate", licenceDate);
            model.addAttribute("birthday", birthday);
            return "html/profile";
        }
    }
} 