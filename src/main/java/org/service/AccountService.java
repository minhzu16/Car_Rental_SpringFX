package org.service;

import org.entity.Account;
import org.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@Service
public class AccountService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(AccountRepository repository, PasswordEncoder passwordEncoder) {
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
        if(acc==null) throw new UsernameNotFoundException("User not found");
        acc.setPassword(passwordEncoder.encode(rawPassword));
        logger.info("Thay đổi mật khẩu cho tài khoản: {}", acc.getAccountName());
        return repository.save(acc);
    }

    public Account updateRole(String accountName, String role){
        Account acc = repository.findByAccountName(accountName);
        if(acc==null) throw new UsernameNotFoundException("User not found");
        acc.setRole(role);
        return repository.save(acc);
    }

    public void deleteById(Integer id){
        repository.deleteById(id);
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Đang tìm người dùng theo tên: {}", username);
        Account acc = repository.findByAccountName(username);
        if(acc == null) {
            logger.error("Không tìm thấy người dùng với tên: {}", username);
            throw new UsernameNotFoundException("Không tìm thấy người dùng với tên: " + username);
        }
        
        logger.info("Đã tìm thấy người dùng: {}, Vai trò: {}", acc.getAccountName(), acc.getRole());
        
        // Đơn giản hóa xử lý role
        String roleWithPrefix = "ROLE_" + acc.getRole();
        
        logger.info("Sử dụng vai trò cho quyền hạn: {}", roleWithPrefix);
        
        return User.builder()
                .username(acc.getAccountName())
                .password(acc.getPassword())
                .authorities(new SimpleGrantedAuthority(roleWithPrefix))
                .build();
    }

    public Account findById(Integer id) {
        return repository.findById(id).orElse(null);
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
