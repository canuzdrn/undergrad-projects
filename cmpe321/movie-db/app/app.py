from flask import Flask, render_template, request
import pymysql

app = Flask(__name__)

logged_director_username = ""
logged_audience_username = ""

class Database:
    def __init__(self):
        host = "127.0.0.1"
        user = "root"
        password = "admin123"
        db = "MovieDB"
        self.con = pymysql.connect(host=host, user=user, password=password,db=db, cursorclass=pymysql.cursors.DictCursor)
        self.cur = self.con.cursor()

# home
@app.route("/")
def home():
    return render_template("home.html")


# Logins
@app.route("/dbmLogin")
def dbm_log():
    return render_template("dbm_login.html")

@app.route("/dirLogin")
def dir_log():
    return render_template("dir_login.html")

@app.route("/audienceLogin")
def audience_log():
    return render_template("audience_login.html")



# Login Processors  (REQUIREMENTS - 1,9)
@app.route('/processDBM_Login', methods=['POST'])
def process_DBM_login():
    username = request.form.get('username')
    password = request.form.get('password')
    try:
        db = Database()
        query = "SELECT * FROM DB_Manager WHERE username = %s AND password = %s"
        db.cur.execute(query,(username,password))
        row = db.cur.fetchone()
        if row is not None:
            return render_template("successful_login_dbm.html")
        else:
            return "Invalid username or password"

    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred during login"
    
@app.route('/processDir_Login', methods=['POST'])
def process_Dir_login():
    global logged_director_username

    username = request.form.get('username')
    password = request.form.get('password')
    try:
        db = Database()
        query = "SELECT * FROM Director WHERE username = %s AND password = %s"
        db.cur.execute(query,(username,password))
        row = db.cur.fetchone()
        if row is not None:
            logged_director_username = username
            return render_template("successful_login_dir.html")
        else:
            return "Invalid username or password"

    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred during login"
    
@app.route('/processAudience_Login', methods=['POST'])
def process_audience_login():
    global logged_audience_username

    username = request.form.get('username')
    password = request.form.get('password')
    try:
        db = Database()
        query = "SELECT * FROM Audience WHERE username = %s AND password = %s"
        db.cur.execute(query,(username,password))
        row = db.cur.fetchone()
        if row is not None:
            logged_audience_username = username
            return render_template("successful_login_audience.html")
        else:
            return "Invalid username or password"

    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred during login"
    

    
# RETURN PAGES AFTER SUCCESSFUL OPERATIONS
@app.route("/dbm_operations")
def dbm_operations_selector():
    return render_template("successful_login_dbm.html")

@app.route("/dir_operations")
def dir_operations_selector():
    return render_template("successful_login_dir.html")

@app.route("/audience_operations")
def audience_operations_selector():
    return render_template("successful_login_audience.html")


    

# REQUIREMENT 2 - TAKE RELEVANT INFO 
@app.route("/taker_add_audience.html")
def audience_addition_page():
    return render_template("taker_add_audience.html")

# REQUIREMENT 2 - PROCESS GIVEN INFO
@app.route("/add_audience", methods=['POST'])
def process_audience_addition():
    username = request.form.get('username')
    password = request.form.get('password')
    name = request.form.get('name')
    surname = request.form.get('surname')
    try:
        db = Database()
        query = "INSERT INTO Audience VALUES (%s,%s,%s,%s)"
        db.cur.execute(query,(username,password,name,surname))
        db.con.commit()
        return render_template("successful_dbm.html")

    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"
    
# REQUIREMENT 2 - TAKE RELEVANT INFO 
@app.route("/taker_add_director.html")
def director_addition_page():
    return render_template("taker_add_director.html")

# REQUIREMENT 2 - PROCESS GIVEN INFO
@app.route("/add_director", methods=['POST'])
def process_director_addition():
    username = request.form.get('username')
    password = request.form.get('password')
    name = request.form.get('name')
    surname = request.form.get('surname')
    nation = request.form.get('nation')
    platform_id = int(request.form.get('platform_id'))
    try:
        db = Database()
        query = "INSERT INTO Director VALUES (%s,%s,%s,%s,%s,%s)"
        db.cur.execute(query,(username,password,name,surname,nation,platform_id))
        db.con.commit()
        return render_template("successful_dbm.html")

    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"

