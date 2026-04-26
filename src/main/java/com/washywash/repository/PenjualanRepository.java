package com.washywash.repository;

import com.washywash.model.Penjualan;
import java.util.List;
import java.util.Date;

public interface PenjualanRepository {
    void save(Penjualan penjualan);
    void update(Penjualan penjualan);
    void delete(String kodePenjualan);
    Penjualan findById(String kodePenjualan);
    List<Penjualan> findAll();
    List<Penjualan> findByTanggalRange(Date dari, Date sampai);


    List<Penjualan> findAllDetails();
    // List<Penjualan> findWithDetailByTanggal(Date dari, Date sampai);
    double getTotalPendapatan(Date dari, Date sampai);
    List<Penjualan> findAllDetailsbyTanggalRange(Date dari, Date sampai);
}
