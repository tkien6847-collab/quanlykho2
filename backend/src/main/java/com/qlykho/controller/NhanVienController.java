package com.qlykho.controller;

import com.qlykho.entity.NhanVien;
import com.qlykho.service.NhanVienService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/nhan-vien")
public class NhanVienController {
    private final NhanVienService nhanVienService;

    public NhanVienController(NhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    @GetMapping
    public List<NhanVien> layDanhSachNhanVien() {
        return nhanVienService.layDanhSachNhanVien();
    }

    @PostMapping
    public ResponseEntity<NhanVien> themNhanVien(@RequestBody NhanVien nhanVien) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nhanVienService.themNhanVien(nhanVien));
    }

    @PutMapping("/{maNV}")
    public NhanVien capNhatNhanVien(@PathVariable Integer maNV, @RequestBody NhanVien nhanVien) {
        return nhanVienService.capNhatNhanVien(maNV, nhanVien);
    }

    @DeleteMapping("/{maNV}")
    public ResponseEntity<Void> xoaNhanVien(@PathVariable Integer maNV) {
        nhanVienService.xoaNhanVien(maNV);
        return ResponseEntity.noContent().build();
    }
}
