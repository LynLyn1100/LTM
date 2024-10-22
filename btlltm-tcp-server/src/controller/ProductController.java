package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connection.DatabaseConnection;
import model.ProductModel;

public class ProductController {
    // SQL queries
    private final String INSERT_PRODUCT = "INSERT INTO product (name, description, price, image_path) VALUES (?, ?, ?, ?)";
    private final String GET_PRODUCT = "SELECT * FROM product WHERE id = ?";
    private final String GET_ALL_PRODUCTS = "SELECT * FROM product";
    private final String UPDATE_PRODUCT = "UPDATE product SET name = ?, description = ?, price = ?, image_path = ? WHERE id = ?";
    private final String DELETE_PRODUCT = "DELETE FROM product WHERE id = ?";
    
    // Instance
    private final Connection con;
    
    public ProductController() {
        this.con = DatabaseConnection.getInstance().getConnection();
    }
    
    public String addProduct(ProductModel product) {
        try {
            PreparedStatement p = con.prepareStatement(INSERT_PRODUCT, PreparedStatement.RETURN_GENERATED_KEYS);
            p.setString(1, product.getName());
            p.setString(2, product.getDescription());
            p.setDouble(3, product.getPrice());
            p.setString(4, product.getImagePath());
            
            int affectedRows = p.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setId(generatedKeys.getInt(1));
                    }
                }
                return "success;Product added successfully";
            } else {
                return "failed;No product added";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "failed;SQL error: " + e.getMessage();
        }
    }
    
    public ProductModel getProduct(int id) {
        try {
            PreparedStatement p = con.prepareStatement(GET_PRODUCT);
            p.setInt(1, id);
            
            ResultSet r = p.executeQuery();
            if (r.next()) {
                return new ProductModel(
                    r.getInt("id"),
                    r.getString("name"),
                    r.getString("description"),
                    r.getDouble("price"),
                    r.getString("image_path")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<ProductModel> getAllProducts() {
        List<ProductModel> products = new ArrayList<>();
        try {
            PreparedStatement p = con.prepareStatement(GET_ALL_PRODUCTS);
            ResultSet r = p.executeQuery();
            
            while (r.next()) {
                products.add(new ProductModel(
                    r.getInt("id"),
                    r.getString("name"),
                    r.getString("description"),
                    r.getDouble("price"),
                    r.getString("image_path")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    public String updateProduct(ProductModel product) {
        try {
            PreparedStatement p = con.prepareStatement(UPDATE_PRODUCT);
            p.setString(1, product.getName());
            p.setString(2, product.getDescription());
            p.setDouble(3, product.getPrice());
            p.setString(4, product.getImagePath());
            p.setInt(5, product.getId());
            
            int affectedRows = p.executeUpdate();
            
            if (affectedRows > 0) {
                return "success;Product updated successfully";
            } else {
                return "failed;No product updated";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "failed;SQL error: " + e.getMessage();
        }
    }
    
    public String deleteProduct(int id) {
        try {
            PreparedStatement p = con.prepareStatement(DELETE_PRODUCT);
            p.setInt(1, id);
            
            int affectedRows = p.executeUpdate();
            
            if (affectedRows > 0) {
                return "success;Product deleted successfully";
            } else {
                return "failed;No product deleted";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "failed;SQL error: " + e.getMessage();
        }
    }
}