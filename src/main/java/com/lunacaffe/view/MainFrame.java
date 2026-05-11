package com.lunacaffe.view;

import com.lunacaffe.util.DatabaseConnection;
import com.lunacaffe.util.SessionManager;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    
    public static final Color COLOR_PRIMARY = Color.decode("#2A113A");
    public static final Color COLOR_SECONDARY = Color.decode("#5D2E61");
    public static final Color COLOR_BACKGROUND = Color.decode("#FCFAF8");

    private CardLayout cardLayout;
    private JPanel cardsPanel;
    
    private JButton btnPelanggan;
    private JButton btnPegawai;
    
    private DashboardPanel dashboardPanel;

    public MainFrame() {
        
        DatabaseConnection.initializeDatabase();
        
        setTitle("Lunacaffe POS System v2.2");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BACKGROUND);

        initUI();
    }

    private void initUI() {
        try {
            java.awt.image.BufferedImage iconImg = new java.awt.image.BufferedImage(64, 64, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g2 = iconImg.createGraphics();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 46));
            g2.drawString("ðŸŒŒ", 8, 48); 
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
            g2.drawString("ðŸŒ™", 20, 36); 
            g2.dispose();
            setIconImage(iconImg);
        } catch (Exception ex) {}

        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BACKGROUND);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel titleLabel = new JLabel("  ðŸŒ™ Lunacaffe POS System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        tabsPanel.setBackground(Color.WHITE);
        tabsPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        
        btnPelanggan = new JButton("Antarmuka Pelanggan");
        styleTabButton(btnPelanggan, true);
        
        btnPegawai = new JButton("Sistem Kasir & Admin");
        styleTabButton(btnPegawai, false);

        tabsPanel.add(btnPelanggan);
        tabsPanel.add(btnPegawai);

        
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        
        CustomerPanel customerPanel = new CustomerPanel();
        LoginPanel loginPanel = new LoginPanel(this);
        dashboardPanel = new DashboardPanel(this);

        cardsPanel.add(customerPanel, "CUSTOMER");
        cardsPanel.add(loginPanel, "LOGIN");
        cardsPanel.add(dashboardPanel, "DASHBOARD");

        
        btnPelanggan.addActionListener(e -> {
            styleTabButton(btnPelanggan, true);
            styleTabButton(btnPegawai, false);
            cardLayout.show(cardsPanel, "CUSTOMER");
        });

        btnPegawai.addActionListener(e -> {
            styleTabButton(btnPelanggan, false);
            styleTabButton(btnPegawai, true);
            if (SessionManager.isLoggedIn()) {
                showDashboard();
            } else {
                cardLayout.show(cardsPanel, "LOGIN");
            }
        });

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(tabsPanel, BorderLayout.CENTER);

        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(cardsPanel, BorderLayout.CENTER);
        
        
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.decode("#EAE5E1"));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));

        JLabel lblCopyright = new JLabel("Â© 2026 - Developed by Kelompok 3 | PBO");
        lblCopyright.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCopyright.setForeground(COLOR_PRIMARY);

        JButton btnAbout = new JButton("Tentang Kelompok");
        btnAbout.setContentAreaFilled(false);
        btnAbout.setBorderPainted(false);
        btnAbout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAbout.setForeground(COLOR_PRIMARY);
        btnAbout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAbout.addActionListener(e -> showAboutDialog());

        footerPanel.add(lblCopyright, BorderLayout.WEST);
        footerPanel.add(btnAbout, BorderLayout.EAST);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
    
    private void showAboutDialog() {
        String htmlMsg = "<html><body style='width: 350px; font-family: Segoe UI, sans-serif;'>" +
            "<h2 style='color:#2A113A; text-align:center;'>ðŸŒ™ LUNACAFFE POS SYSTEM</h2>" +
            "<p style='text-align:center; color:gray; margin-top:-10px;'><i>Final Project Edition</i></p>" +
            "<p style='text-align:justify;'>Aplikasi Point of Sales mandiri ini dibangun sebagai pemenuhan Tugas Mata Kuliah <b>Pemrograman Berorientasi Objek (PBO)</b>. Sistem dirancang dengan arsitektur modern <b>MVC (Model-View-Controller)</b> dan konsep Polimorfisme yang solid, dilengkapi manajemen basis data relasional via <b>SQLite</b>.</p>" +
            "<hr style='border:1px dashed #CCC'>" +
            "<b style='color:#2A113A'>DIBANGUN & DIKEMBANGKAN OLEH KELOMPOK 3:</b><ul style='margin-top:5px;'>" +
            "<li><b>FIRGIAWAN LISTIANTO</b></li>" +
            "<li><b>MUH. NABIL MAKARIMSYAH</b></li>" +
            "<li><b>JUMARIA</b></li>" +
            "<li><b>KELVIN SURYA PUTRA</b></li>" +
            "</ul>" +
            "<hr style='border:1px dashed #CCC'>" +
            "<p style='text-align:center; font-size:10px; color:gray;'>Versi Basis Data Lokal V.2.1 | Â© 2026 All Rights Reserved</p>" +
            "</body></html>";

        JOptionPane.showMessageDialog(this, htmlMsg, "Credit & Tim Pengembang", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void styleTabButton(JButton btn, boolean active) {
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (active) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setForeground(COLOR_SECONDARY);
            btn.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_SECONDARY));
            btn.setBorderPainted(true);
        } else {
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setForeground(Color.GRAY);
            btn.setBorderPainted(false);
        }
    }
    
    
    public void showDashboard() {
        dashboardPanel.updateRoleUI();
        cardLayout.show(cardsPanel, "DASHBOARD");
        styleTabButton(btnPelanggan, false);
        styleTabButton(btnPegawai, true);
    }
    
    public void showLogin() {
        cardLayout.show(cardsPanel, "LOGIN");
        styleTabButton(btnPelanggan, false);
        styleTabButton(btnPegawai, true);
    }
}
