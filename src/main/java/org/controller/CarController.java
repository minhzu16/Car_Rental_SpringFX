package org.controller;

import lombok.RequiredArgsConstructor;
import org.entity.Car;
import org.entity.CarProducer;
import org.service.CarProducerService;
import org.service.CarService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.utils.AddCarValidator;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;
    private final CarProducerService carProducerService;

    @GetMapping("/cars")
    public String listCars(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Car> carPage = carService.findAll(pageable);
        model.addAttribute("cars", carPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", carPage.getTotalPages());
        model.addAttribute("totalItems", carPage.getTotalElements());
        return "html/cars";
    }

    @GetMapping("/cars/{id}")
    public String carDetails(@PathVariable Integer id, Model model) {
        Object car = carService.findById(id).orElseThrow();
        model.addAttribute("car", car);
        return "html/car-details";
    }

    @GetMapping("/admin/cars/add")
    public String newCarForm(Model model) {
        Car car = new Car();
        // Thiết lập giá trị mặc định cho ngày nhập
        car.setImportDate(LocalDate.now());
        
        model.addAttribute("car", car);
        List<CarProducer> producers = carProducerService.findAll();
        model.addAttribute("producers", producers);
        return "html/car-form";
    }

    @PostMapping("/admin/cars/add")
    public String saveCar(@ModelAttribute Car car, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        // Validate car data
        List<String> errors = AddCarValidator.validateCar(car);
        
        if (!errors.isEmpty()) {
            // Add errors to model
            model.addAttribute("errors", errors);
            
            // Add producers for dropdown
            List<CarProducer> producers = carProducerService.findAll();
            model.addAttribute("producers", producers);
            
            // Return to form with errors
            return "html/car-form";
        }
        
        // Luôn cập nhật importDate là thời gian hiện tại khi thêm xe mới
        car.setImportDate(LocalDate.now());
        
        // Save car if validation passes
        carService.save(car);
        redirectAttributes.addFlashAttribute("successMessage", "Car added successfully");
        return "redirect:/admin/cars";
    }

    @GetMapping("/admin/cars/edit/{id}")
    public String editCarForm(@PathVariable Integer id, Model model) {
        Car car = carService.findById(id).orElseThrow();
        model.addAttribute("car", car);
        
        // Add producers for dropdown
        List<CarProducer> producers = carProducerService.findAll();
        model.addAttribute("producers", producers);
        
        return "html/car-form";
    }
    
    @PostMapping("/admin/cars/edit/{id}")
    public String updateCar(@PathVariable Integer id, @ModelAttribute Car car, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        // Validate car data
        List<String> errors = AddCarValidator.validateCar(car);
        
        if (!errors.isEmpty()) {
            // Add errors to model
            model.addAttribute("errors", errors);
            
            // Add producers for dropdown
            List<CarProducer> producers = carProducerService.findAll();
            model.addAttribute("producers", producers);
            
            // Return to form with errors
            return "html/car-form";
        }
        
        // Đảm bảo importDate được thiết lập nếu chưa có
        if (car.getImportDate() == null) {
            car.setImportDate(LocalDate.now());
        }
        
        // Update car if validation passes
        car.setCarID(id);
        carService.save(car);
        redirectAttributes.addFlashAttribute("successMessage", "Car updated successfully");
        return "redirect:/admin/cars";
    }

    @PostMapping("/admin/cars/delete/{id}")
    public String deleteCar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        carService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Car deleted successfully");
        return "redirect:/admin/cars";
    }
}