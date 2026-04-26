package com.washywash.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.washywash.model.Penjualan;
import com.washywash.service.LaporanService;

import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FormLaporan extends JPanel{
    private JDateChooser dateDari;
    private JDateChooser dateSampai;
    private JButton btnTampil;
    private JButton btnReset;

    private JTable table;
    private DefaultTableModel model;

    private JLabel lblTotal;

    private LaporanService laporanService;

    public FormLaporan() {
        laporanService = new LaporanService();
        initComponents();
        loadData(null, null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // ===== PANEL FILTER =====
        JPanel panelFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));

        dateDari = new JDateChooser();
        dateSampai = new JDateChooser();

        dateDari.setDateFormatString("yyyy-MM-dd");
        dateSampai.setDateFormatString("yyyy-MM-dd");

        btnTampil = new JButton("Tampilkan");
        btnReset = new JButton("Reset");

        panelFilter.add(new JLabel("Dari"));
        panelFilter.add(dateDari);
        panelFilter.add(new JLabel("Sampai"));
        panelFilter.add(dateSampai);
        panelFilter.add(btnTampil);
        panelFilter.add(btnReset);

        // ===== TABLE =====
        String[] columns = {
            "Kode",
            "Tanggal",
            "Pelanggan",
            "Total"
        };

        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        // ===== TOTAL =====
        lblTotal = new JLabel("Total: 0");

        // ===== ADD =====
        add(panelFilter, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(lblTotal, BorderLayout.SOUTH);

        // ===== ACTION =====
        btnTampil.addActionListener(e -> tampilkanData());
        btnReset.addActionListener(e -> reset());
    }

    private void loadData(Date dari, Date sampai) {
        model.setRowCount(0);

        List<Penjualan> list;

        if (dari == null || sampai == null) {
            list = laporanService.getSemuaPenjualan();
        } else {
            list = laporanService.getByTanggal(dari, sampai);
        }

        double totalSemua = 0;

        for (Penjualan p : list) {
            p.refreshTotal();

            totalSemua += p.getTotal();

            model.addRow(new Object[]{
                p.getKodePenjualan(),
                new SimpleDateFormat("yyyy-MM-dd").format(p.getTanggal()),
                p.getPelanggan().getNamaPelanggan(),
                String.format("Rp %,.0f", p.getTotal())
            });
        }

        lblTotal.setText("Total: Rp " + String.format("%,.0f", totalSemua));
    }

    private void tampilkanData() {
        Date dari = dateDari.getDate();
        Date sampai = dateSampai.getDate();

        if (dari == null || sampai == null) {
            JOptionPane.showMessageDialog(this, "Pilih tanggal dulu!");
            return;
        }

        if (dari.after(sampai)) {
            JOptionPane.showMessageDialog(this, "Tanggal tidak valid!");
            return;
        }

        loadData(dari, sampai);
    }

    private void reset() {
        dateDari.setDate(null);
        dateSampai.setDate(null);
        loadData(null, null);
    }
}
