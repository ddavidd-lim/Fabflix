CREATE DATABASE IF NOT EXISTS moviedb;
DROP TABLE IF EXISTS moviedb.sales;
DROP TABLE IF EXISTS moviedb.ratings;
DROP TABLE IF EXISTS moviedb.stars_in_movies;
DROP TABLE IF EXISTS moviedb.genres_in_movies;
DROP TABLE IF EXISTS moviedb.customers;
DROP TABLE IF EXISTS moviedb.genres;
DROP TABLE IF EXISTS moviedb.creditcards;
DROP TABLE IF EXISTS moviedb.movies;
DROP TABLE IF EXISTS moviedb.stars;
DROP TABLE IF EXISTS moviedb.employees;


CREATE TABLE moviedb.movies (
id varchar(100) NOT NULL,
title varchar(100) NOT NULL,
year int NOT NULL,
director varchar(100) NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE moviedb.stars (
 id varchar(10) NOT NULL,
 name varchar(100) NOT NULL,
 birthYear int,
 PRIMARY KEY (id)
);

CREATE TABLE moviedb.stars_in_movies (
	starId varchar(10) NOT NULL,
    movieId varchar(10) NOT NULL,
    FOREIGN KEY (starId) REFERENCES stars(id) ON DELETE CASCADE,
    FOREIGN KEY (movieId) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE moviedb.genres (
	id int NOT NULL AUTO_INCREMENT,
    name varchar(32) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE moviedb.genres_in_movies (
	genreId int NOT NULL,
    movieId varchar(10) NOT NULL,
    FOREIGN KEY (genreId) REFERENCES genres(id) ON DELETE CASCADE,
    FOREIGN KEY (movieId) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE moviedb.creditcards (
	id varchar(20) NOT NULL,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    expiration date NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE moviedb.customers (
	id int NOT NULL AUTO_INCREMENT,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    ccId varchar(20) NOT NULL,
    address varchar(200) NOT NULL,
    email varchar(50) NOT NULL,
    password varchar(20) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (ccId) REFERENCES creditcards(id) ON DELETE CASCADE
);

CREATE TABLE moviedb.sales (
	id int NOT NULL AUTO_INCREMENT,
    customerId int NOT NULL,
    movieId varchar(10) NOT NULL,
    salesDate date NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (customerId) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (movieId) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE moviedb.ratings (
	movieId varchar(10) NOT NULL,
    rating float NOT NULL,
    numVotes int NOT NULL,
    FOREIGN KEY (movieId) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE moviedb.employees (
    email varchar(50),
    password varchar(20) NOT NULL,
    fullname varchar(100),
    PRIMARY KEY (email)
);