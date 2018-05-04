USE moviedb;

# 


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