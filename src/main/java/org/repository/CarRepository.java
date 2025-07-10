package org.repository;

import org.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer>, JpaSpecificationExecutor<Car> {
    boolean existsByCarIDAndRentalsIsNotEmpty(Integer carID);

    @Query("SELECT c FROM Car c WHERE LOWER(c.carName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Car> findByNameOrDescriptionContainingIgnoreCase(@Param("query") String query, Pageable pageable);
    
    Page<Car> findByStatus(String status, Pageable pageable);
}
