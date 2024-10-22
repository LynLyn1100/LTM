package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
    private String jdbcURL = "jdbc:mysql://localhost:3306/bttlltm?useSSL=false";
    private String jdbcUsername = "root";
<<<<<<< HEAD
    private String jdbcPassword = "2607";
=======
    private String jdbcPassword = "2607"; //Admin@123
>>>>>>> af6d489e7cd5c354fef3c4d6519b224552051b49

    private static DatabaseConnection instance;
    private Connection connection;

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private DatabaseConnection() {

    }
    
    public Connection getConnection() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("Connected to Database.");
                connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
             // Kiểm tra kết nối
        if (connection != null) {
            System.out.println("Connection to the database established successfully.");
        } else {
            System.out.println("Connection to the database failed.");
        }
    } catch (SQLException e) {
        // Thông báo chi tiết lỗi SQL
        System.out.println("SQL Error: " + e.getMessage());
        System.out.println("SQL State: " + e.getSQLState());
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        // Thông báo khi không tìm thấy driver
        System.out.println("MySQL JDBC Driver not found.");
        e.printStackTrace();
    }
    return connection;
}
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
