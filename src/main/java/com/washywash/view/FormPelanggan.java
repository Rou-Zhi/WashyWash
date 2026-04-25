package com.washywash.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.washywash.model.Pelanggan;
import com.washywash.repository.PelangganRepository;
import com.washywash.repository.impl.PelangganRepositoryImpl;
import com.washywash.service.PelangganService;

import java.awt.*;
import java.util.List;

public class FormPelanggan extends JPanel {
    private JTextField txtKodePelanggan;
    private JTextField txtNamaPelanggan;
    private JTextField txtNoHp;
    private JTextField txtEmail;

    private JButton btnSimpan;
    private JButton btnUpdate;
    private JButton btnHapus;
    private JButton btnCari;
    private JButton btnReset;

    private JTable tablePelanggan;
    private DefaultTableModel tableModel;

    private final PelangganService pelangganService;

    public FormPelanggan() {
        PelangganRepository pelangganRepository = new PelangganRepositoryImpl();
        this.pelangganService = new PelangganService(pelangganRepository);

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
        panelForm.add(new JLabel("Kode Pelanggan"), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        txtKodePelanggan = new JTextField();
        panelForm.add(txtKodePelanggan, gbc);

        // Nama
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Nama Pelanggan"), gbc);

        gbc.gridx = 1; 
        txtNamaPelanggan = new JTextField();
        panelForm.add(txtNamaPelanggan, gbc);

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
        gbc.gridx = 0; gbc.gridy = 4;
        panelForm.add(new JLabel("Aksi"), gbc);

        gbc.gridx = 1;
        panelForm.add(panelButton, gbc);

        String[] columnNames = {
            "Kode Pelanggan", 
            "Nama Pelanggan", 
            "No HP", 
            "Email"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0);
        tablePelanggan = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tablePelanggan);

        panelUtama.add(panelForm, BorderLayout.NORTH);
        panelUtama.add(scrollPane, BorderLayout.CENTER);

        add(panelUtama);

        btnSimpan.addActionListener(e -> simpanPelanggan());
        btnUpdate.addActionListener(e -> updatePelanggan());
        btnHapus.addActionListener(e -> hapusPelanggan());
        btnCari.addActionListener(e -> cariPelanggan());
        btnReset.addActionListener(e -> resetForm());

        tablePelanggan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablePelanggan.getSelectedRow() != -1) {
                isiFormDariTable();
            }
        });
    }

    private void simpanPelanggan() {
        try {
            Pelanggan pelanggan = getPelangganFromForm();
            pelangganService.tambahPelanggan(pelanggan);
            JOptionPane.showMessageDialog(this, "Data pelanggan berhasil disimpan.");
            loadTable();
            resetForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePelanggan() {
        try {
            Pelanggan pelanggan = getPelangganFromForm();
            pelangganService.updatePelanggan(pelanggan);
            JOptionPane.showMessageDialog(this, "Data pelanggan berhasil diupdate.");
            loadTable();
            resetForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusPelanggan() {
        try {
            String kodePelanggan = txtKodePelanggan.getText();
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus data ini?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                pelangganService.hapusPelanggan(kodePelanggan);
                JOptionPane.showMessageDialog(this, "Data pelanggan berhasil dihapus.");
                loadTable();
                resetForm();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cariPelanggan() {
        try {
            String kodePelanggan = txtKodePelanggan.getText();
            Pelanggan pelanggan = pelangganService.cariPelanggan(kodePelanggan);

            if (pelanggan == null) {
                JOptionPane.showMessageDialog(this, "Pelanggan tidak ditemukan.");
                return;
            }

            txtNamaPelanggan.setText(pelanggan.getNamaPelanggan());
            txtNoHp.setText(pelanggan.getNoHp());
            txtEmail.setText(pelanggan.getEmail());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Pelanggan getPelangganFromForm() {
        String kode = txtKodePelanggan.getText().trim();
        String nama = txtNamaPelanggan.getText().trim();
        String no = txtNoHp.getText().trim();
        String email = txtEmail.getText().trim();

        return new Pelanggan(kode, nama, no, email);
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<Pelanggan> listPelanggan = pelangganService.getSemuaPelanggan();

        for (Pelanggan pelanggan : listPelanggan) {
            Object[] row = {
            pelanggan.getKodePelanggan(),
            pelanggan.getNamaPelanggan(),
            pelanggan.getNoHp(),
            pelanggan.getEmail()
            };
            tableModel.addRow(row);
        }
    }

    private void resetForm() {
        txtKodePelanggan.setText("");
        txtNamaPelanggan.setText("");
        txtNoHp.setText("");
        txtEmail.setText("");
        txtKodePelanggan.requestFocus();
        tablePelanggan.clearSelection();
    }

    private void isiFormDariTable() {
        int row = tablePelanggan.getSelectedRow();

        txtKodePelanggan.setText(tableModel.getValueAt(row, 0).toString());
        txtNamaPelanggan.setText(tableModel.getValueAt(row, 1).toString());
        txtNoHp.setText(tableModel.getValueAt(row, 2).toString());
        txtEmail.setText(tableModel.getValueAt(row, 3).toString());
    }
}
