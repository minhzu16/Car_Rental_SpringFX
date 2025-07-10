package org.repository;

import org.entity.Account;
import org.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByEmail(String email);
    Customer findByAccount(Account account);
    Page<Customer> findByCustomerNameContainingOrEmailContaining(String name, String email, Pageable pageable);
}
