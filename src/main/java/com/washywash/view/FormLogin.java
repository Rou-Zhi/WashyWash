package com.washywash.view;

import javax.swing.*;
import java.awt.*;

import com.washywash.model.User;
import com.washywash.repository.UserRepository;
import com.washywash.repository.impl.UserRepositoryImpl;

import org.mindrot.jbcrypt.BCrypt;

public class FormLogin extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    private final UserRepository userRepository;

    public FormLogin() {
        userRepository = new UserRepositoryImpl();

        setTitle("Login WashyWash");
        setSize(300, 200);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1;
        panelForm.add(new JLabel("Email / Kode User"), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        txtEmail = new JTextField();
        panelForm.add(txtEmail, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Password"), gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField();
        panelForm.add(txtPassword, gbc);

        // Button
        gbc.gridx = 1; gbc.gridy = 2;
        btnLogin = new JButton("Login");
        panelForm.add(btnLogin, gbc);

        add(panelForm);

        btnLogin.addActionListener(e -> login());
    }

    private void login() {
        String input = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (input.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi");
            return;
        }

        try {
            User user = userRepository.findByEmailOrKode(input);

            if (user == null) {
                JOptionPane.showMessageDialog(this, "User tidak ditemukan");
                return;
            }

            if (BCrypt.checkpw(password, user.getPassword())) {
                JOptionPane.showMessageDialog(this, "Login berhasil");

                new MainMenu().setVisible(true);
                dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Password salah");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}