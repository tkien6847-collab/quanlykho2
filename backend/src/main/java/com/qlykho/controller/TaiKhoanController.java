package com.qlykho.controller;

import com.qlykho.dto.CapNhatTrangThaiRequest;
import com.qlykho.dto.CapNhatVaiTroRequest;
import com.qlykho.dto.TaiKhoanResponse;
import com.qlykho.dto.TaoTaiKhoanQuanLyRequest;
import com.qlykho.dto.TaoTaiKhoanNhanVienRequest;
import com.qlykho.service.TaiKhoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/api/tai-khoan")
public class TaiKhoanController {
    private final TaiKhoanService taiKhoanService;

    public TaiKhoanController(TaiKhoanService taiKhoanService) {
        this.taiKhoanService = taiKhoanService;
    }

    @GetMapping
    public List<TaiKhoanResponse> layDanhSachTaiKhoan() {
        return taiKhoanService.layDanhSachTaiKhoan();
    }

    @PostMapping("/quan-ly")
    public ResponseEntity<TaiKhoanResponse> taoTaiKhoanQuanLy(@RequestBody TaoTaiKhoanQuanLyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taiKhoanService.taoTaiKhoanQuanLy(request));
    }

    @PostMapping("/nhan-vien")
    public ResponseEntity<TaiKhoanResponse> taoTaiKhoanNhanVien(@RequestBody TaoTaiKhoanNhanVienRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taiKhoanService.taoTaiKhoanNhanVien(request));
    }

    @PatchMapping("/{maTK}/vai-tro")
    public TaiKhoanResponse capNhatVaiTro(
            @PathVariable Integer maTK,
            @RequestBody CapNhatVaiTroRequest request
    ) {
        return taiKhoanService.capNhatVaiTro(maTK, request.maVaiTro());
    }

    @PatchMapping("/{maTK}/trang-thai")
    public TaiKhoanResponse capNhatTrangThai(
            @PathVariable("maTK") Integer maTK,
            @RequestBody CapNhatTrangThaiRequest request,
            Authentication authentication
    ) {
        return taiKhoanService.capNhatTrangThai(maTK, request.trangThai(), authentication.getName());
    }

    @DeleteMapping("/{maTK}")
    public ResponseEntity<Void> xoaTaiKhoan(@PathVariable Integer maTK) {
        taiKhoanService.xoaTaiKhoan(maTK);
        return ResponseEntity.noContent().build();
    }
}
