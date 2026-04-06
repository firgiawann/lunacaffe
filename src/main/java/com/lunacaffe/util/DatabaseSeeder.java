package com.lunacaffe.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseSeeder {

    public static void seedMenus(Connection conn) {
        String countSql = "SELECT count(*) FROM menus";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Tabel menu kosong. Memulai seeding menu produk dengan Icon / Emoji...");
                
                String insertSql = "INSERT INTO menus (id, nama, kategori, harga, stok, image_path, is_new, is_bestseller) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement pst = conn.prepareStatement(insertSql)) {
                    conn.setAutoCommit(false);
                    
                    Object[][] menuData = {
                        {"M-001", "Espresso", "Coffee", 15000.0, 100, 0, 0},
                        {"M-002", "Americano", "Coffee", 20000.0, 100, 0, 1},
                        {"M-003", "Hot Cappuccino", "Hot Drink", 25000.0, 50, 0, 1},
                        {"M-004", "Hot Latte", "Hot Drink", 25000.0, 50, 0, 0},
                        {"M-005", "Hot Caramel Macchiato", "Hot Drink", 28000.0, 40, 1, 1},
                        {"M-006", "Hot Mochaccino", "Hot Drink", 28000.0, 40, 0, 0},
                        {"M-007", "Flat White", "Hot Drink", 26000.0, 30, 1, 0},
                        {"M-008", "Piccolo", "Hot Drink", 22000.0, 30, 0, 0},
                        {"M-009", "Hot Matcha Espresso", "Hot Drink", 30000.0, 25, 1, 0},
                        
                        {"M-010", "Iced Americano", "Cold Drink", 22000.0, 100, 0, 1},
                        {"M-011", "Iced Latte", "Cold Drink", 27000.0, 60, 0, 1},
                        {"M-012", "Iced Cappuccino", "Cold Drink", 27000.0, 60, 0, 0},
                        {"M-013", "Vanilla Sweet Cold Brew", "Cold Drink", 32000.0, 40, 1, 1},
                        {"M-014", "Avocado Coffee Frappe", "Cold Drink", 35000.0, 30, 1, 1},
                        {"M-015", "Caramel Frappuccino", "Cold Drink", 35000.0, 40, 0, 1},
                        {"M-016", "Coffee Jelly", "Cold Drink", 28000.0, 30, 0, 0},
                        {"M-017", "Iced Mocha", "Cold Drink", 29000.0, 50, 0, 0},
                        
                        {"M-018", "Wedang Jahe Madu", "Non-Coffee", 18000.0, 50, 0, 0},
                        {"M-019", "Hot Chocolate", "Non-Coffee", 25000.0, 60, 0, 1},
                        {"M-020", "Iced Chocolate", "Non-Coffee", 27000.0, 60, 0, 1},
                        {"M-021", "Matcha Latte (Hot)", "Non-Coffee", 28000.0, 40, 0, 1},
                        {"M-022", "Iced Matcha Latte", "Non-Coffee", 30000.0, 40, 0, 1},
                        {"M-023", "Taro Latte", "Non-Coffee", 26000.0, 30, 0, 0},
                        {"M-024", "Red Velvet Latte", "Non-Coffee", 26000.0, 30, 1, 0},
                        {"M-025", "Lemon Tea", "Non-Coffee", 18000.0, 80, 0, 0},
                        {"M-026", "Lychee Tea", "Non-Coffee", 22000.0, 80, 0, 1},
                        {"M-027", "Peach Tea", "Non-Coffee", 22000.0, 80, 1, 0},
                        
                        {"M-028", "Butter Croissant", "Bread", 20000.0, 20, 0, 1},
                        {"M-029", "Chocolatine", "Bread", 22000.0, 20, 0, 0},
                        {"M-030", "Almond Croissant", "Bread", 25000.0, 15, 1, 1},
                        {"M-031", "Classic Cheesecake", "Snack", 30000.0, 10, 0, 1},
                        {"M-032", "Tiramisu", "Snack", 32000.0, 10, 0, 0},
                        {"M-033", "Fudgy Brownies", "Snack", 18000.0, 25, 0, 0},
                        {"M-034", "French Fries", "Snack", 20000.0, 50, 0, 1},
                        {"M-035", "Chicken Wings", "Snack", 35000.0, 30, 1, 1},
                        {"M-036", "Beef Nachos", "Snack", 30000.0, 20, 1, 0},
                        {"M-037", "Dimsum Mentai", "Snack", 25000.0, 30, 0, 1}
                    };
                    
                    for (Object[] item : menuData) {
                        pst.setString(1, (String) item[0]);  // id
                        String nm = (String) item[1];
                        pst.setString(2, nm);  // nama
                        String kat = (String) item[2];
                        pst.setString(3, kat); // kategori
                        pst.setDouble(4, (Double) item[3]);  // harga
                        pst.setInt(5, (Integer) item[4]);    // stok
                        
                        // Menentukan logic Icon / Emoji berdasarkan relevansi nama atau kategori
                        String emoji = "🍽️";
                        if (nm.toLowerCase().contains("matcha") || nm.toLowerCase().contains("tea") || nm.toLowerCase().contains("wedang")) {
                            emoji = "🍵";
                        } else if (kat.contains("Hot Drink") || kat.contains("Coffee") || nm.toLowerCase().contains("hot")) {
                            emoji = "☕";
                        } else if (kat.contains("Cold Drink") || nm.toLowerCase().contains("iced")) {
                            emoji = "🥤";
                        } else if (kat.contains("Bread") || nm.toLowerCase().contains("croissant")) {
                            emoji = "🥐";
                        } else if (nm.toLowerCase().contains("fries") || nm.toLowerCase().contains("nachos") || nm.toLowerCase().contains("wings") || nm.toLowerCase().contains("dimsum")) {
                            emoji = "🍟"; // Savory snacks
                        } else if (nm.toLowerCase().contains("cake") || nm.toLowerCase().contains("tiramisu") || nm.toLowerCase().contains("brownies") || nm.toLowerCase().contains("chocolate")) {
                            emoji = "🍰"; // Sweet snacks
                        }
                        
                        pst.setString(6, emoji); // simpan emoji icon ke field image_path
                        pst.setInt(7, (Integer) item[5]);    // is_new
                        pst.setInt(8, (Integer) item[6]);    // is_bestseller
                        pst.addBatch();
                    }
                    
                    pst.executeBatch();
                    conn.commit();
                    System.out.println("Berhasil menyisipkan 37 menu produk (Icon/Emoji).");
                } catch (Exception ex) {
                    conn.rollback();
                    System.err.println("Gagal saat seeding menu: " + ex.getMessage());
                } finally {
                    conn.setAutoCommit(true);
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal mengecek isi tabel menu: " + e.getMessage());
        }
    }
}
