package com.qlykho.controller;

import com.qlykho.dto.PhieuNhapChiTietResponse;
import com.qlykho.service.ChiTietPhieuNhapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chi-tiet-phieu-nhap")
public class ChiTietPhieuNhapController {
    private final ChiTietPhieuNhapService chiTietPhieuNhapService;

    public ChiTietPhieuNhapController(ChiTietPhieuNhapService chiTietPhieuNhapService) {
        this.chiTietPhieuNhapService = chiTietPhieuNhapService;
    }

    @GetMapping("/{maPN}")
    public PhieuNhapChiTietResponse layChiTietPhieuNhap(@PathVariable Integer maPN) {
        return chiTietPhieuNhapService.layChiTietPhieuNhap(maPN);
    }
}
