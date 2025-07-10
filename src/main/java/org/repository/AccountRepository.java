package org.repository;

import org.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findByAccountName(String accountName);
    boolean existsByAccountName(String accountName);
}
