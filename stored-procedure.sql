DROP PROCEDURE IF EXISTS add_movie;
DROP PROCEDURE IF EXISTS add_star;
USE moviedb;

-- Change DELIMITER to $$
DELIMITER $$

-- required fields: movie title, movie year, movie director, star name, genre name
CREATE PROCEDURE add_movie (
    IN movieTitle VARCHAR(100), 
    IN movieYear INTEGER,
    IN movieDirector VARCHAR(100),
    IN moviStar VARCHAR(100),
    IN movieGenre VARCHAR(32))
BEGIN
	-- inserting movie into movie table
	IF (SELECT title FROM movies WHERE title = movieTitle) THEN -- movie exists, don't do anything
		SELECT "Movie already exists"; 
	END IF;
	IF movieTitle = '' THEN -- movie title is empty, don't do anything
		SELECT "Invalid input for movie title";
    END IF;    
    IF movieYear = "" THEN -- movie year is empty, don't do anything
		SELECT "Invalid input for movie year";
	END IF;
    IF movieDirector = "" THEN -- movie director is empty, don't do anything
		SELECT "Invalid input for director";
	END IF;
    SET @movieId = CONCAT("tt", (SELECT MAX(SUBSTRING(id, 3)) FROM movies)) + 1;
    
    -- checking the star
    IF movieStar = "" THEN -- movie star is empty 
		SELECT "Invalid input for movie star";
	END IF;
	IF ((SELECT COUNT(*) FROM stars WHERE name = movieStar) = 0) THEN
		SET @starId = CONCAT("nm", (SELECT MAX(SUBSTRING(id, 3)) FROM stars)) + 1;
        INSERT INTO stars(id, name) VALUES (@starId, movieStar); 
        END IF;
    
    -- checking the genre
    IF movieGenre = "" THEN -- movie genre is empty
		SELECT "Invalid input for genre";
    END IF;
    IF ((SELECT COUNT(*) FROM genres WHERE name = movieGenre) = 0) THEN
        INSERT INTO genres(name) values (movieGenre);
	END IF;
		
    -- now insert all of it into the db
    SET @movieId = (SELECT id FROM movies WHERE title = movieTitle LIMIT 1);
    SET @starId = (SELECT id FROM stars WHERE name = movieStar LIMIT 1);
    SET @genreId = (SELECT id FROM genres WHERE name = movieGenre LIMIT 1);
    
    INSERT INTO movies(id, title, year, director) VALUES (@movieId, movieTitle, movieYear, movieDirector);
    INSERT INTO stars_in_movies(starId, movieId) VALUES (@starId, @movieId);
    INSERT INTO genres_in_movies(genreId, movieId) VALUES (@genreId, @movieId);
    SELECT "movieID: " + @movieId + " starID: " + @starId + " genreID: " + @genreId;
END
$$

-- Change back DELIMITER to ;
DELIMITER ;


DELIMITER $$
CREATE PROCEDURE add_star (IN starName VARCHAR(100), IN starYear INTEGER)
BEGIN
	SET @starId = CONCAT("nm", 
					CAST((SELECT MAX(SUBSTRING(id, 3)) + 1 FROM stars) AS UNSIGNED));
    INSERT INTO stars(id, name, birthYear) VALUES (@starId, starName, starYear);
    SELECT @starId;
END
$$
DELIMITER ;


