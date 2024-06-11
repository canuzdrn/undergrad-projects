# Movie DB
CmpE 321 - Introduction to Database Systems Project

## Application
Project is basically a web application which has built using Python with Flask framework. MySQL and PyMySQL have been used as the database management system and SQL connector respectively. Application also has a simple user interface that's been built using HTML files (templates).

## Dependencies
Application is built using Flask, hence one need to install Flask framework in order to run the application. Flask can be installed using pip via following command (pip3 for Python 3+)
```
pip3 install flask
```
Also PyMySQL has been used as the SQL connector, hence one needs to install PyMySQL library to run the application.
```
pip3 install pymysql
```

## Run
After installations one can run the application via below command: (Make sure you are in the "app" folder)
```
flask run
```
While application is running one can visit the homepage via visiting http://127.0.0.1:5000

User Interface is simple and easy to use, one can view all operations after logging in to the application (As a database manager, director or audience)

## Database Configuration
Database Configuration:
host = "127.0.0.1"
user = "root"
password = "admin123"
db = "MovieDB"

- If one wants to change the configuration info about the database, (s)he needs to change the source code since configuration information is hardcoded into the Python code
- One make sure to establish MySQL connection and ensure that MySQL service is running
- Besides the database connection, one need to create a schema called "MovieDB" and run following SQL files as queries. Below sql files ensure relevant tables and triggers are created (included in "sql" file)
```
createTables.sql
triggers.sql
```



