package com.washywash.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.washywash.model.User;
import com.washywash.repository.UserRepository;
import com.washywash.repository.impl.UserRepositoryImpl;
import com.washywash.service.UserService;

import java.awt.*;
import java.util.List;

public class FormUser extends JPanel {
    private JTextField txtKodeUser;
    private JTextField txtNamaUser;
    private JTextField txtNoHp;
    private JTextField txtEmail;
    private JPasswordField txtPassword;

    private JButton btnSimpan;
    private JButton btnUpdate;
    private JButton btnHapus;
    private JButton btnCari;
    private JButton btnReset;

    private JTable tableUser;
    private DefaultTableModel tableModel;

    private final UserService userService;

    public FormUser() {
        UserRepository userRepository = new UserRepositoryImpl();
        this.userService = new UserService(userRepository);

        initComponents();
        loadTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel panelUtama = new JPanel(new BorderLayout(10, 10));
        panelUtama.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Kode
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1;
        panelForm.add(new JLabel("Kode User"), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        txtKodeUser = new JTextField();
        panelForm.add(txtKodeUser, gbc);

        // Nama
        gbc.gridx = 0; gbc.gridy = 1; 
        panelForm.add(new JLabel("Nama User"), gbc);

        gbc.gridx = 1; 
        txtNamaUser = new JTextField();
        panelForm.add(txtNamaUser, gbc);

        // No HP
        gbc.gridx = 0; gbc.gridy = 2; 
        panelForm.add(new JLabel("No HP"), gbc);

        gbc.gridx = 1; 
        txtNoHp = new JTextField();
        panelForm.add(txtNoHp, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 3; 
        panelForm.add(new JLabel("Email"), gbc);

        gbc.gridx = 1; 
        txtEmail = new JTextField();
        panelForm.add(txtEmail, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 4; 
        panelForm.add(new JLabel("Password"), gbc);

        gbc.gridx = 1; 
        txtPassword = new JPasswordField();
        panelForm.add(txtPassword, gbc);

        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnSimpan = new JButton("Simpan");
        btnUpdate = new JButton("Update");
        btnHapus = new JButton("Hapus");
        btnCari = new JButton("Cari");
        btnReset = new JButton("Reset");

        panelButton.add(btnSimpan);
        panelButton.add(btnUpdate);
        panelButton.add(btnHapus);
        panelButton.add(btnCari);
        panelButton.add(btnReset);

        // Aksi
        gbc.gridx = 0; gbc.gridy = 5;
        panelForm.add(new JLabel("Aksi"), gbc);

        gbc.gridx = 1; 
        panelForm.add(panelButton, gbc);

        String[] columnNames = {
            "Kode User", 
            "Nama User", 
            "No HP", 
            "Email"
        };

        tableModel = new DefaultTableModel(columnNames, 0);
        tableUser = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableUser);

        panelUtama.add(panelForm, BorderLayout.NORTH);
        panelUtama.add(scrollPane, BorderLayout.CENTER);

        add(panelUtama);

        btnSimpan.addActionListener(e -> simpanUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnHapus.addActionListener(e -> hapusUser());
        btnCari.addActionListener(e -> cariUser());
        btnReset.addActionListener(e -> resetForm());

        tableUser.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableUser.getSelectedRow() != -1) {
                isiFormDariTable();
            }
        });
    }

    private void simpanUser() {
        try {
            User user = getUserFromForm(true);
            userService.tambahUser(user);
            JOptionPane.showMessageDialog(this, "Data user berhasil disimpan.");
            loadTable();
            resetForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUser() {
        try {
            User user = getUserFromForm(false);
            userService.updateUser(user);
            JOptionPane.showMessageDialog(this, "Data user berhasil diupdate.");
            loadTable();
            resetForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusUser() {
        try {
            String kodeUser = txtKodeUser.getText();

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus data ini?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                userService.hapusUser(kodeUser);
                JOptionPane.showMessageDialog(this, "Data user berhasil dihapus.");
                loadTable();
                resetForm();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cariUser() {
        try {
            String kodeUser = txtKodeUser.getText();
            User user = userService.cariUser(kodeUser);

            if (user == null) {
                JOptionPane.showMessageDialog(this, "User tidak ditemukan.");
                return;
            }

            txtNamaUser.setText(user.getNamaUser());
            txtNoHp.setText(user.getNoHp());
            txtEmail.setText(user.getEmail());
            txtPassword.setText(""); // penting!

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private User getUserFromForm(boolean isCreate) {
        String kode = txtKodeUser.getText().trim();
        String nama = txtNamaUser.getText().trim();
        String no = txtNoHp.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        // VALIDASI WAJIB
        if (kode.isBlank()) throw new IllegalArgumentException("Kode user wajib diisi.");
        if (nama.isBlank()) throw new IllegalArgumentException("Nama user wajib diisi.");
        if (no.isBlank()) throw new IllegalArgumentException("No HP wajib diisi.");
        if (email.isBlank()) throw new IllegalArgumentException("Email wajib diisi.");

        if (isCreate && password.isBlank()) {
            throw new IllegalArgumentException("Password wajib diisi.");
        }

        User user = new User(kode, nama, no, email);

        if (!password.isBlank()) {
            user.setPassword(password);
        }

        return user;
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<User> listUser = userService.getSemuaUser();

        for (User user : listUser) {
            Object[] row = {
                user.getKodeUser(),
                user.getNamaUser(),
                user.getNoHp(),
                user.getEmail()
            };
            tableModel.addRow(row);
        }
    }

    private void resetForm() {
        txtKodeUser.setText("");
        txtNamaUser.setText("");
        txtNoHp.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtKodeUser.requestFocus();
        tableUser.clearSelection();
    }

    private void isiFormDariTable() {
        int row = tableUser.getSelectedRow();

        txtKodeUser.setText(tableModel.getValueAt(row, 0).toString());
        txtNamaUser.setText(tableModel.getValueAt(row, 1).toString());
        txtNoHp.setText(tableModel.getValueAt(row, 2).toString());
        txtEmail.setText(tableModel.getValueAt(row, 3).toString());
        txtPassword.setText(""); 
    }
}