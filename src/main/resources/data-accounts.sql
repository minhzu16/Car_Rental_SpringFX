-- Chèn dữ liệu mẫu vào Account (sử dụng raw passwords, sẽ được mã hóa bởi DataInitializer)
INSERT INTO Account (AccountName, Password, Role) VALUES
('user1', 'password123', 'USER'),
('admin1', 'adminpass', 'ADMIN');

-- Chèn dữ liệu mẫu vào Customer (không cần CustomerID vì có AUTO_INCREMENT)
INSERT INTO Customer (CustomerName, Mobile, Birthday, IdentityCard, LicenceNumber, LicenceDate, Email, Password, AccountID, Address) VALUES
('John Doe', '1234567890', '1990-05-15', 'ID123456', 'DL789012', '2020-01-10', 'john@example.com', 'password123', 1, '123 Main St, Springfield'),
('Jane Admin', '0987654321', '1985-08-20', 'ID654321', 'DL210987', '2019-03-15', 'jane@example.com', 'adminpass', 2, '456 Oak Ave, Metropolis'); 