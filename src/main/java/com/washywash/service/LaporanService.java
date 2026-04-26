package com.washywash.service;

import com.washywash.repository.PenjualanRepository;
import com.washywash.repository.impl.PenjualanRepositoryImpl;
import com.washywash.model.Penjualan;

import java.util.Date;
import java.util.List;

public class LaporanService {
    private PenjualanRepository penjualanRepo = new PenjualanRepositoryImpl();

    public List<Penjualan> getSemuaPenjualan() {
        return penjualanRepo.findAllDetails();
    }

    public double getTotalPendapatan() {
        return penjualanRepo.getTotalPendapatan(null, null);
    }

    public List<Penjualan> getByTanggal( Date dari, Date sampai) {
        return penjualanRepo.findAllDetailsbyTanggalRange(dari, sampai);
}
}
