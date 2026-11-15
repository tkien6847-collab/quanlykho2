package com.qlykho.service;

import com.qlykho.dto.PhieuNhapChiTietResponse;
import com.qlykho.repository.ChiTietPhieuNhapRepository;
import com.qlykho.repository.PhieuNhapRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class ChiTietPhieuNhapService {
    private final PhieuNhapRepository phieuNhapRepository;
    private final ChiTietPhieuNhapRepository chiTietPhieuNhapRepository;

    public ChiTietPhieuNhapService(
            PhieuNhapRepository phieuNhapRepository,
            ChiTietPhieuNhapRepository chiTietPhieuNhapRepository
    ) {
        this.phieuNhapRepository = phieuNhapRepository;
        this.chiTietPhieuNhapRepository = chiTietPhieuNhapRepository;
    }

    @Transactional(readOnly = true)
    public PhieuNhapChiTietResponse layChiTietPhieuNhap(Integer maPN) {
        return phieuNhapRepository.findById(maPN)
                .map(phieuNhap -> new PhieuNhapChiTietResponse(
                        phieuNhap,
                        chiTietPhieuNhapRepository.findById_MaPN(maPN)
                ))
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy phiếu nhập."));
    }
}