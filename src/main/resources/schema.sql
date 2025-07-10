-- Xóa cơ sở dữ liệu nếu tồn tại
DROP DATABASE IF EXISTS `FuCarRentalSystem`;

-- Tạo cơ sở dữ liệu và sử dụng
CREATE DATABASE IF NOT EXISTS FuCarRentalSystem;
USE FuCarRentalSystem;

-- Tạo bảng CarProducer
CREATE TABLE IF NOT EXISTS CarProducer (
    ProducerID INT PRIMARY KEY,
    ProducerName VARCHAR(100) NOT NULL,
    Address VARCHAR(200) NOT NULL,
    Country VARCHAR(50) NOT NULL
);

-- Tạo bảng Car với ImageUrl
CREATE TABLE IF NOT EXISTS Car (
    CarID INT AUTO_INCREMENT PRIMARY KEY,
    CarName VARCHAR(100) NOT NULL,
    CarModelYear INT NOT NULL,
    Color VARCHAR(50) NOT NULL,
    Capacity INT NOT NULL,
    Description VARCHAR(500) NOT NULL,
    ImportDate DATE NOT NULL,
    ProducerID INT NOT NULL,
    RentPrice DECIMAL(10,2) NOT NULL,
    Status VARCHAR(20) NOT NULL,
    ImageUrl VARCHAR(200) NOT NULL,
    FOREIGN KEY (ProducerID) REFERENCES CarProducer(ProducerID)
);

-- Tạo bảng Account với AUTO_INCREMENT cho AccountID
CREATE TABLE IF NOT EXISTS Account (
    AccountID INT AUTO_INCREMENT PRIMARY KEY,
    AccountName VARCHAR(100) NOT NULL,
    Password VARCHAR(100) NOT NULL,
    Role VARCHAR(50) NOT NULL
);

-- Tạo bảng Customer với AUTO_INCREMENT cho CustomerID
CREATE TABLE IF NOT EXISTS Customer (
    CustomerID INT AUTO_INCREMENT PRIMARY KEY,
    CustomerName VARCHAR(100) NOT NULL,
    Mobile VARCHAR(20) NOT NULL,
    Birthday DATE NOT NULL,
    IdentityCard VARCHAR(20) NOT NULL,
    LicenceNumber VARCHAR(20) NOT NULL,
    LicenceDate DATE NOT NULL,
    Email VARCHAR(100) NOT NULL,
    Password VARCHAR(100) NOT NULL,
    AccountID INT NOT NULL,
    Address VARCHAR(200) NOT NULL,
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID)
);

-- Tạo bảng CarRental
CREATE TABLE IF NOT EXISTS CarRental (
    CustomerID INT NOT NULL,
    CarID INT NOT NULL,
    PickupDate DATE NOT NULL,
    ReturnDate DATE NOT NULL,
    RentPrice DECIMAL(10,2) NOT NULL,
    Status VARCHAR(30) NOT NULL,
    PRIMARY KEY (CustomerID, CarID, PickupDate),
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID),
    FOREIGN KEY (CarID) REFERENCES Car(CarID),
    CONSTRAINT check_dates CHECK (PickupDate < ReturnDate)
);

-- Tạo bảng Review
CREATE TABLE IF NOT EXISTS Review (
    CustomerID INT NOT NULL,
    CarID INT NOT NULL,
    ReviewStar INT NOT NULL,
    Comment VARCHAR(500) NOT NULL,
    PRIMARY KEY (CustomerID, CarID),
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID),
    FOREIGN KEY (CarID) REFERENCES Car(CarID)
); 