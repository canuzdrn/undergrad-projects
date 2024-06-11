-- Trigger for there must be at most 4 db manager

DELIMITER $$

DROP TRIGGER if exists atMostFourDBM $$ -- done
DROP TRIGGER if exists ratingConditions $$ -- done
DROP TRIGGER if exists theatreCapChecker $$ -- done
DROP TRIGGER if exists updateAvgRating $$ -- done
DROP TRIGGER if exists overlapingTimeSlots $$ -- done
DROP TRIGGER if exists mustWatchPreds $$  -- done
DROP TRIGGER if exists initializeAvgRating $$ 
DROP TRIGGER if exists avg_rat_after_audience_del $$  -- done

-- DELIMITER $$

CREATE TRIGGER initializeAvgRating
AFTER INSERT ON movie
FOR EACH ROW
BEGIN
DECLARE init_rating FLOAT DEFAULT 0;
INSERT INTO Average_Rating
VALUES (new.movie_id,init_rating);

END$$

CREATE TRIGGER atMostFourDBM
BEFORE INSERT ON DB_Manager
FOR EACH ROW
BEGIN

DECLARE num_of_dbmanagers INT;
DECLARE msg varchar(128);

SELECT COUNT(*)
FROM DB_Manager
INTO num_of_dbmanagers;

IF num_of_dbmanagers = 4 THEN
        set msg = concat('MyTriggerError: You cant have more than 4 database managers !');
        signal sqlstate '45000' set message_text = msg;
    END IF	; 

END $$

-- Trigger for satisfying rating conditions
-- DELIMITER $$
CREATE TRIGGER ratingConditions
BEFORE INSERT ON Rating
FOR EACH ROW

BEGIN
DECLARE msg varchar(128);

DECLARE platform_id_of_movie INT;
DECLARE subbed INT;

DECLARE ticket_count INT;

DECLARE movie_date DATE;
DECLARE curr_date DATE;

SELECT R.platform_id
FROM Rating_Platform R, Movie M, Director D
WHERE (M.movie_id = new.movie_id) AND (M.dir_username = D.username) AND (D.platform_id = R.platform_id)
INTO platform_id_of_movie;

SELECT COUNT(*)
FROM Audience_RPlatform ARP
WHERE (ARP.username = new.username) AND (ARP.platform_id = platform_id_of_movie)
INTO subbed;


SELECT COUNT(*)
FROM Movie_Session S, Ticket T
WHERE (S.movie_id = new.movie_id) AND (S.session_id = T.session_id) AND (T.username = new.username)
INTO ticket_count;

SELECT MIN(S.date)
FROM Movie_Session S, Ticket T
WHERE (S.movie_id = new.movie_id) AND (S.session_id = T.session_id) AND (T.username = new.username)
INTO movie_date;


IF ticket_count = 0 THEN
	set msg = concat('MyTriggerError: User must have bought a ticket for that movie !');
    signal sqlstate '45000' set message_text = msg;
END IF	;

IF subbed = 0 THEN
	set msg = concat('MyTriggerError: User must have been subscribed to the platform of the Movie !');
    signal sqlstate '45000' set message_text = msg;
END IF	;


SELECT CURDATE() INTO curr_date;

IF movie_date > curr_date THEN
	set msg = concat('MyTriggerError: Cannot rate a movie that has not screened yet !');
    signal sqlstate '45000' set message_text = msg;
END IF	;

END $$

-- Trigger for satisfying theatre capacity conditions while an audience trying to buy a ticket for a movie session

CREATE TRIGGER theatreCapChecker
BEFORE INSERT ON Ticket
FOR EACH ROW

BEGIN

DECLARE msg varchar(128);

DECLARE cap_so_far INT;
DECLARE theatre_cap INT;

SELECT COUNT(*)
FROM Ticket T
WHERE T.session_id = new.session_id
INTO cap_so_far;

SELECT T.theatre_cap
FROM Theatre T, Movie_Session MS
WHERE (MS.session_id = new.session_id) AND (T.theatre_id = MS.theatre_id)
INTO theatre_cap;

IF cap_so_far = theatre_cap THEN
	set msg = concat('MyTriggerError: Theatre Capacity is FULL !');
    signal sqlstate '45000' set message_text = msg;
END IF	;


END $$

-- Trigger for average rating updates

CREATE TRIGGER updateAvgRating
AFTER INSERT ON Rating
FOR EACH ROW

BEGIN

DECLARE new_avg_rating FLOAT;

SELECT AVG(R.rating)
FROM Rating R
WHERE R.movie_id = new.movie_id
INTO new_avg_rating;



