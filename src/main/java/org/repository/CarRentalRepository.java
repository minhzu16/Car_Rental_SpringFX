package org.repository;

import org.entity.CarRental;
import org.entity.CarRentalKey;
import org.enums.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CarRentalRepository extends JpaRepository<CarRental, CarRentalKey> {
    // Query by embedded id property `pickupDate`
    List<CarRental> findByIdPickupDateBetweenOrderByIdPickupDateDesc(LocalDate startDate, LocalDate endDate);
    List<CarRental> findByCustomerCustomerID(Integer customerID);
    // Filter by customer and pickup date range
    List<CarRental> findByCustomerCustomerIDAndIdPickupDateBetweenOrderByIdPickupDateDesc(Integer customerID, LocalDate startDate, LocalDate endDate);
    // Find by car ID and status not equal to a specific value (e.g., CANCELLED)
    List<CarRental> findByCarCarIDAndStatusNot(Integer carID, RentalStatus status);
    
    // Check if car belongs to any rental transaction
    boolean existsByCarCarID(Integer carID);
    
    // Find rentals by period ordered by price descending for reports
    List<CarRental> findByIdPickupDateBetweenOrderByRentPriceDesc(LocalDate startDate, LocalDate endDate);
}
