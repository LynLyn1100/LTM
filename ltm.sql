/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author: nddmanh
 * Created: Oct 18, 2021
 */
<<<<<<< HEAD
create database btlltm
USE btlltm;
=======
create database bttlltm
USE bttlltm;
>>>>>>> af6d489e7cd5c354fef3c4d6519b224552051b49

drop table users;

-- CREATE TABLE users (
--   `userId` INT NOT NULL AUTO_INCREMENT,
--   `username` VARCHAR(45) NOT NULL,
--   `password` VARCHAR(45) NOT NULL,
--   `score` FLOAT NOT NULL,
--   `win` INT NOT NULL,
--   `draw` INT NOT NULL,
--   `lose` INT NOT NULL,
--   `avgCompetitor` FLOAT NOT NULL,
--   `avgTime` FLOAT NOT NULL,
--   PRIMARY KEY (`userId`));
--   
 
-- INSERT INTO users (username, password, score, win, draw, lose, avgCompetitor, avgTime)
-- VALUES
-- ('user1', 'password1', 100.0, 10, 5, 3, 80.0, 25.0),
-- ('user2', 'password2', 90.5, 8, 7, 5, 75.5, 30.0),
-- ('user3', 'password3', 85.0, 9, 4, 6, 70.0, 28.5),
-- ('user4', 'password4', 92.3, 11, 3, 4, 78.2, 27.0);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    userName VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(50),
    score FLOAT,
    win INT DEFAULT 0,
    draw INT DEFAULT 0,
<<<<<<< HEAD
    lose INT DEFAULT 0,
=======
    loss INT DEFAULT 0,
>>>>>>> af6d489e7cd5c354fef3c4d6519b224552051b49
    avgCompetitor FLOAT,
    avgTime FLOAT,
    totalMatches INT DEFAULT 0
);

<<<<<<< HEAD
INSERT INTO users (userName, password, score, win, draw, lose, avgCompetitor, avgTime, totalMatches) 
VALUES 
('ly', '12345', 0, 20, 8, 5, 160.0, 3.0, 33),
('lyn', '12345', 0, 15, 10, 5, 140.0, 2.8, 30);

INSERT INTO users (userName, password, score, win, draw, lose, avgCompetitor, avgTime, totalMatches) 
VALUES 
('a', '1', 0, 0, 10, 0, 160.0, 3.0, 33),
('b', '1', 0, 0, 20, 0, 140.0, 2.8, 30);


drop table products;
=======
INSERT INTO users (userName, password, score, win, draw, loss, avgCompetitor, avgTime, totalMatches) 
VALUES 
('ly', '12345', 200, 20, 8, 5, 160.0, 3.0, 33),
('lyn', '12345', 100, 15, 10, 5, 140.0, 2.8, 30);




drop table Product;
>>>>>>> af6d489e7cd5c354fef3c4d6519b224552051b49

-- CREATE TABLE Product (
--     id VARCHAR(255) PRIMARY KEY,
--     name VARCHAR(255),
--     price DECIMAL(10, 2),
--     image_url VARCHAR(255)
-- );

-- INSERT INTO Product (id, name, price, image_url) VALUES ('SP001', 'iPhone 12', 799.99, 'https://tse3.mm.bing.net/th?id=OIP.Z1tBmQaZAaB8f-Gdihlc8AHaGk&pid=Api&P=0&h=180');
-- INSERT INTO Product (id, name, price, image_url) VALUES ('SP002', 'Samsung Galaxy S21', 999.99, 'https://tse3.mm.bing.net/th?id=OIP.JkeMdYKXrDEoAMBC4UhgKQHaE8&pid=Api&P=0&h=180');
-- INSERT INTO Product (id, name, price, image_url) VALUES ('SP003', 'Dell XPS 13', 1200.50, 'https://tse4.mm.bing.net/th?id=OIP.QGj8RmeUhn6Mg22XPFNwwwHaHD&pid=Api&P=0&h=180');
-- INSERT INTO Product (id, name, price, image_url) VALUES ('SP004', 'Nike Air Max', 199.99, 'https://tse1.mm.bing.net/th?id=OIP.CB7MhAMNOPbtdZb7HPupIAHaFM&pid=Api&P=0&h=180');
-- INSERT INTO Product (id, name, price, image_url) VALUES ('SP005', 'Adidas Ultra Boost', 179.99, 'https://tse1.mm.bing.net/th?id=OIP.3ZDijY9q1sVdK1FgX0pZiwHaFM&pid=Api&P=0&h=180');

<<<<<<< HEAD
CREATE TABLE product (
=======
CREATE TABLE products (
>>>>>>> af6d489e7cd5c354fef3c4d6519b224552051b49
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL,
    image_path VARCHAR(255)
);

<<<<<<< HEAD
INSERT INTO product (name, description, price, image_path) VALUES
=======
INSERT INTO products (name, description, price, image_path) VALUES
>>>>>>> af6d489e7cd5c354fef3c4d6519b224552051b49
('Laptop', 'Powerful laptop for work and gaming', 15000.00, 'laptop.jpg'),
('Smartphone', 'Latest model with advanced features', 7000.00, 'smartphone.jpg'),
('Headphones', 'Noise-cancelling wireless headphones', 3500.00, 'headphones.jpg'),
('Smartwatch', 'Fitness tracker and smartwatch', 2000.00, 'smartwatch.jpg'),
('Tablet', 'Lightweight tablet for productivity', 4500.00, 'tablet.jpg');