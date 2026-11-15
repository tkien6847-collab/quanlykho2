package com.qlykho.controller;

import com.qlykho.entity.KhachHang;
import com.qlykho.service.KhachHangService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/khach-hang")
public class KhachHangController {
    private final KhachHangService khachHangService;

    public KhachHangController(KhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }

    @GetMapping
    public List<KhachHang> layDanhSachKhachHang() {
        return khachHangService.layDanhSachKhachhang();
    }

    @PostMapping
    public ResponseEntity<?> themKhachHang(@RequestBody KhachHang khachHang) {
        try {
            KhachHang khachHangMoi = khachHangService.themKhachHang(khachHang);
            return ResponseEntity.status(HttpStatus.CREATED).body(khachHangMoi);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{maKH}")
    public ResponseEntity<String> xoaKhachHang(@PathVariable Integer maKH) {
        try {
            khachHangService.xoaKhachHang(maKH);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
