package com.qlykho.controller;

import com.qlykho.dto.CapNhatTrangThaiRequest;
import com.qlykho.dto.KhachHangLuaChon;
import com.qlykho.entity.KhachHang;
import com.qlykho.service.KhachHangService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @GetMapping("/lua-chon")
    public List<KhachHangLuaChon> layLuaChonKhachHang() {
        return khachHangService.layLuaChonKhachHang();
    }

    @PostMapping
    public ResponseEntity<KhachHang> themKhachHang(@RequestBody KhachHang khachHang) {
        return ResponseEntity.status(HttpStatus.CREATED).body(khachHangService.themKhachHang(khachHang));
    }

    @PatchMapping("/{maKH}/trang-thai")
    public KhachHang capNhatTrangThai(
            @PathVariable("maKH") Integer maKH,
            @RequestBody CapNhatTrangThaiRequest request
    ) {
        return khachHangService.capNhatTrangThai(maKH, request.trangThai());
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
