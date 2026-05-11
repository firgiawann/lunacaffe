package com.lunacaffe.dao;

import com.lunacaffe.model.DetailPesanan;
import com.lunacaffe.model.Pesanan;
import com.lunacaffe.util.DatabaseConnection;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDAO {
    public String getNextOrderNumber() {
        String todayString = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String expectedPrefix = "ORD-" + todayString + "-";
        String sql = "SELECT id FROM orders WHERE id LIKE '" + expectedPrefix + "%' ORDER BY id DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString(1); 
                String seqStr = lastId.substring(lastId.lastIndexOf('-') + 1);
                int seq = Integer.parseInt(seqStr);
                return String.format("%s%03d", expectedPrefix, seq + 1);
            }
        } catch (Exception e) {}
        return expectedPrefix + "001";
    }

    public boolean createOrder(Pesanan pesanan) {
        String sqlOrder = "INSERT INTO orders (id, nama_pelanggan, total_harga, status) VALUES (?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO order_details (order_id, menu_id, qty, subtotal) VALUES (?, ?, ?, ?)";
        String sqlUpdateStok = "UPDATE menus SET stok = stok - ? WHERE id = ? AND stok >= ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstOrder = conn.prepareStatement(sqlOrder)) {
                pstOrder.setString(1, pesanan.getIdPesanan());
                pstOrder.setString(2, pesanan.getNamaPelanggan());
                pstOrder.setDouble(3, pesanan.getTotalHarga());
                pstOrder.setString(4, pesanan.getStatus());
                pstOrder.executeUpdate();
            }

            try (PreparedStatement pstDetail = conn.prepareStatement(sqlDetail);
                 PreparedStatement pstStock = conn.prepareStatement(sqlUpdateStok)) {
                
                for (DetailPesanan detail : pesanan.getItems()) {
                    pstStock.setInt(1, detail.getQty());
                    pstStock.setString(2, detail.getMenu().getIdMenu());
                    pstStock.setInt(3, detail.getQty());
                    int affectedStock = pstStock.executeUpdate();
                    if (affectedStock == 0) {
                        throw new IllegalStateException("Stok tidak cukup untuk " + detail.getMenu().getNamaMenu());
                    }

                    pstDetail.setString(1, pesanan.getIdPesanan());
                    pstDetail.setString(2, detail.getMenu().getIdMenu());
                    pstDetail.setInt(3, detail.getQty());
                    pstDetail.setDouble(4, detail.getSubtotal());
                    pstDetail.addBatch();
                }
                pstDetail.executeBatch();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            System.err.println("Gagal memproses pesanan: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (Exception r) {}
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ignored) {}
        }
        return false;
    }

    public List<Pesanan> getLiveOrders() {
        List<Pesanan> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY created_at DESC"; 
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Pesanan p = new Pesanan(
                    rs.getString("id"),
                    rs.getString("nama_pelanggan"),
                    rs.getString("created_at")
                );
                p.setTotalHarga(rs.getDouble("total_harga"));
                p.setStatus(rs.getString("status"));
                list.add(p);
            }
        } catch (Exception e) {
            System.err.println("Error getLiveOrders: " + e.getMessage());
        }
        return list;
    }
    
    public void updateStatus(String orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
             pst.setString(1, newStatus);
             pst.setString(2, orderId);
             pst.executeUpdate();
        } catch (Exception e) {
            System.err.println("Update DB failed: " + e.getMessage());
        }
    }
    
    
    public String exportRekapitulasiCSV(String dateString, String outputPath) {
        
        String sql = "SELECT id, nama_pelanggan, total_harga, created_at, status FROM orders WHERE date(created_at) = ?";
        double grandTotal = 0.0;
        int rowCount = 0;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             PrintWriter pw = new PrintWriter(new FileWriter(outputPath))) {
             
             pst.setString(1, dateString);
             ResultSet rs = pst.executeQuery();
             
             
             pw.println("ID Pesanan,Nama Pelanggan,Waktu Order,Status,Total Harga");
             
             while (rs.next()) {
                 rowCount++;
                 String id = rs.getString("id");
                 String nama = rs.getString("nama_pelanggan");
                 String created = rs.getString("created_at");
                 String status = rs.getString("status");
                 double total = rs.getDouble("total_harga");
                 grandTotal += total;
                 
                 pw.printf("%s,%s,%s,%s,%.2f\n", id, nama, created, status, total);
             }
             
             
             pw.println(",,,,");
             pw.printf("GRAND TOTAL KESELURUHAN (HARI INI),,,,%.2f\n", grandTotal);
             
             return rowCount + " pesanan diekspor. Total Rp " + grandTotal;
             
        } catch (Exception e) {
            return "Gagal export CSV: " + e.getMessage();
        }
    }
}
