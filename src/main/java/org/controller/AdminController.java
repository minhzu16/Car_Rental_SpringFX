package org.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.entity.Car;
import org.entity.CarProducer;
import org.entity.CarRental;
import org.entity.CarRentalKey;
import org.entity.Customer;
import org.enums.CarStatus;
import org.enums.RentalStatus;
import org.service.CarProducerService;
import org.service.CarRentalService;
import org.service.CarService;
import org.service.CustomerService;
import org.service.AccountService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.entity.Account;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final CarService carService;
    private final CarProducerService carProducerService;
    private final CarRentalService carRentalService;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/admin")
    public String admin(Model model, 
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String query) {
        // Lấy danh sách xe
        Pageable pageable = PageRequest.of(page, size);
        Page<Car> carsPage;
        
        if (query != null && !query.isEmpty()) {
            // Tìm kiếm xe theo tên hoặc mô tả
            carsPage = carService.findByNameOrDescription(query, pageable);
        } else {
            carsPage = carService.findAll(pageable);
        }
        
        // Lấy danh sách thuê xe
        List<CarRental> activeRentals = carRentalService.findAll().stream()
                .filter(rental -> rental.getStatus() == RentalStatus.ACTIVE)
                .collect(Collectors.toList());
        
        // Lấy số lượng yêu cầu đang chờ duyệt và yêu cầu trả sớm
        long pendingRequests = carRentalService.findAll().stream()
                .filter(rental -> rental.getStatus() == RentalStatus.PENDING)
                .count();
                
        long earlyReturnRequests = carRentalService.findAll().stream()
                .filter(rental -> rental.getStatus() == RentalStatus.EARLY_RETURN)
                .count();
        
        // Cập nhật trạng thái xe dựa trên thuê xe hiện tại
        List<Car> allCars = carService.findAll();
        for (Car car : allCars) {
            boolean isRented = activeRentals.stream()
                    .anyMatch(rental -> rental.getCar().getCarID().equals(car.getCarID()));
            if (isRented && !"Rented".equals(car.getStatus())) {
                car.setStatus("Rented");
                carService.update(car.getCarID(), car);
            } else if (!isRented && "Rented".equals(car.getStatus())) {
                car.setStatus("Available");
                carService.update(car.getCarID(), car);
            }
        }
        
        // Lấy lại danh sách xe sau khi đã cập nhật
        allCars = carService.findAll();
        
        // Thống kê trạng thái xe
        Map<String, Long> carStatusCounts = allCars.stream()
                .collect(Collectors.groupingBy(Car::getStatus, Collectors.counting()));
        
        long availableCars = carStatusCounts.getOrDefault("Available", 0L);
        long rentedCars = carStatusCounts.getOrDefault("Rented", 0L);
        long reservedCars = carStatusCounts.getOrDefault("Reserved", 0L);
        long maintenanceCars = carStatusCounts.getOrDefault("Maintenance", 0L);
        long totalCars = allCars.size();
        
        // Thêm dữ liệu vào model
        model.addAttribute("cars", carsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", carsPage.getTotalPages());
        model.addAttribute("totalItems", carsPage.getTotalElements());
        model.addAttribute("query", query);
        
        // Thêm thống kê
        model.addAttribute("totalCars", totalCars);
        model.addAttribute("availableCars", availableCars);
        model.addAttribute("rentedCars", rentedCars);
        model.addAttribute("reservedCars", reservedCars);
        model.addAttribute("maintenanceCars", maintenanceCars);
        
        // Thêm thông tin yêu cầu chờ xử lý
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("earlyReturnRequests", earlyReturnRequests);
        model.addAttribute("totalRequests", pendingRequests + earlyReturnRequests);
        
        // Thêm flag để phân biệt trang dashboard và trang cars
        model.addAttribute("isDashboard", true);
        
        return "html/admin";
    }

    @GetMapping("/admin/cars")
    public String listCars(Model model, 
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String status) {
        
        // Lấy danh sách thuê xe
        List<CarRental> activeRentals = carRentalService.findAll().stream()
                .filter(rental -> rental.getStatus() == RentalStatus.ACTIVE)
                .collect(Collectors.toList());
        
        // Lấy số lượng yêu cầu đang chờ duyệt và yêu cầu trả sớm
        long pendingRequests = carRentalService.findAll().stream()
                .filter(rental -> rental.getStatus() == RentalStatus.PENDING)
                .count();
                
        long earlyReturnRequests = carRentalService.findAll().stream()
                .filter(rental -> rental.getStatus() == RentalStatus.EARLY_RETURN)
                .count();
        
        // Cập nhật trạng thái xe dựa trên thuê xe hiện tại
        List<Car> allCars = carService.findAll();
        for (Car car : allCars) {
            boolean isRented = activeRentals.stream()
                    .anyMatch(rental -> rental.getCar().getCarID().equals(car.getCarID()));
            if (isRented && !"Rented".equals(car.getStatus())) {
                car.setStatus("Rented");
                carService.update(car.getCarID(), car);
            } else if (!isRented && "Rented".equals(car.getStatus())) {
                car.setStatus("Available");
                carService.update(car.getCarID(), car);
            }
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Car> carsPage;
        
        if (status != null && !status.isEmpty()) {
            // Lọc xe theo trạng thái
            carsPage = carService.findByStatus(status, pageable);
        } else {
            carsPage = carService.findAll(pageable);
        }
        
        // Lấy lại danh sách xe sau khi đã cập nhật
        allCars = carService.findAll();
        
        // Thống kê trạng thái xe
        Map<String, Long> carStatusCounts = allCars.stream()
                .collect(Collectors.groupingBy(Car::getStatus, Collectors.counting()));
        
        long availableCars = carStatusCounts.getOrDefault("Available", 0L);
        long rentedCars = carStatusCounts.getOrDefault("Rented", 0L);
        long reservedCars = carStatusCounts.getOrDefault("Reserved", 0L);
        long maintenanceCars = carStatusCounts.getOrDefault("Maintenance", 0L);
        long totalCars = allCars.size();
        
        // Thêm dữ liệu vào model
        model.addAttribute("cars", carsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", carsPage.getTotalPages());
        model.addAttribute("totalItems", carsPage.getTotalElements());
        model.addAttribute("status", status);
        
        // Thêm thống kê
        model.addAttribute("totalCars", totalCars);
        model.addAttribute("availableCars", availableCars);
        model.addAttribute("rentedCars", rentedCars);
        model.addAttribute("reservedCars", reservedCars);
        model.addAttribute("maintenanceCars", maintenanceCars);
        
        // Thêm thông tin yêu cầu chờ xử lý
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("earlyReturnRequests", earlyReturnRequests);
        model.addAttribute("totalRequests", pendingRequests + earlyReturnRequests);
        
        // Thêm flag để phân biệt trang dashboard và trang cars
        model.addAttribute("isDashboard", false);
        model.addAttribute("isCarsPage", true);
        
        return "html/admin";
    }
    
    // Các phương thức liên quan đến quản lý xe đã được chuyển sang CarController
    // Không thêm phương thức xử lý xe ở đây để tránh xung đột với CarController
    
    @GetMapping("/admin/rentals")
    public String listRentals(Model model, 
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String filter) {
        
        // Count rentals by status
        List<CarRental> allRentals = carRentalService.findAll();
        
        // Debug info for early return requests
        System.out.println("DEBUG: Total rentals: " + allRentals.size());
        for (CarRental rental : allRentals) {
            if (rental.getStatus() == RentalStatus.EARLY_RETURN) {
                System.out.println("DEBUG: Found early return request - " +
                                  "Customer ID: " + rental.getCustomer().getCustomerID() + ", " +
                                  "Car ID: " + rental.getCar().getCarID() + ", " +
                                  "Pickup Date: " + rental.getId().getPickupDate() + ", " +
                                  "Status: " + rental.getStatus());
            }
        }
        
        long activeRentals = allRentals.stream()
                .filter(rental -> rental.getStatus() == RentalStatus.ACTIVE)
                .count();
        long completedRentals = allRentals.stream()
                .filter(rental -> rental.getStatus() == RentalStatus.COMPLETED)
                .count();
        long cancelledRentals = allRentals.stream()
                .filter(rental -> rental.getStatus() == RentalStatus.CANCELLED)
                .count();
        long pendingRequests = allRentals.stream()
                .filter(rental -> rental.getStatus() == RentalStatus.PENDING)
                .count();
        long earlyReturnRequests = allRentals.stream()
                .filter(rental -> rental.getStatus() == RentalStatus.EARLY_RETURN)
                .count();
        
        // Debug for early return requests count
        System.out.println("DEBUG: Early return requests count: " + earlyReturnRequests);
        
        // Filter rentals if requested
        List<CarRental> filteredRentals = allRentals;
        if (filter != null && !filter.isEmpty()) {
            switch (filter) {
                case "active":
                    filteredRentals = allRentals.stream()
                            .filter(rental -> rental.getStatus() == RentalStatus.ACTIVE)
                            .collect(Collectors.toList());
                    break;
                case "completed":
                    filteredRentals = allRentals.stream()
                            .filter(rental -> rental.getStatus() == RentalStatus.COMPLETED)
                            .collect(Collectors.toList());
                    break;
                case "cancelled":
                    filteredRentals = allRentals.stream()
                            .filter(rental -> rental.getStatus() == RentalStatus.CANCELLED)
                            .collect(Collectors.toList());
                    break;
                case "pending":
                    filteredRentals = allRentals.stream()
                            .filter(rental -> rental.getStatus() == RentalStatus.PENDING)
                            .collect(Collectors.toList());
                    break;
                case "early-return":
                    filteredRentals = allRentals.stream()
                            .filter(rental -> rental.getStatus() == RentalStatus.EARLY_RETURN)
                            .collect(Collectors.toList());
                    break;
                default:
                    // No filter or unknown filter, use all rentals
                    break;
            }
        }
        
        // Paginate the filtered list manually since we're not using repository pagination
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, filteredRentals.size());
        
        List<CarRental> pagedRentals;
        if (fromIndex < filteredRentals.size()) {
            pagedRentals = filteredRentals.subList(fromIndex, toIndex);
        } else {
            pagedRentals = new ArrayList<>();
        }
        
        int totalPages = (int) Math.ceil((double) filteredRentals.size() / size);
        
        // Add data to model
        model.addAttribute("rentals", pagedRentals);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", filteredRentals.size());
        model.addAttribute("filter", filter);
        
        // Add rental statistics
        model.addAttribute("totalRentals", allRentals.size());
        model.addAttribute("activeRentals", activeRentals);
        model.addAttribute("completedRentals", completedRentals);
        model.addAttribute("cancelledRentals", cancelledRentals);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("earlyReturnRequests", earlyReturnRequests);
        
        // Thêm flag để phân biệt trang
        model.addAttribute("isRentalsPage", true);
        
        return "html/rentals";
    }

    @PostMapping("/admin/approve-rental")
    public String approveRental(
            @RequestParam("customerId") Integer customerId,
            @RequestParam("carId") Integer carId,
            @RequestParam("pickupDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam("approved") boolean approved,
            RedirectAttributes redirectAttributes) {
        
        // Lấy thông tin thuê xe
        CarRentalKey key = new CarRentalKey(customerId, carId, pickupDate);
        Optional<CarRental> rentalOpt = carRentalService.findById(key);
        
        if (rentalOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Rental not found");
            return "redirect:/admin/rentals";
        }
        
        CarRental rental = rentalOpt.get();
        Car car = rental.getCar();
        
        // Kiểm tra trạng thái thuê xe
        if (rental.getStatus() != RentalStatus.PENDING) {
            redirectAttributes.addFlashAttribute("error", "Only pending rentals can be approved");
            return "redirect:/admin/rentals";
        }
        
        // Kiểm tra thời gian HOLD (không quá 1 ngày)
        LocalDate today = LocalDate.now();
        LocalDate creationDate = rental.getId().getPickupDate().minusDays(1); // Giả định ngày tạo
        if (ChronoUnit.DAYS.between(creationDate, today) > 1) {
            // Tự động hủy nếu quá 1 ngày
            rental.setStatus(RentalStatus.CANCELLED);
            car.setStatus(CarStatus.AVAILABLE.getDisplayValue());
            carService.update(car.getCarID(), car);
            carRentalService.save(rental);
            redirectAttributes.addFlashAttribute("warning", "Rental was automatically cancelled due to approval timeout");
            return "redirect:/admin/rentals";
        }
        
        if (approved) {
            // Phê duyệt thuê xe
            rental.setStatus(RentalStatus.ACTIVE);
            car.setStatus(CarStatus.RENTED.getDisplayValue());
            redirectAttributes.addFlashAttribute("success", "Rental approved successfully");
        } else {
            // Từ chối thuê xe
            rental.setStatus(RentalStatus.CANCELLED);
            car.setStatus(CarStatus.AVAILABLE.getDisplayValue());
            redirectAttributes.addFlashAttribute("success", "Rental rejected successfully");
        }
        
        // Lưu thay đổi
        carService.update(car.getCarID(), car);
        carRentalService.save(rental);
        
        return "redirect:/admin/rentals";
    }
    
    @PostMapping("/admin/handle-early-return")
    public String handleEarlyReturn(
            @RequestParam("customerId") Integer customerId,
            @RequestParam("carId") Integer carId,
            @RequestParam("pickupDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam("approved") boolean approved,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("DEBUG: Admin handling early return request for car ID: " + carId + ", customer ID: " + customerId);
        
        // Lấy thông tin thuê xe
        CarRentalKey key = new CarRentalKey(customerId, carId, pickupDate);
        Optional<CarRental> rentalOpt = carRentalService.findById(key);
        
        if (rentalOpt.isEmpty()) {
            System.out.println("DEBUG: Rental not found for early return handling");
            redirectAttributes.addFlashAttribute("error", "Rental not found");
            return "redirect:/admin/rentals";
        }
        
        CarRental rental = rentalOpt.get();
        Car car = rental.getCar();
        
        System.out.println("DEBUG: Found rental with status: " + rental.getStatus() + ", newReturnDate: " + rental.getNewReturnDate());
        
        // Kiểm tra trạng thái thuê xe
        if (rental.getStatus() != RentalStatus.EARLY_RETURN) {
            System.out.println("DEBUG: Invalid status for early return handling: " + rental.getStatus());
            redirectAttributes.addFlashAttribute("error", "Only rentals with early return requests can be processed");
            return "redirect:/admin/rentals";
        }
        
        if (rental.getNewReturnDate() == null) {
            System.out.println("DEBUG: Missing new return date for early return request");
            redirectAttributes.addFlashAttribute("error", "Missing new return date for early return request");
            return "redirect:/admin/rentals";
        }
        
        if (approved) {
            // Tính lại giá thuê xe
            long days = java.time.temporal.ChronoUnit.DAYS.between(rental.getId().getPickupDate(), rental.getNewReturnDate());
            double rentPrice = car.getRentPrice() * days;
            
            System.out.println("DEBUG: Approving early return - original return date: " + rental.getReturnDate() + 
                               ", new return date: " + rental.getNewReturnDate() + 
                               ", original price: " + rental.getRentPrice() + 
                               ", new price: " + rentPrice);
            
            // Cập nhật thuê xe
            rental.setReturnDate(rental.getNewReturnDate());
            rental.setRentPrice(rentPrice);
            rental.setStatus(RentalStatus.COMPLETED);
            
            // Cập nhật trạng thái xe
            car.setStatus(CarStatus.AVAILABLE.getDisplayValue());
            
            redirectAttributes.addFlashAttribute("success", "Early return approved successfully");
        } else {
            // Từ chối yêu cầu trả xe sớm
            System.out.println("DEBUG: Rejecting early return request");
            rental.setStatus(RentalStatus.ACTIVE);
            
            // Ensure car status remains "Rented" when rejecting early return
            car.setStatus(CarStatus.RENTED.getDisplayValue());
            
            redirectAttributes.addFlashAttribute("success", "Early return rejected, rental remains active");
        }
        
        // Xóa giá trị newReturnDate khi đã xử lý xong
        rental.setNewReturnDate(null);
        
        // Lưu thay đổi
        carService.update(car.getCarID(), car);
        CarRental savedRental = carRentalService.save(rental);
        System.out.println("DEBUG: Saved rental with updated status: " + savedRental.getStatus());
        
        return "redirect:/admin/rentals";
    }

    /**
     * Endpoint để tạo tài khoản admin (chỉ sử dụng trong môi trường phát triển)
     * Đường dẫn: /admin/create-admin?username=adminuser&password=adminpassword&secretKey=your_secret_key
     */
    @GetMapping("/admin/create-admin")
    public String createAdminAccount(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String secretKey,
            RedirectAttributes redirectAttributes) {
        
        // Kiểm tra secret key để đảm bảo an toàn (thay thế bằng secret key của bạn)
        if (!"your_secret_key".equals(secretKey)) {
            redirectAttributes.addFlashAttribute("error", "Không được phép tạo tài khoản admin");
            return "redirect:/login";
        }
        
        // Kiểm tra xem tài khoản đã tồn tại chưa
        Account existingAccount = accountService.findByAccountName(username);
        if (existingAccount != null) {
            redirectAttributes.addFlashAttribute("error", "Tài khoản đã tồn tại");
            return "redirect:/login";
        }
        
        // Tạo tài khoản admin mới
        Account adminAccount = new Account();
        adminAccount.setAccountName(username);
        adminAccount.setPassword(passwordEncoder.encode(password));
        adminAccount.setRole("ROLE_ADMIN");
        
        accountService.save(adminAccount);
        
        redirectAttributes.addFlashAttribute("success", "Tài khoản admin đã được tạo thành công");
        return "redirect:/login";
    }
}
