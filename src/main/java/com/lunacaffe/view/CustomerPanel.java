package com.lunacaffe.view;

import com.lunacaffe.dao.MenuDAO;
import com.lunacaffe.dao.OrderDAO;
import com.lunacaffe.model.DetailPesanan;
import com.lunacaffe.model.Menu;
import com.lunacaffe.model.Pesanan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CustomerPanel extends JPanel {
    private static final DecimalFormat RUPIAH = new DecimalFormat("#,##0");
    private static final Map<String, BufferedImage> IMAGE_CACHE = new HashMap<>();

    private JPanel productsGrid;
    private JPanel cartItemsArea;
    private JLabel lblTotal;
    private JButton btnCheckout;
    private JButton btnSelesaiSesi;
    
    
    private Pesanan currentPesanan;
    private boolean isFinishedMode = false;
    private String activeCategoryFilter = "Semua"; 
    private String activeSearchQuery = "";
    private String activeSort = "Sortir: A-Z";
    
    
    private JPanel categoryPanel;

    public CustomerPanel() {
        setLayout(new BorderLayout(25, 25)); 
        setBackground(MainFrame.COLOR_BACKGROUND);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        initUI();
        initNewPesanan();
    }

    private void initNewPesanan() {
        isFinishedMode = false;
        currentPesanan = new Pesanan("TEMP", "Guest", "");
        
        if (btnCheckout != null && btnSelesaiSesi != null) {
            btnCheckout.setVisible(true);
            btnSelesaiSesi.setVisible(false);
        }
        refreshCartUI();
    }

    private void initUI() {
        JPanel catalogPanel = new JPanel(new BorderLayout(0, 15));
        catalogPanel.setBackground(MainFrame.COLOR_BACKGROUND);

        JPanel catalogHeader = new JPanel(new BorderLayout());
        catalogHeader.setBackground(MainFrame.COLOR_BACKGROUND);
        
        JLabel lblKatalog = new JLabel("Katalog Lunacaffe");
        lblKatalog.setFont(new Font("Segoe UI", Font.BOLD, 26)); 
        lblKatalog.setForeground(MainFrame.COLOR_PRIMARY);

        JPanel catalogTopArea = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        catalogTopArea.setBackground(MainFrame.COLOR_BACKGROUND);

        JTextField searchField = new JTextField(15);
        searchField.putClientProperty("JTextField.placeholderText", " Cari menu...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.putClientProperty("JComponent.roundRect", true);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                activeSearchQuery = searchField.getText().trim();
                productsGrid.removeAll();
                loadMenusFromDB();
                productsGrid.revalidate();
                productsGrid.repaint();
            }
        });

        String[] sortOptions = {"Sortir: A-Z", "Sortir: Harga Termurah", "Sortir: Harga Termahal"};
        JComboBox<String> cbSort = new JComboBox<>(sortOptions);
        cbSort.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbSort.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cbSort.addActionListener(e -> {
            activeSort = (String) cbSort.getSelectedItem();
            productsGrid.removeAll();
            loadMenusFromDB();
            productsGrid.revalidate();
            productsGrid.repaint();
        });

        catalogTopArea.add(searchField);
        catalogTopArea.add(cbSort);

        catalogHeader.add(lblKatalog, BorderLayout.WEST);
        catalogHeader.add(catalogTopArea, BorderLayout.EAST);

        
        categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        categoryPanel.setBackground(MainFrame.COLOR_BACKGROUND);
        String[] categories = {"Semua", "Hot Drink", "Cold Drink", "Coffee", "Non-Coffee", "Snack", "Bread"};
        renderCategoryButtons(categories); 

        JPanel topCatalogPanel = new JPanel(new BorderLayout(0, 15));
        topCatalogPanel.setBackground(MainFrame.COLOR_BACKGROUND);
        topCatalogPanel.add(catalogHeader, BorderLayout.NORTH);
        topCatalogPanel.add(categoryPanel, BorderLayout.CENTER);
        
        catalogPanel.add(topCatalogPanel, BorderLayout.NORTH);

        productsGrid = new JPanel(new GridLayout(0, 3, 20, 20)); 
        productsGrid.setBackground(MainFrame.COLOR_BACKGROUND);
        
        loadMenusFromDB();

        JScrollPane scrollPane = new JScrollPane(productsGrid);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        catalogPanel.add(scrollPane, BorderLayout.CENTER);

        
        JPanel cartPanel = new JPanel(new BorderLayout(0, 15));
        cartPanel.setPreferredSize(new Dimension(320, 0)); 
        cartPanel.setBackground(Color.WHITE);
        cartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E8DECB"), 1, true),
            new EmptyBorder(25, 20, 25, 20)
        ));

        JLabel lblKeranjang = new JLabel("<html><b style='font-size:18px;'>Keranjang</b><br><span style='font-size:11px; color:gray;'>Rincian pesanan pelanggan</span></html>");
        lblKeranjang.setForeground(MainFrame.COLOR_PRIMARY);
        cartPanel.add(lblKeranjang, BorderLayout.NORTH);

        cartItemsArea = new JPanel();
        cartItemsArea.setLayout(new BoxLayout(cartItemsArea, BoxLayout.Y_AXIS));
        cartItemsArea.setBackground(Color.WHITE);
        
        JScrollPane cartScroll = new JScrollPane(cartItemsArea);
        cartScroll.setBorder(null);
        cartPanel.add(cartScroll, BorderLayout.CENTER);

        JPanel cartBottom = new JPanel(new GridLayout(2, 1, 0, 15));
        cartBottom.setBackground(Color.WHITE);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.decode("#FEF8E7"));
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E8DECB"), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblTotalText = new JLabel("Total Harga:");
        lblTotalText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalText.setForeground(MainFrame.COLOR_PRIMARY);
        
        lblTotal = new JLabel("Rp 0", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(MainFrame.COLOR_PRIMARY);
        
        totalPanel.add(lblTotalText, BorderLayout.WEST);
        totalPanel.add(lblTotal, BorderLayout.EAST);

        JPanel actionsPanel = new JPanel(new CardLayout());
        
        btnCheckout = new JButton("Lanjutkan Pembayaran");
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckout.setBackground(MainFrame.COLOR_SECONDARY);
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setPreferredSize(new Dimension(0, 50));
        btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckout.putClientProperty("JButton.buttonType", "roundRect");
        btnCheckout.addActionListener(e -> processCheckout());

        btnSelesaiSesi = new JButton("Selesai & Sesi Baru");
        btnSelesaiSesi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSelesaiSesi.setBackground(MainFrame.COLOR_PRIMARY);
        btnSelesaiSesi.setForeground(Color.WHITE);
        btnSelesaiSesi.setPreferredSize(new Dimension(0, 50));
        btnSelesaiSesi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSelesaiSesi.putClientProperty("JButton.buttonType", "roundRect");
        btnSelesaiSesi.addActionListener(e -> initNewPesanan());

        actionsPanel.add(btnCheckout, "CHECKOUT");
        actionsPanel.add(btnSelesaiSesi, "SELESAI");
        ((CardLayout) actionsPanel.getLayout()).show(actionsPanel, "CHECKOUT");

        cartBottom.add(totalPanel);
        cartBottom.add(actionsPanel);

        cartPanel.add(cartBottom, BorderLayout.SOUTH);

        add(catalogPanel, BorderLayout.CENTER);
        add(cartPanel, BorderLayout.EAST);
    }
    
    private void renderCategoryButtons(String[] categories) {
        categoryPanel.removeAll();
        for (String catName : categories) {
            JButton btnCat = new JButton(catName);
            btnCat.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnCat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnCat.putClientProperty("JButton.buttonType", "roundRect");
            
            if (catName.equals(activeCategoryFilter)) {
                btnCat.setBackground(MainFrame.COLOR_PRIMARY);
                btnCat.setForeground(Color.WHITE);
            } else {
                btnCat.setBackground(Color.decode("#EAE5E1")); 
                btnCat.setForeground(MainFrame.COLOR_PRIMARY);
            }
            
            btnCat.addActionListener(e -> {
                activeCategoryFilter = catName;
                renderCategoryButtons(categories); 
                
                productsGrid.removeAll();
                loadMenusFromDB();
                productsGrid.revalidate();
                productsGrid.repaint();
            });
            categoryPanel.add(btnCat);
        }
        categoryPanel.revalidate();
        categoryPanel.repaint();
    }

    private void loadMenusFromDB() {
        MenuDAO mnDao = new MenuDAO();
        List<Menu> menus = mnDao.getAllMenus();
        
        
        if (activeSort.contains("Termurah")) {
            menus.sort((a,b) -> Double.compare(a.getHarga(), b.getHarga()));
        } else if (activeSort.contains("Termahal")) {
            menus.sort((a,b) -> Double.compare(b.getHarga(), a.getHarga()));
        } else {
            menus.sort((a,b) -> a.getNamaMenu().compareToIgnoreCase(b.getNamaMenu()));
        }
        
        boolean isEmpty = true;
        for (Menu m : menus) {
            
            if (!activeCategoryFilter.equals("Semua") && !m.getKategori().equalsIgnoreCase(activeCategoryFilter)) {
                continue; 
            }
            
            
            if (!activeSearchQuery.isEmpty() && !m.getNamaMenu().toLowerCase().contains(activeSearchQuery.toLowerCase())) {
                continue;
            }
            
            isEmpty = false;
            String badgeStr = m.isNew() ? "NEW" : (m.isBestseller() ? "BEST" : "");
            productsGrid.add(createProductCard(badgeStr, m));
        }
        
        
        if (isEmpty) {
            JLabel emptyCat = new JLabel("<html><center>Menu belum tersedia untuk filter ini.</center></html>");
            emptyCat.setForeground(Color.GRAY);
            emptyCat.setHorizontalAlignment(SwingConstants.CENTER);
            productsGrid.add(emptyCat);
        }
    }

    private JPanel createProductCard(String badge, Menu menu) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#EAE5E1"), 1, true),
            new EmptyBorder(12, 12, 12, 12)
        ));

        
        JPanel imgContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                String category = menu.getKategori();
                Color c1, c2;
                if (category.toLowerCase().contains("coffee") || category.toLowerCase().contains("hot")) {
                    c1 = Color.decode("#EAD6C0"); c2 = Color.decode("#FFF0E0");
                } else if (category.toLowerCase().contains("cold")) {
                    c1 = Color.decode("#C0DEEA"); c2 = Color.decode("#E0F4FF");
                } else if (category.toLowerCase().contains("bread") || category.toLowerCase().contains("snack")) {
                    c1 = Color.decode("#EADEC0"); c2 = Color.decode("#FFF6E0");
                } else {
                    c1 = Color.decode("#D3EAC0"); c2 = Color.decode("#E9FFE0");
                }
                
                GradientPaint gp = new GradientPaint(0, 0, c2, getWidth(), getHeight(), c1);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                BufferedImage productImage = loadMenuImage(menu.getImagePath());
                if (productImage != null) {
                    drawCoverImage(g2, productImage, getWidth(), getHeight(), 16);
                    g2.dispose();
                    return;
                }

                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48)); 
                FontMetrics fm = g2.getFontMetrics();
                String iconEmoji = menu.getImagePath();
                String textToDraw = "LC";
                
                int textX = (getWidth() - fm.stringWidth(textToDraw)) / 2;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent() + 5;
                
                g2.drawString(textToDraw, textX, textY);
                g2.dispose();
            }
        };
        imgContainer.setPreferredSize(new Dimension(150, 130)); 
        imgContainer.setOpaque(false);

        if (!badge.isEmpty()) {
            JLabel lblBadge = new JLabel(" " + badge + " ");
            lblBadge.setOpaque(true);
            lblBadge.setBackground(badge.equals("NEW") ? Color.decode("#FFD700") : Color.decode("#FF6B6B"));
            lblBadge.setForeground(badge.equals("NEW") ? Color.BLACK : Color.WHITE);
            lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            
            lblBadge.setBorder(BorderFactory.createMatteBorder(0,0,2,0, badge.equals("NEW") ? Color.decode("#D4AE00") : Color.decode("#C0392B")));
            
            JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            badgePanel.setOpaque(false);
            badgePanel.add(lblBadge);
            imgContainer.add(badgePanel, BorderLayout.NORTH);
        }

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(12, 0, 12, 0));
        
        JLabel lblTitle = new JLabel("<html><body style='width: 130px'>" + menu.getNamaMenu() + "</body></html>");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(MainFrame.COLOR_PRIMARY);

        JLabel lblPrice = new JLabel(formatRupiah(menu.getHarga()) + " | Stok: " + menu.getStok());
        lblPrice.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPrice.setForeground(Color.decode("#888888"));

        infoPanel.add(lblTitle);
        infoPanel.add(lblPrice);

        JButton btnAdd = new JButton("Tambah");
        btnAdd.setBackground(Color.decode("#3F1F4E"));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.putClientProperty("JButton.buttonType", "roundRect");
        
        
        btnAdd.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnAdd.setBackground(MainFrame.COLOR_PRIMARY); }
            public void mouseExited(MouseEvent e) { btnAdd.setBackground(Color.decode("#3F1F4E")); }
        });
        
        btnAdd.addActionListener(e -> addItemToCart(menu));

        card.add(imgContainer, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.SOUTH);

        return card;
    }

    private BufferedImage loadMenuImage(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }

        String key = imagePath.trim();
        if (IMAGE_CACHE.containsKey(key)) {
            return IMAGE_CACHE.get(key);
        }

        BufferedImage image = null;
        try {
            if (key.startsWith("/")) {
                URL resource = CustomerPanel.class.getResource(key);
                if (resource != null) {
                    image = ImageIO.read(resource);
                }
            } else {
                File file = new File(key);
                if (file.exists()) {
                    image = ImageIO.read(file);
                }
            }
        } catch (Exception ignored) {
            image = null;
        }

        IMAGE_CACHE.put(key, image);
        return image;
    }

    private void drawCoverImage(Graphics2D g2, BufferedImage image, int width, int height, int arc) {
        Shape oldClip = g2.getClip();
        g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, width, height, arc, arc));

        double scale = Math.max(width / (double) image.getWidth(), height / (double) image.getHeight());
        int scaledWidth = (int) Math.ceil(image.getWidth() * scale);
        int scaledHeight = (int) Math.ceil(image.getHeight() * scale);
        int x = (width - scaledWidth) / 2;
        int y = (height - scaledHeight) / 2;

        g2.drawImage(image, x, y, scaledWidth, scaledHeight, null);
        g2.setClip(oldClip);
    }

    private String formatRupiah(double value) {
        return "Rp " + RUPIAH.format(value).replace(",", ".");
    }

    private JButton createSmallCartButton(String text) {
        JButton button = new JButton(text);
        button.setMargin(new Insets(2, 7, 2, 7));
        button.setBackground(Color.decode("#F6F2EC"));
        button.setForeground(MainFrame.COLOR_PRIMARY);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void addItemToCart(Menu menu) {
        if (isFinishedMode) return; 
        
        if (menu.getStok() <= 0) {
            JOptionPane.showMessageDialog(this, "Waduh, stok produk ini habis!", "Maaf", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DetailPesanan exists = currentPesanan.getItems().stream()
                .filter(d -> d.getMenu().getIdMenu().equals(menu.getIdMenu()))
                .findFirst().orElse(null);

        if (exists != null) {
            if (exists.getQty() >= menu.getStok()) {
                JOptionPane.showMessageDialog(this, "Anda memesan seluruh sisa stok tersedia!", "Stok Terbatas", JOptionPane.WARNING_MESSAGE);
                return;
            }
            exists.setQty(exists.getQty() + 1);
        } else {
            currentPesanan.addItem(new DetailPesanan(menu, 1));
        }
        
        currentPesanan.setTotalHarga(currentPesanan.hitungTotal());
        refreshCartUI();
    }
    
    private void refreshCartUI() {
        cartItemsArea.removeAll();
        if (currentPesanan.getItems().isEmpty()) {
            JLabel lblKosong = new JLabel("<html><center><span style='color:#A0A0A0;'>Keranjang masih kosong</span></center></html>", SwingConstants.CENTER);
            lblKosong.setAlignmentX(Component.CENTER_ALIGNMENT);
            cartItemsArea.setLayout(new GridBagLayout()); 
            cartItemsArea.add(lblKosong);
            
            if (btnCheckout != null && btnSelesaiSesi != null) {
                btnCheckout.getParent().invalidate();
                ((CardLayout) btnCheckout.getParent().getLayout()).show(btnCheckout.getParent(), "CHECKOUT");
            }
        } else {
            cartItemsArea.setLayout(new BoxLayout(cartItemsArea, BoxLayout.Y_AXIS));
            for (DetailPesanan item : currentPesanan.getItems()) {
                JPanel row = new JPanel(new BorderLayout());
                row.setOpaque(false);
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#F1EFE7")),
                    new EmptyBorder(10, 5, 10, 5)
                ));
                row.setMaximumSize(new Dimension(300, 70));

                JLabel lblName = new JLabel("<html><b style='font-size:13px;'>" + item.getMenu().getNamaMenu() + "</b><br><span style='color:#E67E22; font-weight:bold;'>x" + item.getQty() + "</span> &nbsp;<span style='color:gray;'>" + formatRupiah(item.getSubtotal()) + "</span></html>");
                row.add(lblName, BorderLayout.CENTER);

                if (!isFinishedMode) {
                    JButton btnMinus = createSmallCartButton("-");
                    JButton btnPlus = createSmallCartButton("+");
                    JButton btnDel = createSmallCartButton("x");
                    btnDel.setBackground(Color.decode("#FFF0F0"));
                    btnDel.setForeground(Color.decode("#D32F2F"));

                    btnMinus.addActionListener(e -> {
                        if (item.getQty() <= 1) {
                            currentPesanan.getItems().remove(item);
                        } else {
                            item.setQty(item.getQty() - 1);
                        }
                        currentPesanan.setTotalHarga(currentPesanan.hitungTotal());
                        refreshCartUI();
                    });

                    btnPlus.addActionListener(e -> {
                        if (item.getQty() >= item.getMenu().getStok()) {
                            JOptionPane.showMessageDialog(this, "Stok produk tidak mencukupi.", "Stok Terbatas", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        item.setQty(item.getQty() + 1);
                        currentPesanan.setTotalHarga(currentPesanan.hitungTotal());
                        refreshCartUI();
                    });

                    btnDel.addActionListener(e -> {
                        currentPesanan.getItems().remove(item);
                        currentPesanan.setTotalHarga(currentPesanan.hitungTotal());
                        refreshCartUI();
                    });
                    
                    JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
                    btnWrapper.setOpaque(false);
                    btnWrapper.add(btnMinus);
                    btnWrapper.add(btnPlus);
                    btnWrapper.add(btnDel);
                    row.add(btnWrapper, BorderLayout.EAST);
                }
                cartItemsArea.add(row);
            }
        }
        
        lblTotal.setText(formatRupiah(currentPesanan.getTotalHarga()));
        cartItemsArea.revalidate();
        cartItemsArea.repaint();
    }
    
    private void processCheckout() {
        if (currentPesanan.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih minimal 1 produk terlebih dahulu!", "Keranjang Kosong", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String namaPelanggan = JOptionPane.showInputDialog(this, "Masukkan Nama Pemesan:", "Informasi Checkout", JOptionPane.QUESTION_MESSAGE);
        
        if (namaPelanggan != null && !namaPelanggan.trim().isEmpty()) {
            currentPesanan.setNamaPelanggan(namaPelanggan.trim().toUpperCase());
            
            OrderDAO orderDAO = new OrderDAO();
            String realId = orderDAO.getNextOrderNumber();
            currentPesanan.setIdPesanan(realId);
            
            boolean success = orderDAO.createOrder(currentPesanan);
            
            if (success) {
                showReceiptDialog(currentPesanan);
                
                productsGrid.removeAll();
                loadMenusFromDB();
                productsGrid.revalidate();
                productsGrid.repaint();
                
                isFinishedMode = true;
                refreshCartUI();
                
                ((CardLayout) btnCheckout.getParent().getLayout()).show(btnCheckout.getParent(), "SELESAI");
            } else {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan sistem saat memproses transaksi.", "Gagal Checkout", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showReceiptDialog(Pesanan p) {
        JDialog receiptDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Cetak Struk", true);
        receiptDialog.setSize(380, 560); 
        receiptDialog.setLocationRelativeTo(this);
        receiptDialog.getContentPane().setBackground(Color.decode("#FFFAFA"));
        
        JTextPane txtReceipt = new JTextPane();
        txtReceipt.setContentType("text/html");
        txtReceipt.setEditable(false);
        txtReceipt.setBackground(Color.decode("#FFFAFA"));
        
        String fullId = p.getIdPesanan();
        String noPesanan = fullId.contains("-") ? fullId.substring(fullId.lastIndexOf('-') + 1) : fullId;
        
        StringBuilder b = new StringBuilder();
        b.append("<div style='font-family: \"Courier New\", monospace; padding: 10px 15px;'>");
        b.append("<h1 style='text-align:center; color:#2A113A; margin:0; font-size:26px;'>LUNACAFFE</h1>");
        b.append("<p style='text-align:center; margin:0 0 10px 0; color:#555;'>Self-Service Kiosk Receipt</p>");
        b.append("<hr style='border:1px dashed #AAA'>");
        
        b.append("<h2 style='text-align:center; margin:10px 0 2px 0; font-size:22px;'>NO ANTRIAN : ").append(noPesanan).append("</h2>");
        b.append("<p style='text-align:center; margin:0 0 10px 0; font-size:10px; color:#A0A0A0;'>REF: ").append(fullId).append("</p>");
        b.append("<hr style='border:1px dashed #AAA'>");
        
        b.append("<p style='font-size:11px; margin:5px 0;'>");
        b.append("<b>TANGGAL :</b> ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())).append("<br>");
        b.append("<b>NAMA    :</b> ").append(p.getNamaPelanggan());
        b.append("</p>");
        b.append("<hr style='border:1px solid #777'>");
        
        b.append("<table width='100%' style='font-size:12px; margin: 5px 0;'>");
        for (DetailPesanan item : p.getItems()) {
            b.append("<tr>");
            b.append("<td width='60%'>").append(item.getMenu().getNamaMenu()).append("</td>");
            b.append("<td width='10%'>x").append(item.getQty()).append("</td>");
            b.append("<td width='30%' align='right'>").append(formatRupiah(item.getSubtotal())).append("</td>");
            b.append("</tr>");
        }
        b.append("</table>");
        
        b.append("<hr style='border:1px solid #777'>");
        b.append("<table width='100%' style='font-size:14px; margin-top:5px;'>");
        b.append("<tr><td><b>TOTAL BAYAR</b></td><td align='right'><b>").append(formatRupiah(p.getTotalHarga())).append("</b></td></tr>");
        b.append("</table>");
        
        b.append("<p style='text-align:center; margin-top:25px; font-size:12px;'>Silakan lakukan pembayaran di kasir.</p>");
        b.append("<p style='text-align:center; font-size:11px;'>Mohon lakukan pembayaran di Kasir<br>Trims atas kunjungannya, Moonchild!</p>");
        b.append("</div>");
        
        txtReceipt.setText(b.toString());
        
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton btnCetak = new JButton("Bukti Cetak");
        btnCetak.setBackground(Color.decode("#EAE5E1"));
        btnCetak.setForeground(MainFrame.COLOR_PRIMARY);
        btnCetak.setFocusPainted(false);
        btnCetak.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCetak.addActionListener(e -> JOptionPane.showMessageDialog(receiptDialog, "Simulasi Printer...\nInformasi dicetak ke perangkat POS kasir belakang.", "Cetak Sinkronisasi", JOptionPane.INFORMATION_MESSAGE));
        
        JButton btnTutup = new JButton("Menu Utama");
        btnTutup.setBackground(MainFrame.COLOR_SECONDARY);
        btnTutup.setForeground(Color.WHITE);
        btnTutup.setFocusPainted(false);
        btnTutup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTutup.addActionListener(e -> receiptDialog.dispose());
        
        btnPanel.add(btnCetak);
        btnPanel.add(btnTutup);
        
        receiptDialog.setLayout(new BorderLayout());
        receiptDialog.add(new JScrollPane(txtReceipt), BorderLayout.CENTER);
        receiptDialog.add(btnPanel, BorderLayout.SOUTH);
        receiptDialog.setVisible(true);
    }
}
