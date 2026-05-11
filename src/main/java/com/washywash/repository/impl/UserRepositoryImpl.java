package com.washywash.repository.impl;

import com.washywash.config.DatabaseConnection;
import com.washywash.model.User;
import com.washywash.repository.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public class UserRepositoryImpl implements UserRepository{


    @Override
    public String generateKodeUser() {
        String kode = "U001";

        try {
            String sql = "SELECT kode_user FROM user ORDER BY kode_user DESC LIMIT 1";

            Connection conn = DatabaseConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                String lastKode = rs.getString("kode_user");

                int angka = Integer.parseInt(lastKode.substring(1)) + 1;

                kode = "U" + String.format("%03d", angka);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    return kode;
    }
    @Override
    public void save(User user) {
        user.setKodeUser(generateKodeUser());
        
        String sql = "INSERT INTO user (kode_user, nama_user, no_hp, email, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            ps.setString(1, user.getKodeUser());
            ps.setString(2, user.getNamaUser());
            ps.setString(3, user.getNoHp());
            ps.setString(4, user.getEmail());
            ps.setString(5, hashedPassword);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menyimpan user: " + e.getMessage());
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE user SET nama_user=?, no_hp=?, email=?, password=? WHERE kode_user=?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getNamaUser());
            ps.setString(2, user.getNoHp());
            ps.setString(3, user.getEmail());

            String password = user.getPassword();

            if (!password.startsWith("$2a$")) {
                password = BCrypt.hashpw(password, BCrypt.gensalt());
            }

            ps.setString(4, password);
            ps.setString(5, user.getKodeUser());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengupdate user: " + e.getMessage());
        }
    }

    @Override
    public void delete(String kodeUser) {
        String sql = "DELETE FROM user WHERE kode_user=?";

        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kodeUser);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghapus user: " +
            e.getMessage());
        }
    }

    @Override
    public User findById(String kodeUser) {
        String sql = "SELECT * FROM user WHERE kode_user=?";


        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kodeUser);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
            return new User(
            rs.getString("kode_user"),
            rs.getString("nama_user"),
            rs.getString("no_hp"),
            rs.getString("email"),
            rs.getString("password")
            );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencari user: " + e.getMessage());
        }
        return null;
    }

    @Override
    public User findByEmailOrKode(String input) {
        String sql = "SELECT * FROM user WHERE email=? OR kode_user=?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, input);
            ps.setString(2, input);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                    rs.getString("kode_user"),
                    rs.getString("nama_user"),
                    rs.getString("no_hp"),
                    rs.getString("email"),
                    rs.getString("password")
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Gagal login: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY kode_user ASC";

        try (Connection conn = DatabaseConnection.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
            User user = new User(
            rs.getString("kode_user"),
            rs.getString("nama_user"),
            rs.getString("no_hp"),
            rs.getString("email"),
            rs.getString("password")
            );
            list.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil data user: " + e.getMessage());
        }
        return list;
    }
}
