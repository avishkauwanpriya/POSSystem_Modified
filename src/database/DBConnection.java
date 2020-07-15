package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection dbConnection;
    private static Connection connection;

    private DBConnection() throws ClassNotFoundException, SQLException {

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/foodcity2", "root", "ijse");


    }

    public static DBConnection getDbConnection() throws SQLException, ClassNotFoundException {
        return dbConnection==null? new DBConnection() : dbConnection;
    }
    public static Connection getConnection(){
        return connection;

    }



}
