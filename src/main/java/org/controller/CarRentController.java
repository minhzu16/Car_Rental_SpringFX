package org.controller;

import lombok.RequiredArgsConstructor;
import org.entity.Account;
import org.entity.Car;
import org.entity.CarRental;
import org.entity.CarRentalKey;
import org.entity.Customer;
import org.enums.CarStatus;
import org.enums.RentalStatus;
import org.service.AccountService;
import org.service.CarRentalService;
import org.service.CarService;
import org.service.CustomerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CarRentController {
    private final CarRentalService carRentalService;
    private final CarService carService;
    private final CustomerService customerService;
    private final AccountService accountService;

    @GetMapping("/rentals")
    public String getAll(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        model.addAttribute("page", carRentalService.findAll(pageable));
        return "html/rentals";
    }

    @GetMapping("/delete-rental")
    public String delete(@RequestParam int customerId, @RequestParam int carId, @RequestParam String pickupDate) {
        carRentalService.deleteById(customerId, carId, pickupDate);
        return "redirect:/rentals";
    }

    @GetMapping("/rental-form")
    public String showRentCarForm(@RequestParam("carId") Integer carId, Model model) {
        // Kiểm tra xác thực
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }
        
        // Lấy thông tin xe
        Optional<Car> carOpt = carService.findById(carId);
        if (carOpt.isEmpty()) {
            return "redirect:/";
        }
        
        model.addAttribute("car", carOpt.get());
        return "html/rental-form";
    }

    // Thêm mapping mới để tương thích với cả hai đường dẫn
    @GetMapping("/rent-car-form")
    public String showRentCarFormAlternative(@RequestParam("carId") Integer carId, Model model) {
        return showRentCarForm(carId, model);
    }
    
    @PostMapping("/rent-car")
    public String rentCar(
            @RequestParam("carId") Integer carId,
            @RequestParam("pickupDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam("returnDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
            @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
            @RequestParam(value = "notes", required = false) String notes,
            RedirectAttributes redirectAttributes) {
        
        // Kiểm tra xác thực
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }
        
        // Kiểm tra thông tin đầu vào
        if (pickupDate == null || returnDate == null) {
            redirectAttributes.addFlashAttribute("error", "Please select valid dates");
            return "redirect:/rental-form?carId=" + carId;
        }
        
        // Kiểm tra ngày thuê và ngày trả
        LocalDate today = LocalDate.now();
        if (pickupDate.isBefore(today.plusDays(1))) {
            redirectAttributes.addFlashAttribute("error", "Pickup date must be at least tomorrow");
            return "redirect:/rental-form?carId=" + carId;
        }
        
        if (returnDate.isBefore(pickupDate.plusDays(1))) {
            redirectAttributes.addFlashAttribute("error", "Return date must be at least 1 day after pickup date");
            return "redirect:/rental-form?carId=" + carId;
        }
        
        // Lấy thông tin xe và khách hàng
        Optional<Car> carOpt = carService.findById(carId);
        if (carOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Car not found");
            return "redirect:/";
        }
        Car car = carOpt.get();
        
        Account account = accountService.findByAccountName(authentication.getName());
        Customer customer = customerService.findByAccount(account);
        if (customer == null) {
            redirectAttributes.addFlashAttribute("error", "Customer information not found");
            return "redirect:/profile";
        }
        
        // Kiểm tra xe đã được thuê trong khoảng thời gian này chưa
        if (carRentalService.isCarRented(carId, pickupDate, returnDate)) {
            redirectAttributes.addFlashAttribute("error", "This car is already rented for the selected dates");
            return "redirect:/rental-form?carId=" + carId;
        }
        
        // Tạo đối tượng CarRental
        CarRentalKey key = new CarRentalKey(customer.getCustomerID(), carId, pickupDate);
        CarRental carRental = new CarRental();
        carRental.setId(key);
        carRental.setCar(car);
        carRental.setCustomer(customer);
        carRental.setReturnDate(returnDate);
        carRental.setStatus(RentalStatus.PENDING);
        
        // Tính giá thuê
        long days = java.time.temporal.ChronoUnit.DAYS.between(pickupDate, returnDate);
        double rentPrice = car.getRentPrice() * days;
        carRental.setRentPrice(rentPrice);
        
        // Lưu thông tin thuê xe
        carRentalService.save(carRental);
        
        // Cập nhật trạng thái xe thành Reserved (đợi admin duyệt)
        car.setStatus(CarStatus.RESERVED.getDisplayValue());
        carService.update(car.getCarID(), car);
        
        redirectAttributes.addFlashAttribute("success", "Rental request submitted and awaiting approval");
        return "redirect:/rental-history";
    }
    
    @GetMapping("/rental-history")
    public String showRentalHistory(Model model) {
        // Kiểm tra xác thực
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }
        
        // Lấy thông tin khách hàng
        Account account = accountService.findByAccountName(authentication.getName());
        Customer customer = customerService.findByAccount(account);
        if (customer == null) {
            return "redirect:/profile";
        }
        
        // Lấy danh sách thuê xe của khách hàng
        List<CarRental> rentals = carRentalService.findByCustomerId(customer.getCustomerID());
        model.addAttribute("rentals", rentals);
        
        return "html/rental-history";
    }
    
    @GetMapping("/rental-details")
    public String showRentalDetails(
            @RequestParam("customerId") Integer customerId,
            @RequestParam("carId") Integer carId,
            @RequestParam("pickupDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            Model model) {
        
        // Kiểm tra xác thực
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            System.out.println("DEBUG: Người dùng chưa xác thực");
            return "redirect:/login";
        }
        
        // Lấy thông tin tài khoản hiện tại
        Account account = accountService.findByAccountName(authentication.getName());
        System.out.println("DEBUG: Tên người dùng: " + authentication.getName());
        System.out.println("DEBUG: Vai trò người dùng: " + account.getRole());
        
        // Kiểm tra vai trò admin (kiểm tra chính xác và linh hoạt hơn)
        boolean isAdmin = account.getRole().contains("ADMIN") || account.getRole().contains("admin");
        System.out.println("DEBUG: Có phải admin không? " + isAdmin);
        
        if (!isAdmin) {
            // Chỉ kiểm tra quyền truy cập cho người dùng thông thường
            Customer currentCustomer = customerService.findByAccount(account);
            if (currentCustomer == null) {
                System.out.println("DEBUG: Không tìm thấy thông tin khách hàng");
                return "redirect:/profile";
            }
            
            System.out.println("DEBUG: ID khách hàng hiện tại: " + currentCustomer.getCustomerID());
            System.out.println("DEBUG: ID khách hàng yêu cầu: " + customerId);
            
            // Kiểm tra quyền truy cập (chỉ cho phép xem thông tin thuê xe của chính mình)
            if (!currentCustomer.getCustomerID().equals(customerId)) {
                System.out.println("DEBUG: Người dùng không có quyền xem chi tiết thuê xe này");
                return "redirect:/rental-history";
            }
        } else {
            System.out.println("DEBUG: Người dùng là admin, được phép xem chi tiết thuê xe");
        }
        
        // Lấy thông tin thuê xe
        CarRentalKey key = new CarRentalKey(customerId, carId, pickupDate);
        Optional<CarRental> rentalOpt = carRentalService.findById(key);
        
        if (rentalOpt.isEmpty()) {
            System.out.println("DEBUG: Không tìm thấy thông tin thuê xe");
            return isAdmin ? "redirect:/admin/rentals" : "redirect:/rental-history";
        }
        
        model.addAttribute("rental", rentalOpt.get());
        model.addAttribute("isAdmin", isAdmin);
        
        // Nếu là admin, chuyển đến trang riêng cho admin
        if (isAdmin) {
            System.out.println("DEBUG: Chuyển hướng đến trang admin-rental-details");
            return "html/admin-rental-details";
        }
        
        System.out.println("DEBUG: Chuyển hướng đến trang rental-details");
        return "html/rental-details";
    }
    
    @PostMapping("/cancel-rental")
    public String cancelRental(
            @RequestParam("customerId") Integer customerId,
            @RequestParam("carId") Integer carId,
            @RequestParam("pickupDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            RedirectAttributes redirectAttributes) {
        
        // Kiểm tra xác thực
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }
        
        // Lấy thông tin tài khoản hiện tại
        Account account = accountService.findByAccountName(authentication.getName());
        
        // Kiểm tra vai trò admin
        boolean isAdmin = "ROLE_ADMIN".equals(account.getRole());
        
        Customer currentCustomer = null;
        if (!isAdmin) {
            currentCustomer = customerService.findByAccount(account);
            if (currentCustomer == null) {
                return "redirect:/profile";
            }
            
            // Kiểm tra quyền truy cập (chỉ cho phép hủy thuê xe của chính mình)
            if (!currentCustomer.getCustomerID().equals(customerId)) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to cancel this rental");
                return "redirect:/rental-history";
            }
        }
        
        // Lấy thông tin thuê xe
        CarRentalKey key = new CarRentalKey(customerId, carId, pickupDate);
        Optional<CarRental> rentalOpt = carRentalService.findById(key);
        
        if (rentalOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Rental not found");
            return isAdmin ? "redirect:/admin/rentals" : "redirect:/rental-history";
        }
        
        CarRental rental = rentalOpt.get();
        
        // Kiểm tra trạng thái thuê xe
        if (isAdmin) {
            // Admin can cancel rentals in any status
            if (rental.getStatus() != RentalStatus.COMPLETED) {
                rental.setStatus(RentalStatus.CANCELLED);
            } else {
                redirectAttributes.addFlashAttribute("error", "Completed rentals cannot be cancelled");
                return "redirect:/admin/rentals";
            }
        } else {
            // Regular users can only cancel PENDING rentals
            if (rental.getStatus() != RentalStatus.PENDING) {
                redirectAttributes.addFlashAttribute("error", "Only pending rentals can be cancelled by users. For active rentals, please request an early return.");
                return "redirect:/rental-details?customerId=" + customerId + "&carId=" + carId + "&pickupDate=" + pickupDate;
            }
            
            // Cập nhật trạng thái
            rental.setStatus(RentalStatus.CANCELLED);
        }
        
        // Lưu thay đổi
        carRentalService.save(rental);
        
        // Cập nhật trạng thái xe thành Available
        Car car = rental.getCar();
        car.setStatus(CarStatus.AVAILABLE.getDisplayValue());
        carService.update(car.getCarID(), car);
        
        redirectAttributes.addFlashAttribute("success", "Rental cancelled successfully");
        return isAdmin ? "redirect:/admin/rentals" : "redirect:/rental-history";
    }

    @PostMapping("/update-rental-status")
    public String updateRentalStatus(
            @RequestParam("customerId") Integer customerId,
            @RequestParam("carId") Integer carId,
            @RequestParam("pickupDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {
        
        // Kiểm tra xác thực
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }
        
        // Kiểm tra quyền admin
        Account account = accountService.findByAccountName(authentication.getName());
        if (!"ROLE_ADMIN".equals(account.getRole())) {
            redirectAttributes.addFlashAttribute("error", "Only administrators can update rental status");
            return "redirect:/admin/rentals";
        }
        
        // Validate status
        RentalStatus rentalStatus;
        try {
            if (status == null || status.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Rental status cannot be empty");
                return "redirect:/admin/rentals";
            }
            rentalStatus = RentalStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid rental status");
            return "redirect:/admin/rentals";
        }
        
        // Lấy thông tin thuê xe
        CarRentalKey key = new CarRentalKey(customerId, carId, pickupDate);
        Optional<CarRental> rentalOpt = carRentalService.findById(key);
        
        if (rentalOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Rental not found");
            return "redirect:/admin/rentals";
        }
        
        CarRental rental = rentalOpt.get();
        Car car = rental.getCar();
        
        // Debug information
        System.out.println("DEBUG: Updating rental status from " + rental.getStatus() + " to " + rentalStatus);
        
        // Cập nhật trạng thái
        rental.setStatus(rentalStatus);
        
        // Cập nhật trạng thái xe dựa trên trạng thái thuê xe mới
        switch (rentalStatus) {
            case ACTIVE:
                car.setStatus(CarStatus.RENTED.getDisplayValue());
                break;
            case COMPLETED:
            case CANCELLED:
                car.setStatus(CarStatus.AVAILABLE.getDisplayValue());
                break;
            case PENDING:
                car.setStatus(CarStatus.RESERVED.getDisplayValue());
                break;
            case EARLY_RETURN:
                // Giữ nguyên trạng thái xe là "Rented" khi yêu cầu trả sớm
                car.setStatus(CarStatus.RENTED.getDisplayValue());
                break;
        }
        
        // Lưu thay đổi
            carService.update(car.getCarID(), car);
        carRentalService.save(rental);
        
        redirectAttributes.addFlashAttribute("success", "Rental status updated successfully to " + rentalStatus);
        return "redirect:/admin/rentals";
    }

    @PostMapping("/request-early-return")
    public String requestEarlyReturn(
            @RequestParam("customerId") Integer customerId,
            @RequestParam("carId") Integer carId,
            @RequestParam("pickupDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam("newReturnDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newReturnDate,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("DEBUG: Early return request started for car ID: " + carId + ", customer ID: " + customerId);
        
        // Kiểm tra xác thực
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            System.out.println("DEBUG: Authentication failed for early return request");
            return "redirect:/login";
        }
        
        // Lấy thông tin tài khoản hiện tại
        Account account = accountService.findByAccountName(authentication.getName());
        Customer currentCustomer = customerService.findByAccount(account);
        
        if (currentCustomer == null) {
            System.out.println("DEBUG: Customer information not found for early return request");
            redirectAttributes.addFlashAttribute("error", "Customer information not found");
            return "redirect:/profile";
        }
        
        // Kiểm tra quyền truy cập (chỉ cho phép yêu cầu trả xe sớm cho chính mình)
        if (!currentCustomer.getCustomerID().equals(customerId)) {
            System.out.println("DEBUG: Permission denied - Customer ID mismatch");
            redirectAttributes.addFlashAttribute("error", "You don't have permission to modify this rental");
            return "redirect:/rental-history";
        }
        
        // Lấy thông tin thuê xe
        CarRentalKey key = new CarRentalKey(customerId, carId, pickupDate);
        Optional<CarRental> rentalOpt = carRentalService.findById(key);
        
        if (rentalOpt.isEmpty()) {
            System.out.println("DEBUG: Rental not found for early return request");
            redirectAttributes.addFlashAttribute("error", "Rental not found");
            return "redirect:/rental-history";
        }
        
        CarRental rental = rentalOpt.get();
        System.out.println("DEBUG: Found rental with current status: " + rental.getStatus());
        
        // Kiểm tra trạng thái thuê xe
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            System.out.println("DEBUG: Invalid status for early return request: " + rental.getStatus() + ", must be ACTIVE");
            redirectAttributes.addFlashAttribute("error", "Only active rentals can be requested for early return");
            return "redirect:/rental-details?customerId=" + customerId + "&carId=" + carId + "&pickupDate=" + pickupDate;
        }
        
        // Kiểm tra ngày trả xe mới
        LocalDate today = LocalDate.now();
        if (newReturnDate.isBefore(today)) {
            System.out.println("DEBUG: Invalid new return date (in the past): " + newReturnDate);
            redirectAttributes.addFlashAttribute("error", "New return date cannot be in the past");
            return "redirect:/rental-details?customerId=" + customerId + "&carId=" + carId + "&pickupDate=" + pickupDate;
        }
        
        if (newReturnDate.isBefore(rental.getId().getPickupDate())) {
            System.out.println("DEBUG: Invalid new return date (before pickup): " + newReturnDate);
            redirectAttributes.addFlashAttribute("error", "New return date cannot be before pickup date");
            return "redirect:/rental-details?customerId=" + customerId + "&carId=" + carId + "&pickupDate=" + pickupDate;
        }
        
        if (newReturnDate.isAfter(rental.getReturnDate()) || newReturnDate.isEqual(rental.getReturnDate())) {
            System.out.println("DEBUG: Invalid new return date (not earlier than original): " + newReturnDate);
            redirectAttributes.addFlashAttribute("error", "New return date must be earlier than the current return date");
            return "redirect:/rental-details?customerId=" + customerId + "&carId=" + carId + "&pickupDate=" + pickupDate;
        }
        
        // Set the newReturnDate field directly (it will be persisted)
        rental.setNewReturnDate(newReturnDate);
        
        // Cập nhật trạng thái thuê xe
        RentalStatus oldStatus = rental.getStatus();
        rental.setStatus(RentalStatus.EARLY_RETURN);
        
        // Lưu thông tin thuê xe
        System.out.println("DEBUG: Changing status from " + oldStatus + " to " + RentalStatus.EARLY_RETURN);
        System.out.println("DEBUG: New return date requested: " + newReturnDate);
                           
        try {
            CarRental savedRental = carRentalService.save(rental);
            System.out.println("DEBUG: Successfully saved rental with new status: " + savedRental.getStatus());
            System.out.println("DEBUG: Saved new return date: " + savedRental.getNewReturnDate());
            
            redirectAttributes.addFlashAttribute("success", "Early return request submitted successfully. Please wait for admin approval.");
        } catch (Exception e) {
            System.out.println("DEBUG: Error saving rental with early return request: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to submit early return request. Please try again later.");
        }
        
        return "redirect:/rental-details?customerId=" + customerId + "&carId=" + carId + "&pickupDate=" + pickupDate;
    }
}