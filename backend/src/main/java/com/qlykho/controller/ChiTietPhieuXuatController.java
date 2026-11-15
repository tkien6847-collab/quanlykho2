package com.qlykho.controller;

import com.qlykho.dto.PhieuXuatChiTietResponse;
import com.qlykho.service.ChiTietPhieuXuatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chi-tiet-phieu-xuat")
public class ChiTietPhieuXuatController {
    private final ChiTietPhieuXuatService chiTietPhieuXuatService;

    public ChiTietPhieuXuatController(ChiTietPhieuXuatService chiTietPhieuXuatService) {
        this.chiTietPhieuXuatService = chiTietPhieuXuatService;
    }

    @GetMapping("/{maPX}")
    public PhieuXuatChiTietResponse layChiTietPhieuXuat(@PathVariable Integer maPX) {
        return chiTietPhieuXuatService.layChiTietPhieuXuat(maPX);
    }
}
