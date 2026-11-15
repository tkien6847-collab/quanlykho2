package com.qlykho.controller;

import com.qlykho.dto.DangNhapRequest;
import com.qlykho.dto.DangNhapResponse;
import com.qlykho.dto.TaiKhoanIdentityResponse;
import com.qlykho.service.DangNhapService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class DangNhapController {
    private final DangNhapService dangNhapService;

    public DangNhapController(DangNhapService dangNhapService) {
        this.dangNhapService = dangNhapService;
    }

    @PostMapping("/login")
    public DangNhapResponse dangNhap(@RequestBody DangNhapRequest request) {
        return dangNhapService.dangNhap(request);
    }

    @GetMapping("/me")
    public TaiKhoanIdentityResponse danhTinh(Authentication authentication) {
        return dangNhapService.layDanhTinh(authentication.getName());
    }
}
