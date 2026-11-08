-- Contact Management System: database schema (MySQL)

CREATE DATABASE IF NOT EXISTS contact_db;
USE contact_db;

CREATE TABLE IF NOT EXISTS contacts (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(100) NOT NULL,
    phone1        VARCHAR(20)  NOT NULL UNIQUE,
    phone2        VARCHAR(20),
    address       VARCHAR(150),
    date_of_birth DATE,
    email         VARCHAR(100) UNIQUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);