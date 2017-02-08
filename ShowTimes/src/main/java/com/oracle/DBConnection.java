package com.oracle;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
 
public class DBConnection {
    //static reference to itself
    private static DBConnection instance = new DBConnection();
    private static Optional<String> ipAddress = Optional.ofNullable(System.getenv("SHOWTIMES_DATABASE_HOST"));
    public static final String URL = "jdbc:mysql://"+ipAddress.orElse("192.168.99.100")+":3308/cinema";
    public static final String USER = "cinema_service";
    public static final String PASSWORD = "welcome1";
    public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver"; 
     
    //private constructor
    private DBConnection() {
        try {
            //Step 2: Load MySQL Java driver
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
     
    private Connection createConnection() {
        Connection connection = null;
        try {
            //Step 3: Establish Java MySQL connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("ERROR: Unable to Connect to Database.");
        }
        return connection;
    }   
     
    public static Connection getConnection() {
        return instance.createConnection();
    }
}