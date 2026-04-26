package com.washywash.repository.impl;

import com.washywash.config.DatabaseConnection;
import com.washywash.model.Barang;
import com.washywash.model.DetailPenjualan;
import com.washywash.model.Pelanggan;
import com.washywash.model.Penjualan;
import com.washywash.repository.PenjualanRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;

public class PenjualanRepositoryImpl implements PenjualanRepository {
    @Override
    public void save(Penjualan p) {
        String sql = "INSERT INTO penjualan (kode_penjualan, kode_pelanggan, tanggal, diskon, total) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getKodePenjualan());
            ps.setString(2, p.getPelanggan().getKodePelanggan());
            ps.setDate(3, new java.sql.Date(p.getTanggal().getTime()));
            ps.setDouble(4, p.getDiskon());
            ps.setDouble(5, p.getTotal());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal simpan penjualan: " + e.getMessage());
        }
    }

    @Override
    public void update(Penjualan p) {
        String sql = "UPDATE penjualan SET kode_pelanggan=?, tanggal=?, diskon=?, total=? WHERE kode_penjualan=?";

        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getPelanggan().getKodePelanggan());
            ps.setDate(2, new java.sql.Date(p.getTanggal().getTime()));
            ps.setDouble(3, p.getDiskon());
            ps.setDouble(4, p.getTotal());
            ps.setString(5, p.getKodePenjualan());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal update penjualan: " + e.getMessage());
        }
    }

    @Override
    public void delete(String kodePenjualan) {
        String sql = "DELETE FROM penjualan WHERE kode_penjualan=?";

        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kodePenjualan);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal hapus penjualan: " + e.getMessage());
        }
    }

    @Override
    public Penjualan findById(String kodePenjualan) {
        String sql = "SELECT * FROM penjualan WHERE kode_penjualan=?";

        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kodePenjualan);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Pelanggan pelanggan = new Pelanggan();
                pelanggan.setKodePelanggan(rs.getString("kode_pelanggan"));

                Penjualan p = new Penjualan(
                        rs.getString("kode_penjualan"),
                        pelanggan,
                        rs.getDate("tanggal"),
                        rs.getDouble("diskon")
                );
                p.setTotal(rs.getDouble("total"));

                return p;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal cari penjualan: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Penjualan> findAll() {
        List<Penjualan> list = new ArrayList<>();

        String sql = """
            SELECT p.kode_penjualan, p.tanggal, p.diskon, p.total,
                pl.kode_pelanggan, pl.nama_pelanggan
            FROM penjualan p
            LEFT JOIN pelanggan pl ON p.kode_pelanggan = pl.kode_pelanggan
            ORDER BY p.tanggal DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                Pelanggan pl = new Pelanggan();
                pl.setKodePelanggan(rs.getString("kode_pelanggan"));
                pl.setNamaPelanggan(rs.getString("nama_pelanggan"));

                Penjualan p = new Penjualan(
                    rs.getString("kode_penjualan"),
                    pl,
                    rs.getDate("tanggal"),
                    rs.getDouble("diskon")
                );

                p.setTotal(rs.getDouble("total"));

                list.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public List<Penjualan> findAllDetails() {
        List<Penjualan> list = new ArrayList<>();

        String sql = """
            SELECT p.kode_penjualan, p.tanggal, p.diskon,
                pl.kode_pelanggan, pl.nama_pelanggan,
                b.kode_barang, b.nama_barang,
                d.qty, d.harga
            FROM penjualan p
            JOIN pelanggan pl ON p.kode_pelanggan = pl.kode_pelanggan
            JOIN detail_penjualan d ON p.kode_penjualan = d.kode_penjualan
            JOIN barang b ON d.kode_barang = b.kode_barang
            ORDER BY p.kode_penjualan
        """;

        try (Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            Map<String, Penjualan> map = new HashMap<>();

            while (rs.next()) {

                String kode = rs.getString("kode_penjualan");

                // ambil / buat penjualan
                Penjualan p = map.get(kode);
                if (p == null) {
                    Pelanggan pelanggan = new Pelanggan(
                        rs.getString("kode_pelanggan"),
                        rs.getString("nama_pelanggan"),
                        "", ""
                    );

                    p = new Penjualan();
                    p.setKodePenjualan(kode);
                    p.setTanggal(rs.getDate("tanggal"));
                    p.setDiskon(rs.getDouble("diskon"));
                    p.setPelanggan(pelanggan);

                    map.put(kode, p);
                }

                // barang
                Barang barang = new Barang();
                barang.setKodeBarang(rs.getString("kode_barang"));
                barang.setNamaBarang(rs.getString("nama_barang"));

                // detail
                DetailPenjualan d = new DetailPenjualan(
                    p,
                    barang,
                    rs.getInt("qty"),
                    rs.getDouble("harga")
                );

                p.getListDetail().add(d);
            }

            list.addAll(map.values());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<Penjualan> findByTanggalRange(Date dari, Date sampai) {
        List<Penjualan> list = new ArrayList<>();

        String sql = """
            SELECT p.kode_penjualan, p.tanggal, p.diskon, p.total,
                pl.kode_pelanggan, pl.nama_pelanggan
            FROM penjualan p
            LEFT JOIN pelanggan pl ON p.kode_pelanggan = pl.kode_pelanggan
            WHERE p.tanggal BETWEEN ? AND ?
            ORDER BY p.tanggal DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(dari.getTime()));
            ps.setDate(2, new java.sql.Date(sampai.getTime()));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Pelanggan pl = new Pelanggan();
                pl.setKodePelanggan(rs.getString("kode_pelanggan"));
                pl.setNamaPelanggan(rs.getString("nama_pelanggan"));

                Penjualan p = new Penjualan(
                    rs.getString("kode_penjualan"),
                    pl,
                    rs.getDate("tanggal"),
                    rs.getDouble("diskon")
                );

                p.setTotal(rs.getDouble("total"));

                list.add(p);
            }

        } catch (Exception e) {
            throw new RuntimeException("Gagal ambil data penjualan: " + e.getMessage());
        }

        return list;
    }


    @Override
    public double getTotalPendapatan(Date dari, Date sampai) {
        double total = 0;

        String sql = """
            SELECT SUM(d.qty * d.harga) AS total
            FROM detail_penjualan d
            JOIN penjualan p ON d.kode_penjualan = p.kode_penjualan
            WHERE p.tanggal BETWEEN ? AND ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            // kalau pakai filter
            if (dari != null && sampai != null) {
                ps.setDate(1, new java.sql.Date(dari.getTime()));
                ps.setDate(2, new java.sql.Date(sampai.getTime()));
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    @Override
    public List<Penjualan> findAllDetailsByTanggalRange(Date dari, Date sampai) {
        List<Penjualan> list = new ArrayList<>();

        String sql = """
            SELECT p.kode_penjualan, p.tanggal, p.diskon,
                pl.kode_pelanggan, pl.nama_pelanggan,
                b.kode_barang, b.nama_barang,
                d.qty, d.harga
            FROM penjualan p
            JOIN pelanggan pl ON p.kode_pelanggan = pl.kode_pelanggan
            JOIN detail_penjualan d ON p.kode_penjualan = d.kode_penjualan
            JOIN barang b ON d.kode_barang = b.kode_barang
            WHERE p.tanggal BETWEEN ? AND ?
            ORDER BY p.kode_penjualan
        """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            // set parameter tanggal
            ps.setDate(1, new java.sql.Date(dari.getTime()));
            ps.setDate(2, new java.sql.Date(sampai.getTime()));

            ResultSet rs = ps.executeQuery();

            Map<String, Penjualan> map = new HashMap<>();

            while (rs.next()) {
                String kode = rs.getString("kode_penjualan");

                // ambil / buat penjualan
                Penjualan p = map.get(kode);
                if (p == null) {
                    Pelanggan pelanggan = new Pelanggan(
                        rs.getString("kode_pelanggan"),
                        rs.getString("nama_pelanggan"),
                        "", ""
                    );

                    p = new Penjualan();
                    p.setKodePenjualan(kode);
                    p.setTanggal(rs.getDate("tanggal"));
                    p.setDiskon(rs.getDouble("diskon"));
                    p.setPelanggan(pelanggan);

                    map.put(kode, p);
                }

                // barang
                Barang barang = new Barang();
                barang.setKodeBarang(rs.getString("kode_barang"));
                barang.setNamaBarang(rs.getString("nama_barang"));

                // detail
                DetailPenjualan d = new DetailPenjualan(
                    p,
                    barang,
                    rs.getInt("qty"),
                    rs.getDouble("harga")
                );

                p.getListDetail().add(d);
            }

            list.addAll(map.values());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}