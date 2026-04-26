package com.washywash.view;

import java.io.InputStream;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.washywash.model.Penjualan;
import com.washywash.service.LaporanService;
// import com.washywash.view.FormHistoryPenjualan;

import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

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
        JButton btnPrint = new JButton("Print PDF");

        panelFilter.add(new JLabel("Dari"));
        panelFilter.add(dateDari);
        panelFilter.add(new JLabel("Sampai"));
        panelFilter.add(dateSampai);
        panelFilter.add(btnTampil);
        panelFilter.add(btnReset);
        
        panelFilter.add(btnPrint);
        btnPrint.addActionListener(e -> exportPDF());

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
        // add(lblTotal, BorderLayout.SOUTH);

        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding
        panelBottom.add(lblTotal, BorderLayout.EAST);
        add(panelBottom, BorderLayout.SOUTH);   

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
    
   private void exportPDF() {
    try {
        // ===== AMBIL DATA =====
        List<Penjualan> list = laporanService.getSemuaPenjualan();

        List<Map<String, Object>> data = new ArrayList<>();
        
        double grandTotal = 0;

        for (Penjualan p : list) {
            p.refreshTotal();
            grandTotal += p.getTotal();

            Map<String, Object> row = new HashMap<>();
            row.put("kode", p.getKodePenjualan());
            row.put("tanggal", new SimpleDateFormat("yyyy-MM-dd").format(p.getTanggal()));
            row.put("pelanggan", p.getPelanggan().getNamaPelanggan());
            row.put("total", "Rp " + String.format("%,.0f", p.getTotal()));
            row.put("total", p.getTotal());
            
            data.add(row);
        }

        // ===== DATA SOURCE =====
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

        // ===== LOAD JRXML =====
        InputStream is = getClass().getResourceAsStream("/laporan_penjualan.jrxml");

        if (is == null) {
            JOptionPane.showMessageDialog(this, "File jrxml tidak ditemukan!");
            return;
        }

        // ===== COMPILE =====
        JasperReport report = JasperCompileManager.compileReport(is);

         // ===== PARAMETER =====
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("grandTotal", grandTotal);

        // ===== FILL REPORT =====
        JasperPrint print = JasperFillManager.fillReport(
                report,
                parameters,
                dataSource
        );

        // ===== EXPORT =====
        String filePath = System.getProperty("user.home")
                + "/Downloads/laporan_penjualan.pdf";

        JasperExportManager.exportReportToPdfFile(print, filePath);

        Desktop.getDesktop().open(new File(filePath));


        JOptionPane.showMessageDialog(this, "PDF berhasil dibuat!");

        // JOptionPane.showMessageDialog(this, "PDF berhasil dibuat!\nTotal: Rp " + String.format("%,.0f", grandTotal));

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
}
    

    private void reset() {
        dateDari.setDate(null);
        dateSampai.setDate(null);
        loadData(null, null);
    }

}
