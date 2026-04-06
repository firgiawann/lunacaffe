package com.lunacaffe.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:lunacaffe.db";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println("Gagal koneksi ke SQLite: " + e.getMessage());
        }
        return conn;
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            if (conn != null) {
                // Table Users
                String sqlUsers = "CREATE TABLE IF NOT EXISTS users ("
                        + " id VARCHAR PRIMARY KEY,"
                        + " nama VARCHAR NOT NULL,"
                        + " username VARCHAR UNIQUE NOT NULL,"
                        + " password VARCHAR NOT NULL,"
                        + " role VARCHAR NOT NULL)";
                stmt.execute(sqlUsers);

                // Table Menus
                String sqlMenus = "CREATE TABLE IF NOT EXISTS menus ("
                        + " id VARCHAR PRIMARY KEY,"
                        + " nama VARCHAR NOT NULL,"
                        + " kategori VARCHAR,"
                        + " harga REAL,"
                        + " stok INTEGER,"
                        + " image_path VARCHAR,"
                        + " is_new INTEGER,"
                        + " is_bestseller INTEGER)";
                stmt.execute(sqlMenus);

                // Table Orders
                String sqlOrders = "CREATE TABLE IF NOT EXISTS orders ("
                        + " id VARCHAR PRIMARY KEY,"
                        + " nama_pelanggan VARCHAR,"
                        + " total_harga REAL,"
                        + " status VARCHAR,"
                        + " created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";
                stmt.execute(sqlOrders);

                // Table Order Details
                String sqlOrderDetails = "CREATE TABLE IF NOT EXISTS order_details ("
                        + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " order_id VARCHAR,"
                        + " menu_id VARCHAR,"
                        + " qty INTEGER,"
                        + " subtotal REAL,"
                        + " FOREIGN KEY(order_id) REFERENCES orders(id),"
                        + " FOREIGN KEY(menu_id) REFERENCES menus(id))";
                stmt.execute(sqlOrderDetails);

                // Seed Accounts
                String seedAdmin = "INSERT OR IGNORE INTO users (id, nama, username, password, role) " +
                                   "VALUES ('U-001', 'Super Admin', 'admin', '123', 'admin')";
                stmt.execute(seedAdmin);
                
                String seedKasir = "INSERT OR IGNORE INTO users (id, nama, username, password, role) " +
                                   "VALUES ('U-002', 'Kasir Depan', 'kasir1', '123', 'kasir')";
                stmt.execute(seedKasir);

                // Seed Menus via Factory
                DatabaseSeeder.seedMenus(conn);

                System.out.println("Database SQLite lunacaffe.db berhasil diinisialisasi.");
            }
        } catch (SQLException e) {
            System.err.println("Error init database: " + e.getMessage());
        }
    }
}
