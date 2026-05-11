package com.lunacaffe.view;

import com.lunacaffe.dao.UserDAO;
import com.lunacaffe.model.Pegawai;
import com.lunacaffe.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout()); 
        
        
        setBackground(Color.decode("#F5F2EE")); 
        initUI();
    }

    private void initUI() {
        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setOpaque(false);

        JPanel cardLabel = new JPanel();
        cardLabel.setLayout(new BoxLayout(cardLabel, BoxLayout.Y_AXIS));
        cardLabel.setBackground(Color.WHITE);
        
        cardLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#DCD0C0"), 1, true),
            new EmptyBorder(40, 50, 40, 50)
        ));

        
        JLabel lblIcon = new JLabel("ðŸŒ™", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 45));
        lblIcon.setForeground(Color.decode("#F1C40F"));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIcon.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("Portal Pegawai");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(MainFrame.COLOR_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Silakan masukan kredensial sistem");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(Color.GRAY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel fieldsPanel = new JPanel(new GridLayout(4, 1, 0, 8));
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setMaximumSize(new Dimension(800, 150));
        
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(MainFrame.COLOR_PRIMARY);
        
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.putClientProperty("JTextField.placeholderText", "Contoh: admin / kasir1");
        txtUsername.putClientProperty("JComponent.roundRect", true); 
        
        JLabel lblPass = new JLabel("Kata Sandi");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(MainFrame.COLOR_PRIMARY);
        lblPass.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.putClientProperty("JComponent.roundRect", true);
        
        fieldsPanel.add(lblUser);
        fieldsPanel.add(txtUsername);
        fieldsPanel.add(lblPass);
        fieldsPanel.add(txtPassword);

        JButton btnLogin = new JButton("Masuk");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(MainFrame.COLOR_SECONDARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(800, 45));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.putClientProperty("JButton.buttonType", "roundRect");
        
        
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnLogin.setBackground(MainFrame.COLOR_PRIMARY); }
            @Override
            public void mouseExited(MouseEvent e) { btnLogin.setBackground(MainFrame.COLOR_SECONDARY); }
        });

        btnLogin.addActionListener(e -> attemptLogin());

        
        cardLabel.add(lblIcon);
        cardLabel.add(lblTitle);
        cardLabel.add(lblSubtitle);
        cardLabel.add(Box.createRigidArea(new Dimension(0, 30)));
        cardLabel.add(fieldsPanel);
        cardLabel.add(Box.createRigidArea(new Dimension(0, 30)));
        cardLabel.add(btnLogin);

        cardWrapper.add(cardLabel, BorderLayout.CENTER);
        
        
        JLabel lblHint = new JLabel("Versi 2.1 | Aman & Terenkripsi");
        lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHint.setForeground(Color.LIGHT_GRAY);
        lblHint.setHorizontalAlignment(SwingConstants.CENTER);
        lblHint.setBorder(new EmptyBorder(20, 0, 0, 0));
        cardWrapper.add(lblHint, BorderLayout.SOUTH);

        add(cardWrapper);
    }

    private void attemptLogin() {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());

        UserDAO dao = new UserDAO();
        Pegawai p = dao.getUserByUsername(user);
        
        if (p != null && p.verifikasiLogin(pass)) {
            SessionManager.setCurrentUser(p);
            
            txtUsername.setText("");
            txtPassword.setText("");
            mainFrame.showDashboard();
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
        }
    }
}
