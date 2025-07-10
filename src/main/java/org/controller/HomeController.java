package org.controller;

import lombok.RequiredArgsConstructor;
import org.entity.Car;
import org.entity.CarProducer;
import org.enums.Operation;
import org.service.CarProducerService;
import org.service.CarService;
import org.service.FilterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.utils.filter.SearchCriteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    
    private final CarService carService;
    private final CarProducerService carProducerService;
    private final FilterService<Car> carFilterService;

    @GetMapping({"/", "/home"})
    public String home(Model model,
                      @RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "8") int size,
                      @RequestParam(required = false) String carType,
                      @RequestParam(required = false) String region,
                      @RequestParam(required = false) Integer producerId,
                      @RequestParam(required = false) Integer modelYear,
                      @RequestParam(required = false) String color,
                      @RequestParam(required = false) Double minPrice,
                      @RequestParam(required = false) Double maxPrice,
                      @RequestParam(required = false) Integer capacity) {
        
        logger.info("Đang xử lý yêu cầu trang chủ với các tham số: page={}, size={}", page, size);
        
        try {
        // Xây dựng bộ lọc
        List<SearchCriteria> criteria = new ArrayList<>();
        
        // Áp dụng các bộ lọc nếu có
        if (carType != null && !carType.isEmpty()) {
            criteria.add(new SearchCriteria("description", Operation.LIKE, carType + "%"));
        }
        
        if (region != null && !region.isEmpty()) {
            criteria.add(new SearchCriteria("producer.country", Operation.EQUALS, region));
        }
        
        if (producerId != null) {
            criteria.add(new SearchCriteria("producer.producerID", Operation.EQUALS, producerId));
        }
        
        if (modelYear != null) {
            criteria.add(new SearchCriteria("carModelYear", Operation.EQUALS, modelYear));
        }
        
        if (color != null && !color.isEmpty()) {
            criteria.add(new SearchCriteria("color", Operation.EQUALS, color));
        }
        
        if (minPrice != null && maxPrice != null) {
            criteria.add(new SearchCriteria("rentPrice", Operation.BETWEEN, new Object[] { minPrice, maxPrice }));
        } else if (minPrice != null) {
            criteria.add(new SearchCriteria("rentPrice", Operation.GREATER_THAN_OR_EQUAL_TO, minPrice));
        } else if (maxPrice != null) {
            criteria.add(new SearchCriteria("rentPrice", Operation.LESS_THAN_OR_EQUAL_TO, maxPrice));
        }
        
        if (capacity != null) {
            criteria.add(new SearchCriteria("capacity", Operation.EQUALS, capacity));
        }
            
            logger.info("Đã xây dựng {} tiêu chí lọc", criteria.size());
        
        // Lấy danh sách xe đã lọc
        List<Car> filteredCars;
        Page<Car> carPage;
        
        if (!criteria.isEmpty()) {
                logger.info("Đang áp dụng bộ lọc với {} tiêu chí", criteria.size());
            filteredCars = carFilterService.filter(criteria);
            
            // Phân trang thủ công
            int start = page * size;
            int end = Math.min(start + size, filteredCars.size());
            
            List<Car> paginatedCars = filteredCars.subList(Math.min(start, filteredCars.size()), Math.min(end, filteredCars.size()));
            int totalPages = (int) Math.ceil((double) filteredCars.size() / size);
                
                logger.info("Đã lọc được {} xe, hiển thị từ {} đến {}", filteredCars.size(), start, end);
            
            model.addAttribute("cars", paginatedCars);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", filteredCars.size());
        } else {
            // Nếu không có bộ lọc nào, sử dụng phân trang của Spring
                logger.info("Không có bộ lọc, lấy tất cả xe với phân trang");
            Pageable pageable = PageRequest.of(page, size);
            carPage = carService.findAll(pageable);
                
                logger.info("Đã tìm thấy {} xe, tổng số trang: {}", carPage.getTotalElements(), carPage.getTotalPages());
            
            model.addAttribute("cars", carPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", carPage.getTotalPages());
            model.addAttribute("totalItems", carPage.getTotalElements());
        }
        
        // Lấy danh sách nhà sản xuất
        List<CarProducer> producers = carProducerService.findAll();
            logger.info("Đã tìm thấy {} nhà sản xuất", producers.size());
        
        // Lấy danh sách các loại xe từ dữ liệu
        List<String> carTypes = carService.findAll().stream()
                .map(car -> car.getDescription().split(" ")[0]) // Giả sử loại xe là từ đầu tiên trong mô tả
                .distinct()
                .collect(Collectors.toList());
        
        // Lấy danh sách các quốc gia từ nhà sản xuất
        List<String> countries = producers.stream()
                .map(CarProducer::getCountry)
                .distinct()
                .collect(Collectors.toList());
        
        // Lấy danh sách các năm sản xuất
        List<Integer> years = carService.findAll().stream()
                .map(Car::getCarModelYear)
                .distinct()
                .sorted((a, b) -> b.compareTo(a)) // Sắp xếp giảm dần
                .collect(Collectors.toList());
        
        // Lấy danh sách các màu sắc
        List<String> colors = carService.findAll().stream()
                .map(Car::getColor)
                .distinct()
                .collect(Collectors.toList());
        
        // Thêm dữ liệu vào model
        model.addAttribute("producers", producers);
        model.addAttribute("carTypes", carTypes);
        model.addAttribute("countries", countries);
        model.addAttribute("years", years);
        model.addAttribute("colors", colors);
        
        // Thêm các tham số lọc
        model.addAttribute("carType", carType);
        model.addAttribute("region", region);
        model.addAttribute("producerId", producerId);
        model.addAttribute("modelYear", modelYear);
        model.addAttribute("color", color);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("capacity", capacity);
        model.addAttribute("size", size);
        
            logger.info("Đã hoàn thành xử lý trang chủ, trả về template html/home");
        return "html/home";
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý trang chủ: {}", e.getMessage(), e);
            model.addAttribute("error", "Đã xảy ra lỗi khi tải trang: " + e.getMessage());
            return "html/error";
        }
    }
    
    @GetMapping("/car-details")
    public String carDetails(@RequestParam Integer id, Model model) {
        try {
            logger.info("Đang xử lý yêu cầu xem chi tiết xe với id: {}", id);
        Car car = carService.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));
            logger.info("Đã tìm thấy xe: {}", car.getCarName());
        model.addAttribute("car", car);
        return "html/car-details";
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý chi tiết xe: {}", e.getMessage(), e);
            model.addAttribute("error", "Không tìm thấy xe với ID: " + id);
            return "html/error";
        }
    }
}