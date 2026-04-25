package com.washywash.model;

public class User {
    private String kodeUser;
    private String namaUser;
    private String noHp;
    private String email;
    private String password;

    public User() {}

    public User(String kodeUser, String namaUser, String noHp, String email) {
        this.kodeUser = kodeUser;
        this.namaUser = namaUser;
        this.noHp = noHp;
        this.email = email;
    }

    public User(String kodeUser, String namaUser, String noHp, String email, String password) {
        this.kodeUser = kodeUser;
        this.namaUser = namaUser;
        this.noHp = noHp;
        this.email = email;
        this.password = password;
    }

    public String getKodeUser() {
        return kodeUser;
    }

    public void setKodeUser(String kodeUser) {
        this.kodeUser = kodeUser;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
