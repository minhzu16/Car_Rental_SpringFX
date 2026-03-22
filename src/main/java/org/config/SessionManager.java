package org.config;

import org.entity.Account;
import org.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {
    private Account currentAccount;
    private Customer currentCustomer;

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    public void logout() {
        currentAccount = null;
        currentCustomer = null;
    }
}
