package com.washywash.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.washywash.model.Pelanggan;
import com.washywash.model.Penjualan;
import com.washywash.model.DetailPenjualan;

import com.washywash.repository.*;
import com.washywash.repository.impl.*;

import com.washywash.service.*;

import java.text.SimpleDateFormat;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

public class FormPenjualan extends JPanel {
    private JTextField txtKodePenjualan;
    private JDateChooser dateTanggal;
    private JComboBox<Pelanggan> cmbPelanggan;
    private JTextField txtDiskon;
    private JLabel lblTotal;

    private JButton btnTambahItem;
    private JButton btnHapusItem;
    private JButton btnSimpan;

    private JTable tableDetail;
    private DefaultTableModel tableModel;

    private final PenjualanService penjualanService;
    private final DetailPenjualanService detailService;
    private final PelangganRepository pelangganRepo;

    public FormPenjualan() {
        PenjualanRepository penRepo = new PenjualanRepositoryImpl();
        DetailPenjualanRepository detRepo = new DetailPenjualanRepositoryImpl();
        pelangganRepo = new PelangganRepositoryImpl();

        detailService = new DetailPenjualanService(detRepo);
        penjualanService = new PenjualanService(penRepo, detailService);

        initComponents();
        setDefaultTanggal();
        loadPelanggan();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Kode
        gbc.gridx=0; gbc.gridy=0; gbc.weightx = 1;
        form.add(new JLabel("Kode"), gbc);

        gbc.gridx=1; gbc.weightx = 1;
        txtKodePenjualan = new JTextField();
        form.add(txtKodePenjualan, gbc);

        // Tanggal
        gbc.gridx=0; gbc.gridy=1;
        form.add(new JLabel("Tanggal"), gbc);

        gbc.gridx=1; 
        dateTanggal = new JDateChooser();
        dateTanggal.setDateFormatString("yyyy-MM-dd");

        form.add(dateTanggal, gbc);

        // Pelanggan
        gbc.gridx=0; gbc.gridy=2;
        form.add(new JLabel("Pelanggan"), gbc);

        gbc.gridx=1;
        cmbPelanggan = new JComboBox<>();
        form.add(cmbPelanggan, gbc);

        // Diskon
        gbc.gridx=0; gbc.gridy=3;
        form.add(new JLabel("Diskon"), gbc);

        gbc.gridx=1;
        txtDiskon = new JTextField("0");
        form.add(txtDiskon, gbc);

        // Total 
        gbc.gridx=0; gbc.gridy=4;
        form.add(new JLabel("Total"), gbc);

        gbc.gridx=1;
        lblTotal = new JLabel("0");
        form.add(lblTotal, gbc);

        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnTambahItem = new JButton("Tambah Item");
        btnHapusItem = new JButton("Hapus Item");
        btnSimpan = new JButton("Simpan");

        panelButton.add(btnTambahItem);
        panelButton.add(btnHapusItem);
        panelButton.add(btnSimpan);

        // Aksi
        gbc.gridx = 0; gbc.gridy = 5;
        form.add(new JLabel("Aksi"), gbc);

        gbc.gridx=1;
        form.add(panelButton, gbc);

        String[] columns = {
            "Kode Barang",
            "Qty",
            "Harga",
            "Subtotal"
        };

        tableModel = new DefaultTableModel(columns, 0);
        tableDetail = new JTable(tableModel);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(tableDetail), BorderLayout.CENTER);

        btnTambahItem.addActionListener(e -> openDetail());
        btnHapusItem.addActionListener(e -> hapusItem());
        btnSimpan.addActionListener(e -> simpanPenjualan());
    }

    private void loadPelanggan() {
        List<Pelanggan> list = pelangganRepo.findAll();

        cmbPelanggan.removeAllItems();

        if (list.isEmpty()) {
            cmbPelanggan.addItem(null);
            cmbPelanggan.setRenderer((list1, value, index, isSelected, cellHasFocus) ->
                new JLabel("Belum ada data pelanggan")
            );
        } else {
            for (Pelanggan p : list) {
                cmbPelanggan.addItem(p);
            }

            cmbPelanggan.setRenderer(new DefaultListCellRenderer(){
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus){

                    JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                    if(value instanceof Pelanggan p){
                        lbl.setText(p.getKodePelanggan()+" - "+p.getNamaPelanggan());
                    }

                    return lbl;
                }
            });
        }
    }

    private void loadDetail() {
        tableModel.setRowCount(0);

        String kode = txtKodePenjualan.getText();
        if (kode.isBlank()) return;

        List<DetailPenjualan> list = detailService.getByPenjualan(kode);

        for (DetailPenjualan d : list) {
            tableModel.addRow(new Object[]{
                d.getBarang().getKodeBarang(),
                d.getQty(),
                d.getHarga(),
                d.getSubtotal()
            });
        }

        double total = detailService.hitungTotal(kode);
        lblTotal.setText(String.valueOf(total));
    }

    private void openDetail() {
        if (txtKodePenjualan.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Isi kode penjualan dulu!");
            return;
        }

        new FormDetailPenjualan(
            txtKodePenjualan.getText(),
            detailService,
            this::loadDetail
        ).setVisible(true);
    }

    private void hapusItem() {
        int row = tableDetail.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item yang mau dihapus!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin hapus item ini?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String kodeBarang = tableModel.getValueAt(row, 0).toString();
                String kodePenjualan = txtKodePenjualan.getText();

                detailService.hapusItem(kodePenjualan, kodeBarang);

                loadDetail();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void simpanPenjualan() {
        try {
            Pelanggan p = (Pelanggan) cmbPelanggan.getSelectedItem();

            if (p == null) {
                JOptionPane.showMessageDialog(this, "Pelanggan belum ada!");
                return;
            }

            Date tanggal = dateTanggal.getDate();

            if (tanggal == null) {
                JOptionPane.showMessageDialog(this, "Tanggal wajib dipilih!");
                return;
            }

            Penjualan penjualan = new Penjualan(
                txtKodePenjualan.getText(),
                p,
                tanggal,
                Double.parseDouble(txtDiskon.getText())
            );

            penjualan.setTotal(Double.parseDouble(lblTotal.getText()));

            penjualanService.tambahPenjualan(penjualan);

            JOptionPane.showMessageDialog(this, "Berhasil simpan");
            reset();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void setDefaultTanggal() {
        dateTanggal.setDate(new Date());
    }

    private void reset() {
        txtKodePenjualan.setText("");
        txtDiskon.setText("0");
        lblTotal.setText("0");
        tableModel.setRowCount(0);
        setDefaultTanggal();
    }
}