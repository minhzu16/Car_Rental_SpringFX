package org.controller;

import org.entity.Account;
import org.entity.Customer;
import org.service.AccountService;
import org.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final AccountService accountService;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AccountService accountService, CustomerService customerService, PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error,
                               @RequestParam(required = false) String message,
                               @RequestParam(required = false) String logout,
                               Model model) {
        logger.info("Hiển thị form đăng nhập");
        
        if (error != null) {
            // Use a fixed error message instead of trying to decode from URL
            String errorMessage = "Tên đăng nhập hoặc mật khẩu không hợp lệ";
            logger.warn("Lỗi đăng nhập");
            model.addAttribute("error", errorMessage);
        }
        
        if (logout != null) {
            logger.info("Người dùng đã đăng xuất");
            model.addAttribute("success", "Bạn đã đăng xuất thành công");
        }
        
        // Kiểm tra nếu người dùng đã đăng nhập
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            logger.info("Người dùng đã được xác thực là: {}", auth.getName());
            return "redirect:/login-success";
        }
        
        return "html/login";
    }
    
    @GetMapping("/login-success")
    public String loginSuccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Đăng nhập thành công cho người dùng: {}", auth.getName());
        
        // Kiểm tra quyền và chuyển hướng tương ứng
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            logger.info("Chuyển hướng ADMIN đến trang quản trị");
            return "redirect:/admin";
        } else {
            logger.info("Chuyển hướng USER đến trang chủ");
            return "redirect:/home";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        logger.info("Hiển thị form đăng ký");
        model.addAttribute("customer", new Customer());
        model.addAttribute("account", new Account());
        return "html/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute Customer customer, 
                              @ModelAttribute Account account,
                              RedirectAttributes redirectAttributes) {
        logger.info("Đang xử lý đăng ký cho tên đăng nhập: {}", account.getAccountName());
        
        // Kiểm tra tài khoản đã tồn tại chưa
        if (accountService.existsByUsername(account.getAccountName())) {
            logger.warn("Đăng ký thất bại - tên đăng nhập đã tồn tại: {}", account.getAccountName());
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã tồn tại");
            return "redirect:/register";
        }

        // Kiểm tra email đã tồn tại chưa
        if (customerService.findByEmail(customer.getEmail()) != null) {
            logger.warn("Đăng ký thất bại - email đã tồn tại: {}", customer.getEmail());
            redirectAttributes.addFlashAttribute("error", "Email đã tồn tại");
            return "redirect:/register";
        }

        try {
            // Thiết lập role
        account.setRole("USER"); // Mặc định là USER
        
            // Mã hóa mật khẩu với jBCrypt trước khi lưu
            String encodedPassword = passwordEncoder.encode(customer.getPassword());
            account.setPassword(encodedPassword);
            customer.setPassword(encodedPassword);
            
            logger.info("Mật khẩu đã được mã hóa với jBCrypt");
            
            // Lưu account và customer
            Account savedAccount = accountService.saveWithoutEncoding(account);
            customer.setAccount(savedAccount);
        customerService.save(customer);
            
            logger.info("Đăng ký thành công cho người dùng: {}", account.getAccountName());
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công. Vui lòng đăng nhập.");
        return "redirect:/login";
        } catch (Exception e) {
            logger.error("Lỗi trong quá trình đăng ký: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Đăng ký thất bại: " + e.getMessage());
            return "redirect:/register";
        }
    }
    
    // Thêm endpoint để debug thông tin đăng nhập
    @GetMapping("/auth-status")
    public String getAuthStatus(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            logger.info("Trạng thái xác thực - Tên: {}, Đã xác thực: {}, Principal: {}, Quyền: {}", 
                auth.getName(), auth.isAuthenticated(), auth.getPrincipal(), auth.getAuthorities());
            
            model.addAttribute("username", auth.getName());
            model.addAttribute("authenticated", auth.isAuthenticated());
            model.addAttribute("authorities", auth.getAuthorities());
            model.addAttribute("principal", auth.getPrincipal().toString());
        } else {
            logger.warn("Trạng thái xác thực - Không tìm thấy thông tin xác thực");
            model.addAttribute("error", "Không tìm thấy thông tin xác thực");
        }
        
        return "html/auth-status";
    }
}