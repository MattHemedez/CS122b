USE moviedb;

# Checkout procedure
DROP PROCEDURE IF EXISTS checkout;

DELIMITER $$
CREATE PROCEDURE checkout (IN uId INT(11), cId VARCHAR(20), 
	fName VARCHAR(50), lName VARCHAR(50), exp DATE)
COMMENT "Use to checkout current cart of userid and add to sales table"

BEGIN
    DECLARE terminate INT DEFAULT 0;
	DECLARE isCreditCardValid VARCHAR(1);
    DECLARE movId VARCHAR(10);
    DECLARE qty INT(11);
    DECLARE cartItems INT(11) DEFAULT 0;
    DECLARE cur CURSOR FOR SELECT ca.movieId, ca.quantity FROM cart AS ca WHERE ca.customerId = uId;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET terminate = terminate + 1;         /*Specify what to do when no more records found*/
	SELECT 'T' INTO isCreditCardValid FROM creditcards AS c WHERE c.id=cId AND c.firstName=fName AND c.lastName=lName AND c.expiration=exp;
    
    IF(isCreditCardValid IS NULL) THEN
		SELECT 'ERROR' AS status, 'INCORRECT CREDITCARD INFO' AS message;
	ELSE
		OPEN cur;
        FETCH cur INTO movId, qty;
		WHILE terminate = 0 DO
			WHILE qty > 0 DO
				INSERT INTO sales (customerId, movieId, saleDate) VALUES (uId, movId, CURDATE());
                SET qty = qty - 1;
                SET cartItems = cartItems + 1;
			END WHILE;
            DELETE FROM cart WHERE customerId = uId AND movieId = movId;
            FETCH cur INTO movId, qty;
        END WHILE;
        IF(cartItems = 0) THEN
			SELECT 'ERROR' AS status, 'CART NOT FOUND' AS message;
		ELSE
			SELECT 'SUCCESS' AS status, '' AS message;
		END IF;
		CLOSE cur;
    END IF;
END$$

# add_movie procedure
DROP PROCEDURE IF EXISTS add_movie;

DELIMITER $$
CREATE PROCEDURE add_movie (IN mov_title VARCHAR(100), IN mov_year int(11), 
	IN mov_director VARCHAR(100), IN mov_star_name VARCHAR(100), IN mov_genre_name VARCHAR(32))
COMMENT "Adds a new movie into the movie table and adds star and genre to their respective tables"

BEGIN
	DECLARE mov_id VARCHAR(10);
    DECLARE mov_exists CHAR(1);
    DECLARE genre_id CHAR(10);
    DECLARE genre_exists CHAR(1);
    DECLARE star_id CHAR(10);
    DECLARE star_exists CHAR (1);
    DECLARE status_message VARCHAR(1000);
    
    SET status_message = '';
    
    SELECT CONCAT("tt", SUBSTRING(id, 3, CHAR_LENGTH(id) - 2 -CHAR_LENGTH(SUBSTRING(id, 3, CHAR_LENGTH(id) - 2) + 1)), SUBSTRING(id, 3, CHAR_LENGTH(id) - 2) + 1)
		INTO mov_id FROM movies ORDER BY id DESC LIMIT 1;
    SELECT DISTINCT 't' INTO mov_exists FROM movies AS m WHERE m.title = mov_title AND m.year = mov_year AND m.director = mov_director;
    SELECT DISTINCT 't' INTO genre_exists FROM genres AS g WHERE g.name = mov_genre_name;
    SELECT DISTINCT 't' INTO star_exists FROM stars AS s WHERE s.name = mov_star_name;
    
    IF (mov_exists = 't') THEN
		SELECT 'ERROR' AS status, 'Movie not added to the database because the movie already exists.' AS message;
	ELSE
		# Insert into movies table
		INSERT INTO movies (id, title, year, director) VALUES (mov_id, mov_title, mov_year, mov_director);
        SET status_message = CONCAT(status_message, 'New Movie was added to movies table. ');
        
        # Insert into genres and genres_in_movies tables
        IF (genre_exists IS NULL) THEN
			INSERT INTO genres (name) VALUES (mov_genre_name);
            SET status_message = CONCAT(status_message, 'New Genre was added to genres table. ');
		END IF;
		SELECT id INTO genre_id FROM genres AS g WHERE g.name LIKE mov_genre_name LIMIT 1;
		INSERT INTO genres_in_movies (genreId, movieId) VALUES (genre_id, mov_id);
        SET status_message = CONCAT(status_message, 'Linked movie to genre in genres_in_movies table. ');
        
        # Insert into stars and stars_in_movies tables
		IF (star_exists IS NULL) THEN
			SELECT CONCAT("nm", SUBSTRING(id, 3, CHAR_LENGTH(id) - 2 -CHAR_LENGTH(SUBSTRING(id, 3, CHAR_LENGTH(id) - 2) + 1)), SUBSTRING(id, 3, CHAR_LENGTH(id) - 2) + 1) INTO star_id FROM stars ORDER BY id DESC LIMIT 1;
			INSERT INTO stars (id, name, birthYear) VALUES (star_id, mov_star_name, NULL);
            SET status_message = CONCAT(status_message, 'New Star was added to stars table. ');
		END IF;
		SELECT id INTO star_id FROM stars AS s WHERE s.name LIKE mov_star_name ORDER BY s.birthYear LIMIT 1;
		INSERT INTO stars_in_movies (starId, movieId) VALUES (star_id, mov_id);
        SET status_message = CONCAT(status_message, 'Linked star to movie in stars_in_movies table.');
        
        SELECT 'SUCCESS' AS status, RTRIM(status_message) AS message;
    END IF;
END$$

# add_star procedure
DROP PROCEDURE IF EXISTS add_star;

DELIMITER $$
CREATE PROCEDURE add_star (IN star_name VARCHAR(100), IN star_birth_year int(11))
COMMENT "Allows an employee to add a star into the system"

BEGIN
	DECLARE star_exists CHAR(1);
    DECLARE star_id VARCHAR(10);
    IF(star_birth_year = '' || star_birth_year = 0) THEN
		SET star_birth_year = NULL;
    END IF;

    SELECT CONCAT("nm", SUBSTRING(id, 3, CHAR_LENGTH(id) - 2 -CHAR_LENGTH(SUBSTRING(id, 3, CHAR_LENGTH(id) - 2) + 1)), SUBSTRING(id, 3, CHAR_LENGTH(id) - 2) + 1) INTO star_id FROM stars ORDER BY id DESC LIMIT 1;
    SELECT DISTINCT 't' INTO star_exists FROM stars AS s WHERE s.name = star_name AND s.birthYear = star_birth_year;
    
    INSERT INTO stars (id, name, birthYear) VALUES (star_id, star_name, star_birth_year);
    
	IF (star_exists LIKE 't') THEN
        SELECT 'SUCCESS' AS status, 'Star added to database. Possibly added duplicate star.' AS message;
	ELSE
		SELECT 'SUCCESS' AS status, 'Star added to database.' AS message;
    END IF;
END$$