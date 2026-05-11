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

    public boolean addMenu(Menu menu) {
        String sql = "INSERT INTO menus (id, nama, kategori, harga, stok, image_path, is_new, is_bestseller) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, menu.getIdMenu());
            pst.setString(2, menu.getNamaMenu());
            pst.setString(3, menu.getKategori());
            pst.setDouble(4, menu.getHarga());
            pst.setInt(5, menu.getStok());
            pst.setString(6, menu.getImagePath());
            pst.setInt(7, menu.isNew() ? 1 : 0);
            pst.setInt(8, menu.isBestseller() ? 1 : 0);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Tambah menu gagal: " + e.getMessage());
            return false;
        }
    }

    public boolean updateMenu(Menu menu) {
        String sql = "UPDATE menus SET nama = ?, kategori = ?, harga = ?, stok = ?, image_path = ?, "
                + "is_new = ?, is_bestseller = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, menu.getNamaMenu());
            pst.setString(2, menu.getKategori());
            pst.setDouble(3, menu.getHarga());
            pst.setInt(4, menu.getStok());
            pst.setString(5, menu.getImagePath());
            pst.setInt(6, menu.isNew() ? 1 : 0);
            pst.setInt(7, menu.isBestseller() ? 1 : 0);
            pst.setString(8, menu.getIdMenu());
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Update menu gagal: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMenu(String idMenu) {
        String sql = "DELETE FROM menus WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, idMenu);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Hapus menu gagal: " + e.getMessage());
            return false;
        }
    }

    public String getNextMenuId() {
        String sql = "SELECT id FROM menus WHERE id LIKE 'M-%' ORDER BY CAST(SUBSTR(id, 3) AS INTEGER) DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1);
                int seq = Integer.parseInt(lastId.substring(lastId.lastIndexOf('-') + 1));
                return String.format("M-%03d", seq + 1);
            }
        } catch (Exception e) {
            System.err.println("Gagal membuat ID menu baru: " + e.getMessage());
        }
        return "M-001";
    }
}
