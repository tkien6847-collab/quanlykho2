package com.qlykho.controller;

import com.qlykho.dto.DangNhapRequest;
import com.qlykho.dto.DangNhapResponse;
import com.qlykho.dto.TaiKhoanIdentityResponse;
import com.qlykho.dto.ThongTinCaNhan;
import com.qlykho.service.DangNhapService;
import com.qlykho.service.NhanVienService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class DangNhapController {
    private final DangNhapService dangNhapService;
    private final NhanVienService nhanVienService;

    public DangNhapController(DangNhapService dangNhapService, NhanVienService nhanVienService) {
        this.dangNhapService = dangNhapService;
        this.nhanVienService = nhanVienService;
    }

    @PostMapping("/login")
    public DangNhapResponse dangNhap(@RequestBody DangNhapRequest request) {
        return dangNhapService.dangNhap(request);
    }

    @GetMapping("/me")
    public TaiKhoanIdentityResponse danhTinh(Authentication authentication) {
        return dangNhapService.layDanhTinh(authentication.getName());
    }

    @GetMapping("/me/profile")
    public ThongTinCaNhan thongTinCaNhan(Authentication authentication) {
        return nhanVienService.layThongTinCaNhan(authentication.getName());
    }

    @PutMapping("/me/profile")
    public ThongTinCaNhan capNhatThongTinCaNhan(
            Authentication authentication,
            @RequestBody ThongTinCaNhan thongTin
    ) {
        return nhanVienService.capNhatThongTinCaNhan(authentication.getName(), thongTin);
    }
}
