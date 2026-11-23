package com.qlykho.service;

import com.qlykho.dto.DangNhapRequest;
import com.qlykho.dto.DangNhapResponse;
import com.qlykho.dto.TaiKhoanIdentityResponse;
import com.qlykho.entity.TaiKhoan;
import com.qlykho.entity.VaiTro;
import com.qlykho.repository.TaiKhoanRepository;
import com.qlykho.repository.VaiTroRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DangNhapService {
    private final TaiKhoanRepository taiKhoanRepository;
    private final VaiTroRepository vaiTroRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public DangNhapService(
            TaiKhoanRepository taiKhoanRepository,
            VaiTroRepository vaiTroRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public DangNhapResponse dangNhap(DangNhapRequest request) {
        if (request == null || request.tenDangNhap() == null || request.matKhau() == null) {
            throw new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không chính xác.");
        }
        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(request.tenDangNhap())
                .orElseThrow(() -> new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không chính xác."));
        if (!passwordEncoder.matches(request.matKhau(), taiKhoan.getMatKhau())) {
            throw new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không chính xác.");
        }
        if (!taiKhoan.dangHoatDong()) {
            throw new BadCredentialsException("Tài khoản hiện không hoạt động.");
        }

        VaiTro vaiTro = vaiTroRepository.findById(taiKhoan.getMaVaiTro())
                .orElseThrow(() -> new BadCredentialsException("Tài khoản chưa được gán vai trò hợp lệ."));
        return new DangNhapResponse(
                jwtService.createToken(taiKhoan.getTenDangNhap()),
                taiKhoan.getTenDangNhap(),
                vaiTro.getMaVaiTro(),
                vaiTro.getTenVaiTro(),
                taiKhoan.getMaNV()
        );
    }

    public TaiKhoanIdentityResponse layDanhTinh(String username) {
        TaiKhoan account = taiKhoanRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new BadCredentialsException("Tài khoản không còn tồn tại."));
        if (!account.dangHoatDong()) {
            throw new BadCredentialsException("Tài khoản hiện không hoạt động.");
        }
        VaiTro role = vaiTroRepository.findById(account.getMaVaiTro())
                .orElseThrow(() -> new BadCredentialsException("Tài khoản chưa được gán vai trò hợp lệ."));
        return new TaiKhoanIdentityResponse(
                account.getTenDangNhap(),
                role.getMaVaiTro(),
                role.getTenVaiTro(),
                account.getMaNV()
        );
    }

}
