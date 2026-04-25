package com.washywash.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.washywash.model.Barang;
import com.washywash.repository.BarangRepository;
import com.washywash.repository.impl.BarangRepositoryImpl;
import com.washywash.service.BarangService;

import java.awt.*;
import java.util.List;

public class FormBarang extends JPanel {
    private JTextField txtKodeBarang;
    private JTextField txtNamaBarang;
    private JComboBox<String> cmbJenisBarang;
    private JComboBox<String> cmbSatuan;
    private JTextField txtHarga;
    private JTextField txtStok;

    private JButton btnSimpan;
    private JButton btnUpdate;
    private JButton btnHapus;
    private JButton btnCari;
    private JButton btnReset;

    private JTable tableBarang;
    private DefaultTableModel tableModel;

    private final BarangService barangService;

    public FormBarang() {
        BarangRepository barangRepository = new BarangRepositoryImpl();
        this.barangService = new BarangService(barangRepository);

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
        panelForm.add(new JLabel("Kode Barang"), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        txtKodeBarang = new JTextField();
        panelForm.add(txtKodeBarang, gbc);

        // Nama
        gbc.gridx = 0; gbc.gridy = 1; 
        panelForm.add(new JLabel("Nama Barang"), gbc);

        gbc.gridx = 1; 
        txtNamaBarang = new JTextField();
        panelForm.add(txtNamaBarang, gbc);

        // Jenis
        gbc.gridx = 0; gbc.gridy = 2; 
        panelForm.add(new JLabel("Jenis Barang"), gbc);

        gbc.gridx = 1; 
        cmbJenisBarang = new JComboBox<>(new String[]{"Jasa", "Barang"});
        panelForm.add(cmbJenisBarang, gbc);

        // Satuan
        gbc.gridx = 0; gbc.gridy = 3; 
        panelForm.add(new JLabel("Satuan"), gbc);

        gbc.gridx = 1; 
        cmbSatuan = new JComboBox<>(new String[]{"KG", "PCS", "SET", "UNIT"});
        panelForm.add(cmbSatuan, gbc);

        // Harga
        gbc.gridx = 0; gbc.gridy = 4; 
        panelForm.add(new JLabel("Harga"), gbc);

        gbc.gridx = 1; 
        txtHarga = new JTextField();
        panelForm.add(txtHarga, gbc);

        // Stok
        gbc.gridx = 0; gbc.gridy = 5; 
        panelForm.add(new JLabel("Stok"), gbc);

        gbc.gridx = 1; 
        txtStok = new JTextField();
        panelForm.add(txtStok, gbc);

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
        gbc.gridx = 0; gbc.gridy = 6; 
        panelForm.add(new JLabel("Aksi"), gbc);

        gbc.gridx = 1;
        panelForm.add(panelButton, gbc);

        String[] columnNames = {"Kode Barang", "Nama Barang", "Jenis Barang", "Satuan", "Harga", "Stok"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tableBarang = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableBarang);

        panelUtama.add(panelForm, BorderLayout.NORTH);
        panelUtama.add(scrollPane, BorderLayout.CENTER);

        add(panelUtama);

        btnSimpan.addActionListener(e -> simpanBarang());
        btnUpdate.addActionListener(e -> updateBarang());
        btnHapus.addActionListener(e -> hapusBarang());
        btnCari.addActionListener(e -> cariBarang());
        btnReset.addActionListener(e -> resetForm());

        tableBarang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableBarang.getSelectedRow() != -1) {
                isiFormDariTable();
            }
        });
    }

    private void simpanBarang() {
        try {
            Barang barang = getBarangFromForm();
            barangService.tambahBarang(barang);
            JOptionPane.showMessageDialog(this, "Data barang berhasil disimpan.");
            loadTable();
            resetForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBarang() {
        try {
            Barang barang = getBarangFromForm();
            barangService.updateBarang(barang);
            JOptionPane.showMessageDialog(this, "Data barang berhasil diupdate.");
            loadTable();
            resetForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusBarang() {
        try {
            String kodeBarang = txtKodeBarang.getText();
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus data ini?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                barangService.hapusBarang(kodeBarang);
                JOptionPane.showMessageDialog(this, "Data barang berhasil dihapus.");
                loadTable();
                resetForm();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cariBarang() {
        try {
            String kodeBarang = txtKodeBarang.getText();
            Barang barang = barangService.cariBarang(kodeBarang);

            if (barang == null) {
                JOptionPane.showMessageDialog(this, "Barang tidak ditemukan.");
                return;
            }

            txtNamaBarang.setText(barang.getNamaBarang());
            cmbJenisBarang.setSelectedItem(barang.getJenisBarang());
            cmbSatuan.setSelectedItem(barang.getSatuan());
            txtHarga.setText(String.valueOf(barang.getHarga()));
            txtStok.setText(String.valueOf(barang.getStok()));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Barang getBarangFromForm() {
        String kode = txtKodeBarang.getText().trim();
        String nama = txtNamaBarang.getText().trim();
        String jenis = cmbJenisBarang.getSelectedItem().toString();
        String satuan = cmbSatuan.getSelectedItem().toString();
        double harga = Double.parseDouble(txtHarga.getText().trim());
        int stok = Integer.parseInt(txtStok.getText().trim());

        return new Barang(kode, nama, jenis, satuan, harga, stok);
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<Barang> listBarang = barangService.getSemuaBarang();

        for (Barang barang : listBarang) {
            Object[] row = {
            barang.getKodeBarang(),
            barang.getNamaBarang(),
            barang.getJenisBarang(),
            barang.getSatuan(),
            barang.getHarga(),
            barang.getStok()
            };
            tableModel.addRow(row);
        }
    }

    private void resetForm() {
        txtKodeBarang.setText("");
        txtNamaBarang.setText("");
        cmbJenisBarang.setSelectedIndex(0);
        cmbSatuan.setSelectedIndex(0);
        txtHarga.setText("");
        txtStok.setText("");
        txtKodeBarang.requestFocus();
        tableBarang.clearSelection();
    }

    private void isiFormDariTable() {
        int row = tableBarang.getSelectedRow();

        txtKodeBarang.setText(tableModel.getValueAt(row, 0).toString());
        txtNamaBarang.setText(tableModel.getValueAt(row, 1).toString());
        cmbJenisBarang.setSelectedItem(tableModel.getValueAt(row, 2).toString());
        cmbSatuan.setSelectedItem(tableModel.getValueAt(row, 3).toString());
        txtHarga.setText(tableModel.getValueAt(row, 4).toString());
        txtStok.setText(tableModel.getValueAt(row, 5).toString());
    }
}
