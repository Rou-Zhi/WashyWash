package com.washywash.service;

import com.washywash.model.User;
import com.washywash.repository.UserRepository;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void tambahUser(User user) {
        validateUser(user);

        if (userRepository.findById(user.getKodeUser()) != null) {
            throw new IllegalArgumentException("Kode user sudah ada.");
        }

        userRepository.save(user);
    }

    public void updateUser(User user) {
        validateUser(user);

        if (userRepository.findById(user.getKodeUser()) == null) {
            throw new IllegalArgumentException("User tidak ditemukan.");
        }

        userRepository.update(user);
    }

    public void hapusUser(String kodeUser) {
        if (kodeUser == null || kodeUser.isBlank()) {
            throw new IllegalArgumentException("Kode user tidak boleh kosong.");
        }

        if (userRepository.findById(kodeUser) == null) {
            throw new IllegalArgumentException("User tidak ditemukan.");
        }

        userRepository.delete(kodeUser);
    }

    public User cariUser(String kodeUser) {
        if (kodeUser == null || kodeUser.isBlank()) {
            throw new IllegalArgumentException("Kode user tidak boleh kosong.");
        }

        return userRepository.findById(kodeUser);
    }

    public List<User> getSemuaUser() {
        return userRepository.findAll();
    }

    public User login(String kodeUser, String password) {
        User user = userRepository.findById(kodeUser);

        if (user == null) {
            throw new IllegalArgumentException("User tidak ditemukan.");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new IllegalArgumentException("Password salah.");
        }

        return user;
    }

    private void validateUser(User user) {
        if (user.getKodeUser() == null || user.getKodeUser().isBlank()) {
            throw new IllegalArgumentException("Kode user wajib diisi.");
        }

        if (user.getNamaUser() == null || user.getNamaUser().isBlank()) {
            throw new IllegalArgumentException("Nama user wajib diisi.");
        }

        if (user.getNoHp() == null || user.getNoHp().isBlank()) {
            throw new IllegalArgumentException("No HP wajib diisi.");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email wajib diisi.");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password wajib diisi.");
        }
    }
}