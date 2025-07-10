package org.service;

import lombok.RequiredArgsConstructor;

import org.entity.CarRental;
import org.repository.CarRentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.entity.CarRentalKey;
import org.enums.RentalStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarRentalService {
    private final CarRentalRepository repository;

    public CarRental save(CarRental r){return repository.save(r);}    

    // Pagination support
    public Page<CarRental> findAll(Pageable pageable){
        return repository.findAll(pageable);
    }

    public List<CarRental> findRentalsBetween(LocalDate from, LocalDate to){
        return repository.findByIdPickupDateBetweenOrderByIdPickupDateDesc(from,to);
    }
    
    public List<CarRental> findByCustomerId(Integer customerId){
        return repository.findByCustomerCustomerID(customerId);
    }

    // Composite key helpers
    public CarRental findById(Integer customerId, Integer carId, String pickupDate){
        CarRentalKey key = new CarRentalKey(customerId, carId, LocalDate.parse(pickupDate));
        return repository.findById(key).orElseThrow();
    }

    public void deleteById(Integer customerId, Integer carId, String pickupDate){
        CarRentalKey key = new CarRentalKey(customerId, carId, LocalDate.parse(pickupDate));
        repository.deleteById(key);
    }

    public List<CarRental> findAll() {
        return repository.findAll();
    }

    public Optional<CarRental> findById(CarRentalKey id) {
        return repository.findById(id);
    }

    public void delete(CarRentalKey id) {
        repository.deleteById(id);
    }
    
    /**
     * Kiểm tra xem xe đã được thuê trong khoảng thời gian này chưa
     * @param carId ID của xe
     * @param pickupDate ngày bắt đầu thuê
     * @param returnDate ngày trả xe
     * @return true nếu xe đã được thuê hoặc đang chờ duyệt, false nếu chưa
     */
    public boolean isCarRented(Integer carId, LocalDate pickupDate, LocalDate returnDate) {
        // Lấy danh sách thuê xe không bị hủy (ACTIVE, PENDING, COMPLETED, EARLY_RETURN)
        List<CarRental> overlappingRentals = repository.findByCarCarIDAndStatusNot(carId, RentalStatus.CANCELLED);
        
        for (CarRental rental : overlappingRentals) {
            // Kiểm tra xem có sự chồng chéo về thời gian không
            if (!(returnDate.isBefore(rental.getId().getPickupDate()) || 
                  pickupDate.isAfter(rental.getReturnDate()))) {
                return true; // Có sự chồng chéo
            }
        }
        
        return false; // Không có sự chồng chéo
    }
}