# REQUIREMENT 3 - TAKE RELEVANT INFO
@app.route("/taker_delete_audience.html")
def audience_deletion_page():
    return render_template("taker_delete_audience.html")

# REQUIREMENT 3 - PROCESS GIVEN INFO
@app.route("/delete_audience", methods=['POST'])
def process_audience_deletion():
    username = request.form.get('username')
    try:
        db = Database()
        query = "DELETE FROM Audience WHERE username=%s"
        db.cur.execute(query,(username))
        db.con.commit()
        return render_template("successful_dbm.html")
    
    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"

# REQUIREMENT 4 - TAKE RELEVANT INFO
@app.route("/taker_update_platformID.html")
def platformID_update_page():
    return render_template("taker_update_platformID.html")

# REQUIREMENT 4 - PROCESS GIVEN INFO
@app.route("/update_platformID", methods=['POST'])
def process_update_platformID():
    username = request.form.get('username')
    platform_id = int(request.form.get('platform_id'))
    try:
        db = Database()
        query = "UPDATE Director SET platform_id=%s WHERE username=%s"
        db.cur.execute(query,(platform_id,username))
        db.con.commit()
        return render_template("successful_dbm.html")
    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"

# REQUIREMENT 5 - PROCESS INFO (NO NEED ANY TAKE)
@app.route("/retrieve_directors")
def retrieve_directors():
    try:
        db = Database()
        db.cur.execute("SELECT username, name, surname, nation, platform_id FROM Director")
        directors = db.cur.fetchall()
        return render_template("show_all_directors.html",results=directors)
    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"


# REQUIREMENT 6 - TAKE RELEVANT INFO
@app.route("/taker_ratings_of_user.html")
def get_username_for_ratings():
    return render_template("taker_ratings_of_user.html")

# REQUIREMENT 6 - PROCESS GIVEN INFO
@app.route("/ratings_of_user", methods=["POST"])
def process_username_for_ratings():
    username = request.form.get("username")
    try:
        db = Database()
        query = "SELECT Rating.movie_id, Movie.movie_name, Rating.rating FROM Rating, Movie WHERE Rating.movie_id = Movie.movie_id AND Rating.username = %s"
        db.cur.execute(query,(username))
        ratings = db.cur.fetchall()
        return render_template("show_rating_of_user.html",results = ratings)
    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"
    

# REQUIREMENT 7 - TAKE RELEVANT INFO
@app.route("/taker_movies_of_director.html")
def get_username_of_director():
    return render_template("taker_movies_of_director.html")

# REQUIREMENT 7 - PROCESS RELEVANT INFO
@app.route("/show_movies_of_director", methods=["POST"])
def show_director_movies():
    dir_username = request.form.get("username")
    movie_id_set = set()
    try:
        db = Database()
        query = """SELECT M.movie_id, M.movie_name, M.duration, M.dir_username, T.theatre_id, T.theatre_name, T.theatre_cap, T.district, MS.time_slot, MS.date
        FROM Movie_Session MS, Movie M, Theatre T
        WHERE MS.movie_id = M.movie_id AND MS.theatre_id = T.theatre_id AND M.dir_username = %s"""
        db.cur.execute(query,(dir_username))
        rows = db.cur.fetchall()
        for row in rows:
            movie_id_set.add(row["movie_id"])

        for movie_id in movie_id_set:
            genre_query = "SELECT genre_id FROM Movie_Genres WHERE movie_id = %s"
            db.cur.execute(genre_query,(movie_id))
            genres = db.cur.fetchall()                # [{"genre_id" : 80001}, {"genre_id" : 80002}, ...]

            # stringify genre_list
            genre_list = []
            for element in genres:
                genre_list.append(str(element["genre_id"]))
            str_genre_list = ",".join(genre_list)

            pred_query = "SELECT pred_id FROM Predecessor WHERE movie_id = %s"
            db.cur.execute(pred_query,(movie_id))
            preds = db.cur.fetchall()                       # [{"pred_id" : 11}, {"pred_id" : 12}, ...]
            
            # stringify pred_list
            pred_list = []
            for element in preds:
                pred_list.append(str(element["pred_id"]))
            str_pred_list = ",".join(pred_list)

            for row in rows:
                if row["movie_id"] == movie_id:
                    row["genre_list"] = str_genre_list
                    row["predecessors_list"] = str_pred_list
        return render_template("show_movies_of_director.html",results = rows)
    
    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"




