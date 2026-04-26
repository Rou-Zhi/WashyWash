package com.washywash.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.washywash.model.Penjualan;
import com.washywash.model.DetailPenjualan;

import com.washywash.repository.impl.*;
import com.washywash.service.*;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormHistoryPenjualan extends JPanel {
    private JLabel lblTotal;


    private JDateChooser dateDari;
    private JDateChooser dateSampai;
    private JButton btnCari;
    private JButton btnReset;

    private JTable tablePenjualan;
    private DefaultTableModel tableModel;

    private final PenjualanService penjualanService;
    private final DetailPenjualanService detailService;


    public FormHistoryPenjualan() {
        penjualanService = new PenjualanService(
            new PenjualanRepositoryImpl(),
            new DetailPenjualanService(new DetailPenjualanRepositoryImpl())
        );

        detailService = new DetailPenjualanService(new DetailPenjualanRepositoryImpl());

        initComponents();
        loadSemuaData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel panelFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));

        dateDari = new JDateChooser();
        dateSampai = new JDateChooser();

        dateDari.setDateFormatString("yyyy-MM-dd");
        dateSampai.setDateFormatString("yyyy-MM-dd");

        dateDari.setDate(new Date());
        dateSampai.setDate(new Date());

        btnCari = new JButton("Cari");
        btnReset = new JButton("Reset");

        panelFilter.add(new JLabel("Dari"));
        panelFilter.add(dateDari);
        panelFilter.add(new JLabel("Sampai"));
        panelFilter.add(dateSampai);
        panelFilter.add(btnCari);
        panelFilter.add(btnReset);


        String[] columns = {
            "Kode",
            "Tanggal",
            "Pelanggan",
            "Diskon",
            "Total"
        };

        tableModel = new DefaultTableModel(columns, 0);
        tablePenjualan = new JTable(tableModel);

        add(panelFilter, BorderLayout.NORTH);
        add(new JScrollPane(tablePenjualan), BorderLayout.CENTER);
        lblTotal = new JLabel("Total: Rp 0");
        // add(lblTotal, BorderLayout.SOUTH);

        btnCari.addActionListener(e -> cariData());
        btnReset.addActionListener(e -> resetFilter());

        tablePenjualan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablePenjualan.getSelectedRow() != -1) {
                showDetailPopup();
            }
        });
    }

    private void loadSemuaData() {
        tableModel.setRowCount(0);

        List<Penjualan> list = penjualanService.getSemuaPenjualan();
        double total = 0;

        for (Penjualan p : list) {
            p.refreshTotal();
            total += p.getTotal();

            tableModel.addRow(new Object[]{
                p.getKodePenjualan(),
                new SimpleDateFormat("yyyy-MM-dd").format(p.getTanggal()),
                p.getPelanggan().getNamaPelanggan(),
                p.getDiskon(),
                p.getTotal()
            });
        }
        lblTotal.setText("Total: Rp " + String.format("%,.0f", total));
    }

    private void cariData() {
        try {
            Date dari = dateDari.getDate();
            Date sampai = dateSampai.getDate();

            if (dari == null || sampai == null) {
                JOptionPane.showMessageDialog(this, "Pilih tanggal dulu!");
                return;
            }

            if (dari.after(sampai)) {
                JOptionPane.showMessageDialog(this, "Tanggal 'Dari' tidak boleh lebih besar!");
                return;
            }

            tableModel.setRowCount(0);

            List<Penjualan> list = penjualanService.cariByTanggal(dari, sampai);
            double total = 0;

            for (Penjualan p : list) {
                tableModel.addRow(new Object[]{
                    p.getKodePenjualan(),
                    new SimpleDateFormat("yyyy-MM-dd").format(p.getTanggal()),
                    p.getPelanggan().getNamaPelanggan(),
                    p.getDiskon(),
                    p.getTotal()
                });
                
            }
            lblTotal.setText("Total: Rp " + String.format("%,.0f", total));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void resetFilter() {
        dateDari.setDate(new Date());
        dateSampai.setDate(new Date());
        loadSemuaData();
    }

    private void showDetailPopup() {
        int row = tablePenjualan.getSelectedRow();
        String kodePenjualan = tableModel.getValueAt(row, 0).toString();

        List<DetailPenjualan> list = detailService.getByPenjualan(kodePenjualan);

        JDialog dialog = new JDialog((Frame) null, "Detail Penjualan", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(null);

        String[] columns = {
            "Kode Barang",
            "Qty",
            "Harga",
            "Subtotal"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable tableDetail = new JTable(model);

        for (DetailPenjualan d : list) {
            model.addRow(new Object[]{
                d.getBarang().getKodeBarang(),
                d.getQty(),
                d.getHarga(),
                d.getSubtotal()
            });
        }

        dialog.add(new JScrollPane(tableDetail));
        dialog.setVisible(true);
    }

    
}