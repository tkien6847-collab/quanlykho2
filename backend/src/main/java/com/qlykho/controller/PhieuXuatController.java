package com.qlykho.controller;

import com.qlykho.dto.CapNhatTrangThaiRequest;
import com.qlykho.dto.TaoPhieuXuatRequest;
import com.qlykho.entity.PhieuXuat;
import com.qlykho.service.PhieuXuatService;
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
@RequestMapping("/api/phieu-xuat")
public class PhieuXuatController {
    private final PhieuXuatService phieuXuatService;

    public PhieuXuatController(PhieuXuatService phieuXuatService) {
        this.phieuXuatService = phieuXuatService;
    }

    @GetMapping
    public List<PhieuXuat> layDanhSachPhieuXuat() {
        return phieuXuatService.layDanhSachPhieuXuat();
    }

    @PostMapping
    public ResponseEntity<PhieuXuat> taoPhieuXuat(@RequestBody TaoPhieuXuatRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(phieuXuatService.taoPhieuXuat(request));
    }

    @PatchMapping("/{maPX}/trang-thai")
    public PhieuXuat capNhatTrangThai(
            @PathVariable Integer maPX,
            @RequestBody CapNhatTrangThaiRequest request
    ) {
        return phieuXuatService.capNhatTrangThai(maPX, request.trangThai());
    }
}
