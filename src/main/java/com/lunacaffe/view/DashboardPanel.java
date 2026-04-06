package com.lunacaffe.view;

import com.lunacaffe.dao.MenuDAO;
import com.lunacaffe.dao.OrderDAO;
import com.lunacaffe.dao.UserDAO;
import com.lunacaffe.model.Menu;
import com.lunacaffe.model.Pegawai;
import com.lunacaffe.model.Pesanan;
import com.lunacaffe.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DashboardPanel extends JPanel {
    private MainFrame mainFrame;
    private JLabel lblUserRole;
    
    private JButton btnPesananLive;
    private JButton btnRekapitulasi;
    private JButton btnKelolaMenu;
    private JButton btnKelolaAkun;
    
    private JTable tableOrders;
    private DefaultTableModel tModel;
    
    private JTable tableMenu;
    private DefaultTableModel tMenuModel;
    
    private JTable tableAkun;
    private DefaultTableModel tAkunModel;
    
    private CardLayout contentCardLayout;
    private JPanel mainContentArea;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(MainFrame.COLOR_BACKGROUND);
        initUI();
    }

    private void initUI() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(MainFrame.COLOR_PRIMARY);

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setOpaque(false);
        profilePanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel lblLoginAs = new JLabel("Status Login:");
        lblLoginAs.setForeground(Color.LIGHT_GRAY);
        
        lblUserRole = new JLabel("Role");
        lblUserRole.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblUserRole.setForeground(Color.WHITE);

        profilePanel.add(lblLoginAs);
        profilePanel.add(lblUserRole);

        // Sidebar menus
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        btnPesananLive = createSidebarMenu("🗂️ Pesanan Live");
        btnRekapitulasi = createSidebarMenu("📊 Rekap Harian");
        btnKelolaMenu = createSidebarMenu("📦 Kelola Stok Menu");
        btnKelolaAkun = createSidebarMenu("⚙️ Kelola Akun Staff");
        
        btnPesananLive.addActionListener(e -> { contentCardLayout.show(mainContentArea, "LIVE_ORDER"); loadLiveOrders(); });
        btnRekapitulasi.addActionListener(e -> contentCardLayout.show(mainContentArea, "REKAP"));
        btnKelolaMenu.addActionListener(e -> { contentCardLayout.show(mainContentArea, "MENU"); loadMenuAdmin(); });
        btnKelolaAkun.addActionListener(e -> { contentCardLayout.show(mainContentArea, "AKUN"); loadAkunAdmin(); });

        menuPanel.add(btnPesananLive);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnRekapitulasi);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnKelolaMenu);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnKelolaAkun);

        JButton btnLogout = new JButton("🚪 Log Out");
        btnLogout.setBackground(Color.decode("#8A2E3B"));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            SessionManager.logout();
            mainFrame.showLogin();
        });

        sidebar.add(profilePanel, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        
        JPanel bottomSidebar = new JPanel(new BorderLayout());
        bottomSidebar.setOpaque(false);
        bottomSidebar.setBorder(new EmptyBorder(20, 20, 25, 20));
        bottomSidebar.add(btnLogout, BorderLayout.CENTER);
        sidebar.add(bottomSidebar, BorderLayout.SOUTH);

        // Content Area setup
        contentCardLayout = new CardLayout();
        mainContentArea = new JPanel(contentCardLayout);
        mainContentArea.setBackground(MainFrame.COLOR_BACKGROUND);

        mainContentArea.add(buildLiveOrderPanel(), "LIVE_ORDER");
        mainContentArea.add(buildRekapitulasiPanel(), "REKAP");
        mainContentArea.add(buildKelolaMenuPanel(), "MENU");
        mainContentArea.add(buildKelolaAkunPanel(), "AKUN");

        add(sidebar, BorderLayout.WEST);
        add(mainContentArea, BorderLayout.CENTER);
    }
    
    private JButton createSidebarMenu(String title) {
        JButton btn = new JButton(title);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode("#462157"));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setMargin(new Insets(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel buildLiveOrderPanel() {
        JPanel contentArea = new JPanel(new BorderLayout(20, 20));
        contentArea.setBackground(MainFrame.COLOR_BACKGROUND);
        contentArea.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Pesanan Masuk (Live Queue)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(MainFrame.COLOR_PRIMARY);
        
        JButton btnRefresh = new JButton("🔄 Perbarui Tabel");
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadLiveOrders());
        
        JButton btnSelesai = new JButton("Tandai Selesai");
        btnSelesai.setBackground(MainFrame.COLOR_SECONDARY);
        btnSelesai.setForeground(Color.WHITE);
        btnSelesai.setFocusPainted(false);
        btnSelesai.addActionListener(e -> markAsDone());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        actionPanel.add(btnSelesai);
        actionPanel.add(btnRefresh);

        tableHeader.add(lblTitle, BorderLayout.WEST);
        tableHeader.add(actionPanel, BorderLayout.EAST);

        String[] cols = {"ID / Tiket", "Nama Pelanggan", "Total Checkout", "Status Order"};
        tModel = new DefaultTableModel(cols, 0);
        tableOrders = new JTable(tModel) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableOrders.setRowHeight(40);
        tableOrders.getTableHeader().setReorderingAllowed(false);
        tableOrders.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollTable = new JScrollPane(tableOrders);
        scrollTable.getViewport().setBackground(Color.WHITE);
        contentArea.add(tableHeader, BorderLayout.NORTH);
        contentArea.add(scrollTable, BorderLayout.CENTER);
        return contentArea;
    }
    
    private JPanel buildKelolaMenuPanel() {
        JPanel contentArea = new JPanel(new BorderLayout(20, 20));
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Inventory & Stok Gudang");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(MainFrame.COLOR_PRIMARY);
        
        JButton btnTambahStok = new JButton("📥 Inflow (Restok)");
        btnTambahStok.setBackground(Color.decode("#2E8B57")); 
        btnTambahStok.setForeground(Color.WHITE);
        btnTambahStok.setFocusPainted(false);
        btnTambahStok.addActionListener(e -> addStockAction());
        
        JButton btnAturHarga = new JButton("🏷️ Ubah Harga");
        btnAturHarga.setBackground(Color.decode("#F39C12")); 
        btnAturHarga.setForeground(Color.WHITE);
        btnAturHarga.setFocusPainted(false);
        btnAturHarga.addActionListener(e -> updatePriceAction());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(btnAturHarga);
        actionPanel.add(btnTambahStok);

        tableHeader.add(lblTitle, BorderLayout.WEST);
        tableHeader.add(actionPanel, BorderLayout.EAST);

        String[] cols = {"ID Menu", "Nama Produk", "Kategori", "Harga Dasar", "Sisa Stok Fisik"};
        tMenuModel = new DefaultTableModel(cols, 0);
        tableMenu = new JTable(tMenuModel) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableMenu.setRowHeight(35);
        tableMenu.getTableHeader().setReorderingAllowed(false);
        tableMenu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollTable = new JScrollPane(tableMenu);
        scrollTable.getViewport().setBackground(Color.WHITE);

        contentArea.add(tableHeader, BorderLayout.NORTH);
        contentArea.add(scrollTable, BorderLayout.CENTER);
        return contentArea;
    }
    
    // PERKEMBANGAN FINAL: FITUR KELOLA AKUN ADMIN
    private JPanel buildKelolaAkunPanel() {
        JPanel contentArea = new JPanel(new BorderLayout(20, 20));
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Perizinan Master Akun");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(MainFrame.COLOR_PRIMARY);
        
        JButton btnTambahKasir = new JButton("➕ Daftarkan Akun Baru");
        btnTambahKasir.setBackground(MainFrame.COLOR_PRIMARY); 
        btnTambahKasir.setForeground(Color.WHITE);
        btnTambahKasir.setFocusPainted(false);
        btnTambahKasir.addActionListener(e -> addKasirAction());
        
        JButton btnHapusKasir = new JButton("🗑️ Blokir & Hapus Akun");
        btnHapusKasir.setBackground(Color.decode("#C0392B")); 
        btnHapusKasir.setForeground(Color.WHITE);
        btnHapusKasir.setFocusPainted(false);
        btnHapusKasir.addActionListener(e -> deleteKasirAction());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(btnHapusKasir);
        actionPanel.add(btnTambahKasir);

        tableHeader.add(lblTitle, BorderLayout.WEST);
        tableHeader.add(actionPanel, BorderLayout.EAST);

        String[] cols = {"ID Pegawai", "Nama Lengkap", "Username", "Jabatan (Role)", "Sandi Terekam"};
        tAkunModel = new DefaultTableModel(cols, 0);
        tableAkun = new JTable(tAkunModel) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableAkun.setRowHeight(35);
        tableAkun.getTableHeader().setReorderingAllowed(false);
        tableAkun.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollTable = new JScrollPane(tableAkun);
        scrollTable.getViewport().setBackground(Color.WHITE);

        contentArea.add(tableHeader, BorderLayout.NORTH);
        contentArea.add(scrollTable, BorderLayout.CENTER);
        return contentArea;
    }

    private JPanel buildRekapitulasiPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        JLabel lblTitle = new JLabel("Automasi Jurnal Keuangan Harian");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(MainFrame.COLOR_PRIMARY);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        JPanel cardLabel = new JPanel();
        cardLabel.setLayout(new BoxLayout(cardLabel, BoxLayout.Y_AXIS));
        cardLabel.setBackground(Color.decode("#FEF8E7"));
        cardLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E8DECB"), 1, true),
            new EmptyBorder(10, 20, 20, 20)
        ));

        JLabel info = new JLabel("<html><div style='text-align: center;'><h2>📄 CSV EXPORTER</h2>Aplikasi akan mengalkulasi total pendapatan penjualan yang telah bertanda <b style='color:green'>Selesai</b> dari awal <i>Shift</i> hari ini untuk dipindahkan ke Desktop Anda dalam format Microsoft Excel/CSV.</div></html>");
        info.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton btnExport = new JButton("Ekspor (Klik 1x Saja)");
        btnExport.setBackground(Color.decode("#217346")); 
        btnExport.setForeground(Color.WHITE);
        btnExport.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExport.putClientProperty("JButton.buttonType", "roundRect");
        
        btnExport.addActionListener(e -> {
            OrderDAO dao = new OrderDAO();
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String savedFile = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "LUNACAFFE_REKAP_" + today + ".csv";
            
            String logMsg = dao.exportRekapitulasiCSV(today, savedFile);
            JOptionPane.showMessageDialog(this, "Berhasil! Cek folder Desktop/Layar Depan PC Anda.\n\n" + logMsg, "Rekap Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        cardLabel.add(Box.createRigidArea(new Dimension(0, 10)));
        cardLabel.add(info);
        cardLabel.add(Box.createRigidArea(new Dimension(0, 30)));
        cardLabel.add(btnExport);

        centerPanel.add(cardLabel);
        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    public void updateRoleUI() {
        if (SessionManager.isLoggedIn()) {
            lblUserRole.setText(SessionManager.getCurrentUser().getNama() + " (" + SessionManager.getCurrentUser().getRole() + ")");
            String role = SessionManager.getCurrentUser().getRole();
            
            // ROLE BASED ACCESS CONTROL (Kasir disembunyikan akses menunya!)
            if ("kasir".equalsIgnoreCase(role)) {
                if(btnKelolaMenu != null) btnKelolaMenu.setVisible(false);
                if(btnKelolaAkun != null) btnKelolaAkun.setVisible(false);
            } else {
                if(btnKelolaMenu != null) btnKelolaMenu.setVisible(true);
                if(btnKelolaAkun != null) btnKelolaAkun.setVisible(true);
            }
        }
        contentCardLayout.show(mainContentArea, "LIVE_ORDER"); 
        loadLiveOrders();
    }

    private void loadLiveOrders() {
        tModel.setRowCount(0);
        OrderDAO dao = new OrderDAO();
        List<Pesanan> orders = dao.getLiveOrders();
        for (Pesanan p : orders) {
            tModel.addRow(new Object[]{
                p.getIdPesanan(),
                p.getNamaPelanggan(),
                "Rp " + p.getTotalHarga(),
                p.getStatus()
            });
        }
    }
    
    private void loadMenuAdmin() {
        tMenuModel.setRowCount(0);
        MenuDAO mnDao = new MenuDAO();
        List<Menu> menus = mnDao.getAllMenus();
        for (Menu m : menus) {
            tMenuModel.addRow(new Object[]{
                m.getIdMenu(),
                m.getNamaMenu(),
                m.getKategori(),
                m.getHarga(),
                m.getStok()
            });
        }
    }
    
    private void loadAkunAdmin() {
        tAkunModel.setRowCount(0);
        UserDAO uDao = new UserDAO();
        List<Pegawai> staffs = uDao.getAllUsers();
        for (Pegawai p : staffs) {
            tAkunModel.addRow(new Object[]{
                p.getId(),
                p.getNama(),
                p.getUsername(),
                p.getRole().toUpperCase(),
                "***" // Hide pass logic if needed, but PRD standard is textual, let's just show it for debug
            });
        }
    }
    
    // OPERASIONAL AKUN
    private void addKasirAction() {
        JTextField idField = new JTextField();
        JTextField namaField = new JTextField();
        JTextField userField = new JTextField();
        JTextField passField = new JTextField();
        JComboBox<String> roleField = new JComboBox<>(new String[]{"admin", "kasir"});
        
        Object[] message = {
            "ID Pegawai Baru (Misal: USR-003):", idField,
            "Nama Lengkap:", namaField,
            "Username Akses:", userField,
            "Password Akses:", passField,
            "Pilih Hak Akses Portal (Role):", roleField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Pendaftaran Pegawai Baru", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            UserDAO dao = new UserDAO();
            boolean success = dao.addUser(idField.getText(), userField.getText(), passField.getText(), namaField.getText(), (String) roleField.getSelectedItem());
            if (success) { loadAkunAdmin(); JOptionPane.showMessageDialog(this, "Berhasil didaftarkan!"); }
            else { JOptionPane.showMessageDialog(this, "Gagal didaftarkan ke Database."); }
        }
    }
    
    private void deleteKasirAction() {
        int row = tableAkun.getSelectedRow();
        if(row < 0) {
             JOptionPane.showMessageDialog(this, "Pilih akun yang akan dihapus terlebih dahulu.");
             return;
        }
        String idUser = (String) tAkunModel.getValueAt(row, 0);
        if (idUser.equals("USR-001") || idUser.equals(SessionManager.getCurrentUser().getId())) {
             JOptionPane.showMessageDialog(this, "Waduh, Anda tidak dapat memblokir/menghapus Raja Admin (SuperUser) atau diri Anda sendiri!");
             return;
        }
        
        if (JOptionPane.showConfirmDialog(this, "Yakin hapus hak akses secara permanen?", "Konfirmasi Blokir", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
             new UserDAO().deleteUser(idUser);
             loadAkunAdmin();
        }
    }

    private void markAsDone() {
        int selectedRow = tableOrders.getSelectedRow();
        if (selectedRow >= 0) {
            String orderId = (String) tModel.getValueAt(selectedRow, 0);
            String status = (String) tModel.getValueAt(selectedRow, 3);
            
            if ("Selesai".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this, "Status antrean pesanan ini sudah berada di mode Selesai!", "Validasi Cerdas", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            int conf = JOptionPane.showConfirmDialog(this, "Tandai pesanan " + orderId + " sebagai rampung diproses?", "Operasional Kasir", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                new OrderDAO().updateStatus(orderId, "Selesai");
                loadLiveOrders();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih salah satu baris di tabel dulu, ya!");
        }
    }
    
    private void addStockAction() {
        int row = tableMenu.getSelectedRow();
        if(row < 0) {
             JOptionPane.showMessageDialog(this, "Pilih menu yang disuplai di tabel.");
             return;
        }
        String idMenu = (String) tMenuModel.getValueAt(row, 0);
        String nama = (String) tMenuModel.getValueAt(row, 1);
        
        String input = JOptionPane.showInputDialog(this, "Jumlah Inflow (Restok) hari ini untuk [" + nama + "]:");
        if(input != null && !input.isEmpty()) {
            try {
                int add = Integer.parseInt(input);
                if(new MenuDAO().addStok(idMenu, add)) {
                    loadMenuAdmin(); 
                }
            } catch (Exception x) {}
        }
    }
    
    private void updatePriceAction() {
        int row = tableMenu.getSelectedRow();
        if(row < 0) {
             JOptionPane.showMessageDialog(this, "Pilih menu yang akan diubah harganya.");
             return;
        }
        String idMenu = (String) tMenuModel.getValueAt(row, 0);
        String nama = (String) tMenuModel.getValueAt(row, 1);
        
        String input = JOptionPane.showInputDialog(this, "Harga Dasar TERBARU untuk [" + nama + "]:");
        if(input != null && !input.isEmpty()) {
            try {
                double harga = Double.parseDouble(input);
                if(new MenuDAO().updateHarga(idMenu, harga)) {
                    loadMenuAdmin(); 
                }
            } catch (Exception x) {}
        }
    }
}
