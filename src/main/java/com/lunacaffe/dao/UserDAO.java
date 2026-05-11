package com.lunacaffe.dao;

import com.lunacaffe.model.Admin;
import com.lunacaffe.model.Kasir;
import com.lunacaffe.model.Pegawai;
import com.lunacaffe.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public Pegawai getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                if ("admin".equalsIgnoreCase(role)) {
                    Admin adm = new Admin(
                        rs.getString("nama"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                    adm.setId(rs.getString("id"));
                    return adm;
                } else {
                    Kasir ksr = new Kasir(
                        rs.getString("nama"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                    ksr.setId(rs.getString("id"));
                    return ksr;
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal cek user: " + e.getMessage());
        }
        return null;
    }
    
    public List<Pegawai> getAllUsers() {
        List<Pegawai> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
             while (rs.next()) {
                 Pegawai p;
                 if("admin".equalsIgnoreCase(rs.getString("role"))) {
                     p = new Admin(rs.getString("nama"), rs.getString("username"), rs.getString("password"));
                 } else {
                     p = new Kasir(rs.getString("nama"), rs.getString("username"), rs.getString("password"));
                 }
                 
                 p.setId(rs.getString("id"));
                 list.add(p);
             }
        } catch(Exception e) {}
        return list;
    }
    
    public boolean addUser(String id, String username, String password, String nama, String role) {
        String sql = "INSERT INTO users (id, username, password, nama, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
             pst.setString(1, id);
             pst.setString(2, username);
             pst.setString(3, password);
             pst.setString(4, nama);
             pst.setString(5, role);
             return pst.executeUpdate() > 0;
        } catch(Exception e) { return false; }
    }
    
    public boolean deleteUser(String id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
             pst.setString(1, id);
             return pst.executeUpdate() > 0;
        } catch(Exception e) { return false; }
    }
}