# REQUIREMENT 8 - TAKE RELEVANT INFO
@app.route("/taker_movie_id_for_avg_rating.html")
def get_movie_id_for_average_rating():
    return render_template("taker_movie_id_for_avg_rating.html")

# REQUIREMENT 8 - PROCESS RELEVANT INFO
@app.route("/show_avg_rating", methods=["POST"])
def show_avg_rating():
    movie_id = request.form.get("movie_id")
    try:
        db = Database()
        query = """SELECT Average_Rating.movie_id, Movie.movie_name, Average_Rating.avg_rating
         FROM Average_Rating, Movie WHERE Average_Rating.movie_id = %s AND Average_Rating.movie_id = Movie.movie_id """
        db.cur.execute(query,(movie_id))
        rows = db.cur.fetchall()
        return render_template("show_avg_rating.html",results = rows)
    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"
    

# REQUIREMENT 10 - TAKE RELEVANT INFO
@app.route("/taker_available_theatres.html")
def get_date_time_slot():
    return render_template("taker_available_theatres.html")    

# REQUIREMENT 10 - PROCESS RELEVANT INFO
@app.route("/show_available_theatres", methods=["POST"])
def show_avail_theatres():
    date = request.form.get("date")
    slot = request.form.get("slot")
    try:
        db = Database()
        query = """SELECT DISTINCT Theatre.theatre_id, Theatre.district, Theatre.theatre_cap
        FROM Theatre, Reserves 
        WHERE Theatre.theatre_id NOT IN 
        (SELECT Reserves.Theatre_id 
        FROM Reserves
        WHERE Reserves.date = %s AND (%s >= Reserves.starting_slot AND %s <= Reserves.ending_slot))"""
        db.cur.execute(query,(date,slot,slot))
        rows = db.cur.fetchall()
        return render_template("show_available_theatres.html",results = rows)
    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"

# REQUIREMENT 11 TAKE RELEVANT INFO
@app.route("/taker_add_session.html")
def get_session_infos():
    return render_template("taker_add_session.html")

# REQUIREMENT 11 PROCESS GIVEN INFO
@app.route("/add_movie_session", methods=["POST"])
def process_session_infos():
    session_id = request.form.get("session_id")
    movie_id = request.form.get("movie_id")
    movie_name = request.form.get("movie_name")
    duration = request.form.get("duration")
    genre_list = request.form.get("genre_list")
    dir_username = request.form.get("dir_username")
    theatre_id = request.form.get("theatre_id")
    theatre_name = request.form.get("theatre_name")
    theatre_cap = request.form.get("theatre_cap")
    district = request.form.get("district")
    time_slot = request.form.get("time_slot")
    date = request.form.get("date")
    try:
        db = Database()
        # add movie if not exist
        is_valid_query = "SELECT COUNT(*) as count FROM Movie WHERE movie_id = %s"
        db.cur.execute(is_valid_query,(movie_id))
        is_valid = db.cur.fetchone()["count"]
        if is_valid == 0:
            query_movie = "INSERT INTO Movie VALUES (%s,%s,%s,%s)"
            db.cur.execute(query_movie,(movie_id,movie_name,duration,dir_username))
            db.con.commit()
            genres = genre_list.split(",")
            for genre_id in genres:
                query_genre = "INSERT INTO Movie_Genres VALUES (%s,%s)"
                db.cur.execute(query_genre,(movie_id,genre_id))
                db.con.commit()

        # add theatre if not exist
        is_valid_query = "SELECT COUNT(*) as count FROM Theatre WHERE theatre_id = %s"
        db.cur.execute(is_valid_query,(theatre_id))
        is_valid = db.cur.fetchone()["count"]
        if is_valid == 0:
            query_theatre = "INSERT INTO Theatre VALUES (%s,%s,%s,%s)"
            db.cur.execute(query_theatre,(theatre_id,theatre_name,theatre_cap,district))
            db.con.commit()

        # insert session
        query_session = "INSERT INTO Movie_Session VALUES (%s,%s,%s,%s,%s)"
        db.cur.execute(query_session,(session_id,time_slot,date,movie_id,theatre_id))
        db.con.commit()
        
        return render_template("successful_dir.html")
    
    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"


