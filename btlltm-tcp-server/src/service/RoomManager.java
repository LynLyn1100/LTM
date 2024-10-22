/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import helper.RandomString;
import model.ProductModel;
import run.ServerRun;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class RoomManager {
    ArrayList<Room> rooms;
    RandomString idGenerator;

    public RoomManager() {
        rooms = new ArrayList<>();
        idGenerator = new RandomString(5);
    }

    public Room createRoom() {
        Room room = new Room(idGenerator.nextString());
        rooms.add(room);

        return room;
    }

    public boolean add(Room r) {
        if (!rooms.contains(r)) {
            rooms.add(r);
            return true;
        }
        return true;
    }

    public boolean remove(Room r) {
        if (rooms.contains(r)) {
            rooms.remove(r);
            return true;
        }
        return false;
    }

    public Room find(String id) {
        for (Room r : rooms) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    public int getSize() {
        return rooms.size();
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }
    
    // Các phương thức mới để hỗ trợ quản lý sản phẩm

    public ProductModel getRandomProductForRoom() {
        return ServerRun.productManager.getRandomProduct();
    }

    public void assignProductToRoom(Room room) {
        ProductModel product = getRandomProductForRoom();
        room.setCurrentProduct(product);
    }

    public List<ProductModel> getAllProducts() {
        return ServerRun.productManager.getAllProducts();
    }

    public ProductModel getProductById(int id) {
        return ServerRun.productManager.getProduct(id);
    }

    public void addNewProduct(ProductModel product) {
        ServerRun.productManager.addProduct(product);
    }

    public void updateProduct(ProductModel product) {
        ServerRun.productManager.updateProduct(product);
    }

    public void removeProduct(int productId) {
        ServerRun.productManager.removeProduct(productId);
    }
}
