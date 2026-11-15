package com.qlykho.controller;

import com.qlykho.dto.CapNhatTrangThaiRequest;
import com.qlykho.dto.TaoPhieuNhapRequest;
import com.qlykho.entity.PhieuNhap;
import com.qlykho.service.PhieuNhapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/phieu-nhap")
public class PhieuNhapController {
    private final PhieuNhapService phieuNhapService;

    public PhieuNhapController(PhieuNhapService phieuNhapService) {
        this.phieuNhapService = phieuNhapService;
    }

    @GetMapping
    public List<PhieuNhap> layDanhSachPhieuNhap() {
        return phieuNhapService.layDanhSachPhieuNhap();
    }

    @PostMapping
    public ResponseEntity<PhieuNhap> taoPhieuNhap(@RequestBody TaoPhieuNhapRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(phieuNhapService.taoPhieuNhap(request));
    }

    @PatchMapping("/{maPN}/trang-thai")
    public PhieuNhap capNhatTrangThai(
            @PathVariable Integer maPN,
            @RequestBody CapNhatTrangThaiRequest request
    ) {
        return phieuNhapService.capNhatTrangThai(maPN, request.trangThai());
    }
}
