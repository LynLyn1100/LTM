/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import connection.DatabaseConnection;
import model.UserModel;
/**
 *
 * @author admin
 */
public class UserController {
    //  SQL
    private final String INSERT_USER = "INSERT INTO users (username, password, score, win, draw, lose, avgCompetitor, avgTime) VALUES (?, ?, 0, 0, 0, 0, 0, 0)";
    
    private final String CHECK_USER = "SELECT userId from users WHERE username = ? limit 1";
    
    private final String LOGIN_USER = "SELECT username, password, score FROM users WHERE username=? AND password=?";
    
    private final String GET_INFO_USER = "SELECT username, password, score, win, draw, lose, avgCompetitor, avgTime FROM users WHERE username=?";
    
    private final String UPDATE_USER = "UPDATE users SET score = ?, win = ?, draw = ?, lose = ?, avgCompetitor = ?, avgTime = ? WHERE username=?";
    //  Instance
    private final Connection con;
    
    public UserController() {
        this.con = DatabaseConnection.getInstance().getConnection();
    }

    
  public String register(String username, String password) {
    try {
        // Kiểm tra người dùng đã tồn tại
        PreparedStatement p = con.prepareStatement(CHECK_USER);
        p.setString(1, username);
        ResultSet r = p.executeQuery();

        // Kiểm tra nếu người dùng đã tồn tại
        if (r.next()) { // Sử dụng next() để kiểm tra sự tồn tại của người dùng
            r.close();
            p.close();
            return "failed;User already exists";
        } else {
            // Chèn người dùng mới
            r.close(); 
            p.close();
            
            p = con.prepareStatement(INSERT_USER);
            p.setString(1, username);
            p.setString(2, password);
            int rowsInserted = p.executeUpdate();
            p.close();

            if (rowsInserted > 0) {
                return "success;User registered successfully";
            } else {
                return "failed;No rows inserted";
            }
        }
    } catch (SQLException e) {
        e.printStackTrace(); // In lỗi SQL
        return "failed;SQL error: " + e.getMessage(); // Trả về thông báo lỗi
    } catch (Exception e) {
        e.printStackTrace(); // In lỗi khác
        return "failed;Unexpected error: " + e.getMessage(); // Trả về thông báo lỗi
    }
}
  
  
  
    public String login(String username, String password) {
    // Kiểm tra người dùng
    try {
        PreparedStatement p = con.prepareStatement(LOGIN_USER);
        // Đăng nhập người dùng 
        p.setString(1, username);
        p.setString(2, password);
        ResultSet r = p.executeQuery();
        
        if (r.next()) { // Sử dụng r.next() để kiểm tra xem có bản ghi nào không
            float score = r.getFloat("score");
            return "success;" + username + ";" + score;
        } else {
            return "failed;Please enter the correct account password!";
        }
    } catch (SQLException e) {
        e.printStackTrace(); // In ra lỗi SQL
        return "failed;SQL error: " + e.getMessage(); // Trả về thông báo lỗi
    } catch (Exception e) {
        e.printStackTrace(); // In ra lỗi chung
        return "failed;Unexpected error: " + e.getMessage(); // Trả về thông báo lỗi
    }
}

    
    public String getInfoUser(String username) {
        UserModel user = new UserModel();
        try {
            PreparedStatement p = con.prepareStatement(GET_INFO_USER);
            p.setString(1, username);
            
            ResultSet r = p.executeQuery();
            while(r.next()) {
                user.setUserName(r.getString("username"));
                user.setScore(r.getFloat("score"));
                user.setWin(r.getInt("win"));
                user.setDraw(r.getInt("draw"));
                user.setLose(r.getInt("lose"));
                user.setAvgCompetitor(r.getFloat("avgCompetitor"));
                user.setAvgTime(r.getFloat("avgTime"));
            }
            return "success;" + user.getUserName() + ";" + user.getScore() + ";" + user.getWin() + ";" + user.getDraw() + ";" + user.getLose() + ";" + user.getAvgCompetitor() + ";" + user.getAvgTime() ;
        } catch (SQLException e) {
            e.printStackTrace();
        }   
        return null;
    }
    
    public boolean updateUser(UserModel user) throws SQLException {
        boolean rowUpdated;
        PreparedStatement p = con.prepareStatement(UPDATE_USER);
        //  Login User 
        p.setFloat(1, user.getScore());
        p.setInt(2, user.getWin());
        p.setInt(3, user.getDraw());
        p.setInt(4, user.getLose());
        p.setFloat(5, user.getAvgCompetitor());
        p.setFloat(6, user.getAvgTime());
        p.setString(7, user.getUserName());

//            ResultSet r = p.executeQuery();
        rowUpdated = p.executeUpdate() > 0;
        return rowUpdated;
    }

    public UserModel getUser(String username) {
        UserModel user = new UserModel();
        try {
            PreparedStatement p = con.prepareStatement(GET_INFO_USER);
            p.setString(1, username);
            
            ResultSet r = p.executeQuery();
            while(r.next()) {
                user.setUserName(r.getString("username"));
                user.setScore(r.getFloat("score"));
                user.setWin(r.getInt("win"));
                user.setDraw(r.getInt("draw"));
                user.setLose(r.getInt("lose"));
                user.setAvgCompetitor(r.getFloat("avgCompetitor"));
                user.setAvgTime(r.getFloat("avgTime"));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }   
        return null;
    }
}
