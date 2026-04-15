// =========================
// DATABASE CONNECTION SETUP
// =========================
// This class is responsible for establishing a connection
// between the Java application and PostgreSQL database
// using JDBC (Java Database Connectivity).


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/Contract_system";
    private static final String USER_NAME = "your_postgres_username";
    private static final String PASSWORD = "your_postgres_password";

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL, USER_NAME, PASSWORD);
    }
}