IF (SELECT COUNT(*) FROM Average_rating AR WHERE AR.movie_id = new.movie_id) > 0 THEN
	SET SQL_SAFE_UPDATES = 0;
	UPDATE Average_Rating
	SET avg_rating = new_avg_rating
	WHERE movie_id = new.movie_id;
ELSE
	INSERT INTO Average_Rating
    VALUES (new.movie_id,new_avg_rating);
END IF;

END $$

DELIMITER $$
CREATE TRIGGER avg_rat_after_audience_del
BEFORE DELETE ON Audience
FOR EACH ROW

BEGIN

DECLARE new_avg_rating FLOAT;
DECLARE old_avg_rating FLOAT;
DECLARE num_of_ratings_movie_have INT;
DECLARE affected_movie_id INT;
DECLARE deleted_rating_of_user FLOAT;
DECLARE done BOOL default false;
DECLARE cursor_rating CURSOR FOR SELECT R.movie_id, R.rating FROM Rating R WHERE R.username = old.username;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = true;

OPEN cursor_rating;
cursorloop:loop
FETCH cursor_rating INTO affected_movie_id,deleted_rating_of_user;
if done = true then 
	leave cursorloop;
end if;

SELECT AR.avg_rating
FROM Average_Rating AR
WHERE AR.movie_id = affected_movie_id
INTO old_avg_rating;

SELECT COUNT(*)
FROM Rating R
WHERE R.movie_id = affected_movie_id
INTO num_of_ratings_movie_have;

SET new_avg_rating = (old_avg_rating * num_of_ratings_movie_have - deleted_rating_of_user) / (num_of_ratings_movie_have - 1);

SET SQL_SAFE_UPDATES = 0;
UPDATE Average_Rating
SET avg_rating = new_avg_rating
WHERE movie_id = affected_movie_id;

END loop cursorloop;
CLOSE cursor_rating;

END $$


-- Trigger for checking no overlapping movie sessions occur in the same theatre and the same date


CREATE TRIGGER overlapingTimeSlots
BEFORE INSERT ON Movie_Session
FOR EACH ROW

BEGIN

DECLARE msg VARCHAR(128);
DECLARE is_occupied INT;
DECLARE starting_time INT;
DECLARE ending_time INT;
DECLARE movie_duration INT;

SELECT M.duration
FROM Movie M
WHERE M.movie_id = new.movie_id
INTO movie_duration;

SET starting_time = new.time_slot;
SET ending_time = starting_time + movie_duration - 1;


SELECT EXISTS (SELECT * 
				FROM Reserves Res 
				WHERE (Res.theatre_id = new.theatre_id) AND (Res.date = new.date) 
                AND ( (starting_time >= Res.starting_slot AND starting_time <= Res.ending_slot) 
						OR (ending_time >= Res.starting_slot AND ending_time <= Res.ending_slot)
                        OR (starting_time < Res.starting_slot AND ending_time > Res.ending_slot)
                        )
                )
INTO is_occupied;

IF is_occupied = 1 THEN
	set msg = concat('MyTriggerError: Screening times cannot overlap for the sam theatre in same date !');
    signal sqlstate '45000' set message_text = msg;
ELSE
	INSERT INTO Reserves
    VALUES (new.theatre_id,new.date,starting_time,ending_time);

END IF;

END $$



CREATE TRIGGER mustWatchPreds
BEFORE INSERT ON Ticket
FOR EACH ROW

BEGIN

-- want to loop through predecessor movie ids
DECLARE watched INT DEFAULT -1;
DECLARE msg varchar(128);
DECLARE this_movie_date DATE;
DECLARE sample_pred INT;
DECLARE done BOOL default false;
DECLARE cursor_pred CURSOR FOR SELECT P.pred_id FROM predecessor P, movie_session MS WHERE MS.session_id = new.session_id AND P.movie_id = MS.movie_id;				-- cursor for selected movie's predecessors
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = true;

-- temp : table for movies that user has bought ticket, need also date column to check

SELECT MS.date
FROM Movie_session MS
WHERE MS.session_id = new.session_id
INTO this_movie_date;

OPEN cursor_pred;
cursorloop:loop
FETCH cursor_pred INTO sample_pred;
if done = true then 
	leave cursorloop;
end if;

SET watched = (SELECT COUNT(*) 
				FROM (SELECT MS.movie_id, MS.date 
						FROM Ticket T, Movie_Session MS 
						WHERE T.username = new.username AND T.session_id = MS.session_id) temp
				WHERE temp.movie_id = sample_pred AND temp.date < this_movie_date);
IF watched = 0 THEN
	set msg = concat('MyTriggerError: You need to watch all predecessor movies in order to buy a ticket for that movie !');
    signal sqlstate '45000' set message_text = msg;
    leave cursorloop;
END IF;

END loop cursorloop;
CLOSE cursor_pred;


END $$