# REQUIREMENT 12 - TAKE RELEVANT INFO
@app.route("/taker_add_pred.html")
def get_pred_id():
    return render_template("taker_add_pred.html")

# REQUIREMENT 12 - PROCESS GIVEN INFO
@app.route("/add_pred", methods=['POST'])
def process_pred_addition():
    movie_id = request.form.get('movie_id')
    pred_id = request.form.get('pred_id')
    try:
        db = Database()
        pred_list = pred_id.split(",")
        for pred in pred_list:
            query = "INSERT INTO Predecessor VALUES (%s,%s)"
            db.cur.execute(query,(movie_id,pred))
            db.con.commit()
        return render_template("successful_dir.html")

    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"
    

# REQUIREMENT 13
@app.route("/show_movies_of_himself")
def show_movies_of_himself():
    global logged_director_username
    username = logged_director_username
    id_set = set()
    try:
        db = Database()
        query = """SELECT M.movie_id, M.movie_name, M.duration, M.dir_username, T.theatre_id, T.theatre_name, T.theatre_cap, T.district, MS.time_slot, MS.date
        FROM Movie_Session MS, Movie M, Theatre T
        WHERE MS.movie_id = M.movie_id AND MS.theatre_id = T.theatre_id AND M.dir_username = %s
        ORDER BY M.movie_id ASC"""
        db.cur.execute(query,(username))
        rows = db.cur.fetchall()
        for row in rows:
            id_set.add(row["movie_id"])
        for movie_id in id_set:
            genre_query = "SELECT genre_id FROM Movie_Genres WHERE movie_id = %s"
            db.cur.execute(genre_query,(movie_id))
            genres = db.cur.fetchall()                
            genre_list = []
            for element in genres:
                genre_list.append(str(element["genre_id"]))
            str_genre_list = ",".join(genre_list)

            pred_query = "SELECT pred_id FROM Predecessor WHERE movie_id = %s"
            db.cur.execute(pred_query,(movie_id))
            preds = db.cur.fetchall()                       
            pred_list = []
            for element in preds:
                pred_list.append(str(element["pred_id"]))
            str_pred_list = ",".join(pred_list)

            for row in rows:
                if row["movie_id"] == movie_id:
                    row["genre_list"] = str_genre_list
                    row["predecessors_list"] = str_pred_list
        # print(rows)
        return render_template("show_movies_of_himself.html",results = rows)

    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"
    

# REQUIREMENT 14 - TAKE RELEVANT INFO
@app.route("/taker_tickets_for_movie.html")
def get_movie_id():
    return render_template("taker_tickets_for_movie.html")

# REQUIREMENT 14 - PROCESS GIVEN INFO
@app.route("/show_tickets_for_movie", methods=['POST'])
def show_tickets_for_movie():
    global logged_director_username
    movie_id = request.form.get('movie_id')
    try:
        db = Database()
        valid = "SELECT COUNT(*) AS count FROM Movie WHERE Movie.dir_username = %s AND Movie.movie_id = %s"
        db.cur.execute(valid,(logged_director_username,movie_id))
        is_valid = db.cur.fetchone()["count"]
        if is_valid == 0:
            return "Movie ID does not belong to the logged in director"
        query = """SELECT Audience.username, Audience.name, Audience.surname FROM Audience, Ticket, Movie_Session
        WHERE Audience.username = Ticket.username AND Ticket.session_id = Movie_Session.session_id AND Movie_Session.movie_id = %s"""
        db.cur.execute(query,(movie_id))
        rows = db.cur.fetchall()
        return render_template("show_tickets_for_movie.html", results = rows)

    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"


