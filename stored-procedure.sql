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
    IN movieStar VARCHAR(100),
    IN movieGenre VARCHAR(32))
startAdd: BEGIN
	-- inserting movie into movie table
	IF ((SELECT COUNT(*) FROM movies WHERE title = movieTitle AND year = movieYear AND director = movieDirector) > 0) THEN -- movie exists, don't do anything
		SELECT "Movie already exists" as message;
        LEAVE startAdd;
	END IF;
	IF movieTitle = '' THEN -- movie title is empty, don't do anything
		SELECT "Invalid input for movie title" as message;
        LEAVE startAdd;
    END IF;    
    IF movieYear = "" OR movieYEAR = null THEN -- movie year is empty, don't do anything
		SELECT "Invalid input for movie year" as message;
        LEAVE startAdd;
	END IF;
    IF movieDirector = "" THEN -- movie director is empty, don't do anything
		SELECT "Invalid input for director" as message;
        LEAVE startAdd;
	END IF;
    
    
    
    -- checking the star
    IF movieStar = "" THEN -- movie star is empty 
		SELECT "Invalid input for movie star" as message;
        LEAVE startAdd;
	END IF;
    
    -- checking the genre
    IF movieGenre = "" THEN -- movie genre is empty
		SELECT "Invalid input for genre" as message;
        LEAVE startAdd;
	END IF;
	
    -- check if star or genres are in the tables
	IF ((SELECT COUNT(*) FROM stars WHERE name = movieStar) = 0) THEN
		SET @starId = CONCAT("nm", 
					CAST((SELECT MAX(SUBSTRING(id, 3)) + 1 FROM stars) AS UNSIGNED));
		INSERT INTO stars(id, name) VALUES (@starId, movieStar);
	END IF;
    
    IF ((SELECT COUNT(*) FROM genres WHERE name = movieGenre) = 0) THEN
        INSERT INTO genres(name) values (movieGenre);
	END IF;
		
    -- now insert all of it into the db
    SET @movieId = CONCAT("tt", CAST((SELECT MAX(SUBSTRING(id, 3)) + 1 FROM movies) AS UNSIGNED));
    SET @starId = (SELECT id FROM stars WHERE name = movieStar LIMIT 1);
    SET @genreId = (SELECT id FROM genres WHERE name = movieGenre LIMIT 1);
    
	INSERT INTO movies(id, title, year, director) VALUES (@movieId, movieTitle, movieYear, movieDirector);
	INSERT INTO stars_in_movies(starId, movieId) VALUES (@starId, @movieId);
	INSERT INTO genres_in_movies(genreId, movieId) VALUES (@genreId, @movieId);
	SELECT CONCAT('movieID: ', @movieId, ' starID: ', @starId, ' genreID: ', @genreId) AS message;


	LEAVE startAdd;
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

CALL add_movie("help me2", 2011, "tom us", "anna", "t1");

