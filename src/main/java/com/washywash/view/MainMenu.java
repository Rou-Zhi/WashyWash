package com.washywash.view;

import javax.swing.*;
import java.awt.*;


public class MainMenu extends JFrame {
    private CardLayout cardLayout;
    private JPanel panelContent;

    private FormHistoryPenjualan formHistory;
    private FormLaporan formLaporan;

    public MainMenu() {
        setTitle("WashyWash Dashboard");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        panelContent = createContent();

        add(sidebar, BorderLayout.WEST);
        add(panelContent, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBackground(new Color(32, 178, 170));
        panel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(32, 178, 170)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
        );

        JButton btnUser = createButton("User");
        JButton btnPelanggan = createButton("Pelanggan");
        JButton btnBarang = createButton("Barang");
        JButton btnLaporan = createButton("Laporan");

        JComboBox<String> cmbPenjualan = createComboBox(new String[]{
            "Penjualan",
            "Tambah Penjualan",
            "History Penjualan"
            // "Laporan Penjualan"
        });

        JButton btnLogout = createButton("Logout");

        btnUser.addActionListener(e ->
            cardLayout.show(panelContent, "user")
        );

        btnPelanggan.addActionListener(e ->
            cardLayout.show(panelContent, "pelanggan")
        );

        btnBarang.addActionListener(e ->
            cardLayout.show(panelContent, "barang")
        );

        btnLaporan.addActionListener(e ->{
            formLaporan.refreshData();
            cardLayout.show(panelContent, "laporan");
        });

        cmbPenjualan.addActionListener(e -> {
            String selected = (String) cmbPenjualan.getSelectedItem();

            if ("Tambah Penjualan".equals(selected)) {
                cardLayout.show(panelContent, "penjualan");
            }

            if ("History Penjualan".equals(selected)) {
                formHistory.refreshData();
                cardLayout.show(panelContent, "history");
            }

            if("Laporan Penjualan".equals(selected)) {
                formLaporan.refreshData();
                cardLayout.show(panelContent, "laporan");
            }

            cmbPenjualan.setSelectedIndex(0); 
        });

        btnLogout.addActionListener(e -> logout());

        panel.add(btnUser);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnPelanggan);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnBarang);
        panel.add(Box.createVerticalStrut(10));
        panel.add(cmbPenjualan);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnLaporan);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnLogout);

        return panel;
    }

    private JPanel createContent() {
        cardLayout = new CardLayout();
        JPanel panel = new JPanel(cardLayout);

        formHistory = new FormHistoryPenjualan();
        formLaporan = new FormLaporan();

        panel.add(new FormUser(), "user");
        panel.add(new FormPelanggan(), "pelanggan");
        panel.add(new FormBarang(), "barang");
        panel.add(new FormPenjualan(), "penjualan");
        panel.add(formHistory, "history");
        panel.add(formLaporan, "laporan");

        return panel;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(32, 178, 170));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> cmb = new JComboBox<>(items);
        cmb.setBackground(Color.WHITE);
        cmb.setForeground(new Color(32, 178, 170));
        cmb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cmb.setAlignmentX(Component.LEFT_ALIGNMENT);

        cmb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

                label.setHorizontalAlignment(SwingConstants.CENTER); 
                
                return label;
            }
        });

        return cmb;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin logout?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            new FormLogin().setVisible(true); 
            dispose(); 
        }
    }
}