# REQUIREMENT 15 - TAKE RELEVANT INFO
@app.route("/taker_update_movie.html")
def get_new_name():
    return render_template("taker_update_movie.html")

# REQUIREMENT 15 - PROCESS GIVEN INFO
@app.route("/update_movie_name", methods=['POST'])
def process_update_movie_name():
    global logged_director_username
    movie_id = request.form.get('movie_id')
    new_name = request.form.get('new_name')
    try:
        db = Database()
        valid = "SELECT COUNT(*) AS count FROM Movie WHERE Movie.dir_username = %s AND Movie.movie_id = %s"
        db.cur.execute(valid,(logged_director_username,movie_id))
        is_valid = db.cur.fetchone()["count"]
        if is_valid == 0:
            return "Movie ID does not belong to the logged in director"
        query = "UPDATE Movie SET movie_name = %s WHERE movie_id = %s"
        db.cur.execute(query,(new_name,movie_id))
        db.con.commit()
        return render_template("successful_dir.html")

    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"


# REQUIREMENT 16
@app.route("/show_all_movies")
def show_all_movies():
    id_set = set()
    try:
        db = Database()
        query = """SELECT DISTINCT M.movie_id, M.movie_name, D.surname, D.platform_id, MS.theatre_id, MS.time_slot
        FROM Movie_Session MS, Movie M, Theatre T, Director D
        WHERE MS.movie_id = M.movie_id AND M.dir_username = D.username
        """
        db.cur.execute(query)
        rows = db.cur.fetchall()
        for row in rows:
            id_set.add(row["movie_id"])

        for movie_id in id_set:
            pred_query = "SELECT pred_id FROM Predecessor WHERE movie_id = %s"
            db.cur.execute(pred_query,(movie_id))
            preds = db.cur.fetchall()                      
            pred_list = []
            for element in preds:
                pred_list.append(str(element["pred_id"]))
            str_pred_list = ",".join(pred_list)

            for row in rows:
                if row["movie_id"] == movie_id:
                    row["predecessors_list"] = str_pred_list

        return render_template("show_all_movies.html",results = rows)

    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"


# REQUIREMENT 17 - TAKE RELEVANT INFO
@app.route("/taker_buy_ticket.html")
def get_sessionID_for_ticket():
    return render_template("taker_buy_ticket.html")

# REQUIREMENT 17 - PROCESS GIVEN INFO
@app.route("/buy_ticket", methods=["POST"])
def buy_ticket():
    global logged_audience_username
    username = logged_audience_username
    session_id = request.form.get("session_id")
    try:
        db = Database()
        query = "INSERT INTO Ticket VALUES (%s,%s)"
        print(username)
        db.cur.execute(query,(username,session_id))
        db.con.commit()
        return render_template("successful_audience.html")

    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"
    

# REQUIREMENT 18
@app.route("/show_tickets")
def show_tickets():
    global logged_audience_username
    username = logged_audience_username
    id_set = set()
    try:
        db = Database()
        query = """SELECT M.movie_id, M.movie_name, MS.session_id
        FROM Movie_Session MS, Movie M, Ticket T
        WHERE T.username = %s AND T.session_id = MS.session_id  AND MS.movie_id = M.movie_id"""
        db.cur.execute(query,(username))
        rows = db.cur.fetchall()
        for row in rows:
            id_set.add(row["movie_id"])
        
        for movie_id in id_set:
            rating_query = "SELECT R.rating FROM Rating R WHERE R.movie_id = %s AND R.username = %s"
            db.cur.execute(rating_query,(movie_id,username))
            rating = db.cur.fetchone()                 
            if rating == None:
                rating = "null"
            else:
                rating = rating["rating"]

            avg_rating_query = "SELECT AR.avg_rating FROM Average_Rating AR WHERE AR.movie_id = %s"
            db.cur.execute(avg_rating_query,(movie_id))
            avg_rating = db.cur.fetchone()["avg_rating"]
            for row in rows:
                if row["movie_id"] == movie_id:
                    row["rating"] = rating
                    row["avg_rating"] = avg_rating
        
        return render_template("show_tickets_of_audience.html",results = rows)

    except pymysql.Error as error:
        print(f"MySQL Error: {error}")
        return "An error occurred"