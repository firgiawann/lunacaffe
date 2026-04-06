package com.lunacaffe.dao;

import com.lunacaffe.model.Menu;
import com.lunacaffe.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    public List<Menu> getAllMenus() {
        List<Menu> list = new ArrayList<>();
        String sql = "SELECT * FROM menus";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Menu m = new Menu(
                    rs.getString("id"),
                    rs.getString("nama"),
                    rs.getString("kategori"),
                    rs.getDouble("harga"),
                    rs.getInt("stok"),
                    rs.getString("image_path"),
                    rs.getInt("is_new") == 1,
                    rs.getInt("is_bestseller") == 1
                );
                list.add(m);
            }
        } catch (Exception e) {
            System.err.println("Gagal mengambil data menu: " + e.getMessage());
        }
        return list;
    }
    
    // Tambahan Perkembangan (CRUD - Update)
    public boolean addStok(String idMenu, int qtyAdded) {
        String sql = "UPDATE menus SET stok = stok + ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
             pst.setInt(1, qtyAdded);
             pst.setString(2, idMenu);
             int affected = pst.executeUpdate();
             return affected > 0;
        } catch (Exception e) {
            System.err.println("Update stok gagal: " + e.getMessage());
            return false;
        }
    }
    
    // Tambahan Perkembangan (CRUD - Update Price)
    public boolean updateHarga(String idMenu, double hargaBaru) {
        String sql = "UPDATE menus SET harga = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
             pst.setDouble(1, hargaBaru);
             pst.setString(2, idMenu);
             int affected = pst.executeUpdate();
             return affected > 0;
        } catch (Exception e) {
            System.err.println("Update harga gagal: " + e.getMessage());
            return false;
        }
    }
}
