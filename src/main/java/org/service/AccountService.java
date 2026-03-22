package org.service;

import org.entity.Account;
import org.repository.AccountRepository;
import org.config.CustomPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository repository;
    private final CustomPasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(AccountRepository repository, CustomPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account save(Account acc){
        acc.setPassword(passwordEncoder.encode(acc.getPassword()));
        logger.info("Mã hóa mật khẩu và lưu tài khoản: {}", acc.getAccountName());
        return repository.save(acc);
    }
    
    public Account saveWithoutEncoding(Account acc){
        logger.info("Lưu tài khoản mà không mã hóa mật khẩu: {}", acc.getAccountName());
        return repository.save(acc);
    }

    public Account changePassword(String accountName, String rawPassword){
        Account acc = repository.findByAccountName(accountName);
        if(acc==null) throw new RuntimeException("User not found: " + accountName);
        acc.setPassword(passwordEncoder.encode(rawPassword));
        logger.info("Thay đổi mật khẩu cho tài khoản: {}", acc.getAccountName());
        return repository.save(acc);
    }

    public Account updateRole(String accountName, String role){
        Account acc = repository.findByAccountName(accountName);
        if(acc==null) throw new RuntimeException("User not found: " + accountName);
        acc.setRole(role);
        return repository.save(acc);
    }

    public void deleteById(Integer id){
        if (id != null) {
            repository.deleteById(id);
        }
    }

    public Account findByAccountName(String name){
        logger.debug("Tìm tài khoản theo tên: {}", name);
        Account account = repository.findByAccountName(name);
        if (account == null) {
            logger.warn("Không tìm thấy tài khoản với tên: {}", name);
        } else {
            logger.debug("Đã tìm thấy tài khoản với tên: {}", name);
        }
        return account;
    }

    public Account authenticate(String username, String rawPassword) {
        logger.info("Authenticating user: {}", username);
        Account account = repository.findByAccountName(username);
        if (account != null && passwordEncoder.matches(rawPassword, account.getPassword())) {
            return account;
        }
        return null; // Authentication failed
    }

    public Account findById(Integer id) {
        return id != null ? repository.findById(id).orElse(null) : null;
    }

    public Account findByUsername(String username) {
        return repository.findByAccountName(username);
    }

    public boolean existsByUsername(String username) {
        boolean exists = repository.existsByAccountName(username);
        logger.debug("Kiểm tra xem tên người dùng đã tồn tại chưa: {} - Kết quả: {}", username, exists);
        return exists;
    }
}
