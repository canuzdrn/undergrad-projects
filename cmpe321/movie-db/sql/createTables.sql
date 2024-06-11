-- audience is a sub-type of user relation hence inherits its attributes such as username, password, name and surname
CREATE TABLE Audience (
	username varchar(100),
    password varchar(100),
    name varchar(100),
    surname varchar(100),
    PRIMARY KEY (username)
);

-- rating platform has platform_id and platform_name and both of them are unique
CREATE TABLE Rating_Platform (
	platform_id int,
    platform_name varchar(100),
    PRIMARY KEY (platform_id),
    UNIQUE (platform_name)
);

-- director is a sub-type of user relation hence inherits its attributes such as username, password, name and surname
-- additionally director has an attribute called nation and a director have exactly one nation
-- since there exist only one row for each director (username as primary key) setting nation as NOT NULL will handle this constraint
CREATE TABLE Director (
	username varchar(100),
    password varchar(100),
    name varchar(100),
    surname varchar(100),
    nation varchar(100) NOT NULL,
    platform_id int,
    PRIMARY KEY (username),
    FOREIGN KEY (platform_id)
		REFERENCES Rating_Platform(platform_id)
        ON UPDATE NO ACTION ON DELETE CASCADE
);

-- since one audience can subscribe to multiple rating platform (unlike directors)
-- we need to store them (audience - rating platform pairs) in a different relation/table
CREATE TABLE Audience_RPlatform (
	username varchar(100),
    platform_id int,
    PRIMARY KEY (username,platform_id),
    FOREIGN KEY (username) 
		REFERENCES Audience(username)
		ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (platform_id) 
		REFERENCES Rating_platform(platform_id)
		ON UPDATE NO ACTION ON DELETE CASCADE
);

-- movie relation basically includes the necessary movie session attributes related to movie information
CREATE TABLE Movie (
	movie_id int,
    movie_name varchar(100),
    duration int,
    dir_username varchar(100) NOT NULL,
    PRIMARY KEY (movie_id),
    FOREIGN KEY (dir_username) 
		REFERENCES Director(username)
		ON UPDATE CASCADE ON DELETE CASCADE
);

-- we must create additional table to keep predecessor of movies
-- since there may be more than one predecessor for a movie we can not keep pred_id as an attribute in a movie relation
CREATE TABLE Predecessor (
	movie_id int,
    pred_id int,
    PRIMARY KEY (movie_id,pred_id),
	FOREIGN KEY (movie_id) 
		REFERENCES Movie(movie_id)
		ON UPDATE NO ACTION ON DELETE CASCADE,
	FOREIGN KEY (pred_id) 
		REFERENCES Movie(movie_id)
		ON UPDATE NO ACTION ON DELETE CASCADE
);

-- theatre relation basically includes the necessary movie session attributes related to theatre information
CREATE TABLE Theatre (
	theatre_id int,
    theatre_name varchar(100),
    theatre_cap int,
    district varchar(100),
    PRIMARY KEY (theatre_id)
);

-- since we have theatre and movie relations in order to keep related attributes of movie sessions
-- there are only session id, time slots and date attributes left to keep
CREATE TABLE Movie_Session (
	session_id int,
    time_slot int,
    date date,
    movie_id int,
    theatre_id int,
    PRIMARY KEY (session_id),
    FOREIGN KEY (movie_id) 
		REFERENCES Movie(movie_id)
		ON UPDATE NO ACTION ON DELETE CASCADE,
    FOREIGN KEY (theatre_id) 
		REFERENCES Theatre(theatre_id)
		ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE Reserves (
	theatre_id int,
    date date,
    starting_slot int,
    ending_slot int,
    PRIMARY KEY (theatre_id,date,starting_slot,ending_slot),
    FOREIGN KEY (theatre_id) 
		REFERENCES Theatre(theatre_id)
		ON UPDATE NO ACTION ON DELETE CASCADE
);

-- ratings are kept on additional table and will be used to calculate average rating of movies
-- note: on delete cascade is used, because having a rating of NOBODY seemed nonsense to me
CREATE TABLE Rating (
	username varchar(100),
    movie_id int,
    rating float,
    PRIMARY KEY (username,movie_id),
    FOREIGN KEY (username) 
		REFERENCES Audience(username)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (movie_id) 
		REFERENCES Movie(movie_id)
		ON UPDATE NO ACTION ON DELETE CASCADE,
	CHECK (rating <= 5.0 AND rating >= 0.0)
);

-- Average rating table basically holds the average rating of movies and updated each time if there is a new rating added to the Ratings table
CREATE TABLE Average_Rating (
    movie_id int,
    avg_rating float,
    PRIMARY KEY (movie_id),
    FOREIGN KEY (movie_id) 
		REFERENCES Movie(movie_id)
		ON UPDATE NO ACTION ON DELETE CASCADE,
	CHECK (avg_rating <= 5.0 AND avg_rating >= 0.0)
);

-- since a user can buy more than one ticket (movie session)
-- we need to store them in a different table instead of using it as an attribute
CREATE TABLE Ticket (
	username varchar(100),
    session_id int,
    PRIMARY KEY (username, session_id),
    FOREIGN KEY (username) 
		REFERENCES Audience(username)
		ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (session_id) 
		REFERENCES Movie_Session(session_id)
		ON UPDATE NO ACTION ON DELETE CASCADE
);

-- genre has genre id and genre name where both of them are unique
CREATE TABLE Genre (
	genre_id int,
    genre_name varchar(100),
    PRIMARY KEY (genre_id),
    UNIQUE (genre_name)
);

-- since a movie may belong to multiple genres we need to store them in a different table
CREATE TABLE Movie_Genres (
	movie_id int,
	genre_id int,
    PRIMARY KEY (movie_id,genre_id),
    FOREIGN KEY (movie_id) 
		REFERENCES Movie(movie_id)
		ON UPDATE NO ACTION ON DELETE CASCADE,
    FOREIGN KEY (genre_id) 
		REFERENCES Genre(genre_id)
		ON UPDATE NO ACTION ON DELETE CASCADE
);

-- database manager is an independent relation and have attributes username and password
-- where each db manager is identified with their username
CREATE TABLE DB_Manager (
	username varchar(100),
    password varchar(100),
    PRIMARY KEY (username)
);

