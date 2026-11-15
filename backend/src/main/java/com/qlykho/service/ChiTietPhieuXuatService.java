package com.qlykho.service;

import com.qlykho.dto.PhieuXuatChiTietResponse;
import com.qlykho.repository.ChiTietPhieuXuatRepository;
import com.qlykho.repository.PhieuXuatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class ChiTietPhieuXuatService {
    private final PhieuXuatRepository phieuXuatRepository;
    private final ChiTietPhieuXuatRepository chiTietPhieuXuatRepository;

    public ChiTietPhieuXuatService(
            PhieuXuatRepository phieuXuatRepository,
            ChiTietPhieuXuatRepository chiTietPhieuXuatRepository
    ) {
        this.phieuXuatRepository = phieuXuatRepository;
        this.chiTietPhieuXuatRepository = chiTietPhieuXuatRepository;
    }

    @Transactional(readOnly = true)
    public PhieuXuatChiTietResponse layChiTietPhieuXuat(Integer maPX) {
        return phieuXuatRepository.findById(maPX)
                .map(phieuXuat -> new PhieuXuatChiTietResponse(
                        phieuXuat,
                        chiTietPhieuXuatRepository.findById_MaPX(maPX)
                ))
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy phiếu xuất."));
    }
}