package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    
    private static final String PROPERTIES_FILE_PATH = "C:\\Users\\MY PC\\eclipse-workspace\\Loan_management_system\\src\\util\\db.properties";

    public static Connection getConnection() {
        Properties properties = new Properties();

        
        try (FileInputStream fileInputStream = new FileInputStream(PROPERTIES_FILE_PATH)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null; 
        }

        
        String urlString = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");


        try {
            return DriverManager.getConnection(urlString, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; 
    }
}
