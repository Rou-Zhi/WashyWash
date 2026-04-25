package com.washywash.model;

public class Pelanggan {
    private String kodePelanggan;
    private String namaPelanggan;
    private String noHp;
    private String email;

    public Pelanggan() {}

    public Pelanggan(String kodePelanggan, String namaPelanggan, String noHp, String email) {
        this.kodePelanggan = kodePelanggan;
        this.namaPelanggan = namaPelanggan;
        this.noHp = noHp;
        this.email = email;
    }

    public String getKodePelanggan() {
        return kodePelanggan;
    }

    public void setKodePelanggan(String kodePelanggan) {
        this.kodePelanggan = kodePelanggan;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
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
}
