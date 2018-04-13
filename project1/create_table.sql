CREATE SCHEMA IF NOT EXISTS `moviedb`;
USE `moviedb`;

CREATE TABLE IF NOT EXISTS `movies` (
  `id` VARCHAR(10) NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `year` INT NOT NULL,
  `director` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE IF NOT EXISTS `stars` (
  `id` VARCHAR(10) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `birthYear` INT,
  PRIMARY KEY (`id`));

CREATE TABLE IF NOT EXISTS `stars_in_movies` (
  `starId` VARCHAR(10) NOT NULL,
  `movieId` VARCHAR(10) NOT NULL,
  INDEX `starId_idx` (`starId` ASC),
  INDEX `movieId_idx` (`movieId` ASC),
  CONSTRAINT `starId`
    FOREIGN KEY (`starId`)
    REFERENCES `stars` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `movieId`
    FOREIGN KEY (`movieId`)
    REFERENCES `movies` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS `genres` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(32) NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE IF NOT EXISTS `creditcards` (
  `id` VARCHAR(20) NOT NULL,
  `firstName` VARCHAR(50) NOT NULL,
  `lastName` VARCHAR(50) NOT NULL,
  `expiration` DATE NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE IF NOT EXISTS `genres_in_movies` (
  `genreId` INT NOT NULL,
  `movieId` VARCHAR(10) NOT NULL,
  INDEX `genreId_idx` (`genreId` ASC),
  INDEX `movieId_idx` (`movieId` ASC),
  CONSTRAINT `genreId`
    FOREIGN KEY (`genreId`)
    REFERENCES `genres` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `movieId2`
    FOREIGN KEY (`movieId`)
    REFERENCES `movies` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS `customers` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `firstName` VARCHAR(50) NOT NULL,
  `lastName` VARCHAR(50) NOT NULL,
  `ccId` VARCHAR(20) NOT NULL,
  `address` VARCHAR(200) NOT NULL,
  `email` VARCHAR(50) NOT NULL,
  `password` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `ccId_idx` (`ccId` ASC),
  CONSTRAINT `ccId`
    FOREIGN KEY (`ccId`)
    REFERENCES `creditcards` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS `sales` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `customerId` INT NOT NULL,
  `movieId` VARCHAR(10) NOT NULL,
  `saleDate` DATE NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `movieId_idx` (`movieId` ASC),
  INDEX `customerId_idx` (`customerId` ASC),
  CONSTRAINT `customerId`
    FOREIGN KEY (`customerId`)
    REFERENCES `customers` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `movieId3`
    FOREIGN KEY (`movieId`)
    REFERENCES `movies` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS `ratings` (
  `movieId` VARCHAR(10) NOT NULL,
  `rating` FLOAT NOT NULL,
  `numVotes` INT NOT NULL,
  PRIMARY KEY (`movieId`),
  CONSTRAINT `movieId4`
    FOREIGN KEY (`movieId`)
    REFERENCES `movies` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);
