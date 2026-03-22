package org.service;

import lombok.RequiredArgsConstructor;
import org.entity.CarRental;
import org.entity.CarRentalKey;
import org.entity.Customer;
import org.enums.RentalStatus;
import org.repository.CarRentalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarRentalService {
    private final CarRentalRepository repository;

    public CarRental save(CarRental r) {
        return repository.save(r);
    }

    public List<CarRental> findAll() {
        return repository.findAll();
    }

    public Page<CarRental> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<CarRental> findByDateRange(LocalDate from, LocalDate to) {
        return repository.findByIdPickupDateBetweenOrderByIdPickupDateDesc(from, to);
    }

    public List<CarRental> findByCustomer(Customer customer) {
        return repository.findByCustomerCustomerID(customer.getCustomerID());
    }

    public List<CarRental> findByCustomerId(Integer customerId) {
        return repository.findByCustomerCustomerID(customerId);
    }

    public Optional<CarRental> findById(CarRentalKey id) {
        return repository.findById(id);
    }

    public void delete(CarRentalKey id) {
        repository.deleteById(id);
    }

    public boolean isCarRented(Integer carId, LocalDate pickupDate, LocalDate returnDate) {
        List<CarRental> overlappingRentals = repository.findByCarCarIDAndStatusNot(carId, RentalStatus.CANCELLED);
        for (CarRental rental : overlappingRentals) {
            if (!(returnDate.isBefore(rental.getId().getPickupDate()) || 
                  pickupDate.isAfter(rental.getReturnDate()))) {
                return true;
            }
        }
        return false;
    }
}
