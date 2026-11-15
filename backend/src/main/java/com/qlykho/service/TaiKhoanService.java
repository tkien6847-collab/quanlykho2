package com.qlykho.service;

import com.qlykho.dto.TaiKhoanResponse;
import com.qlykho.dto.TaoTaiKhoanQuanLyRequest;
import com.qlykho.dto.TaoTaiKhoanNhanVienRequest;
import com.qlykho.dto.TaoTaiKhoanRequest;
import com.qlykho.entity.NhanVien;
import com.qlykho.entity.TaiKhoan;
import com.qlykho.entity.VaiTro;
import com.qlykho.repository.NhanVienRepository;
import com.qlykho.repository.TaiKhoanRepository;
import com.qlykho.repository.VaiTroRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TaiKhoanService {
    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final VaiTroRepository vaiTroRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public TaiKhoanService(
            TaiKhoanRepository taiKhoanRepository,
            NhanVienRepository nhanVienRepository,
            VaiTroRepository vaiTroRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<TaiKhoanResponse> layDanhSachTaiKhoan() {
        return taiKhoanRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public TaiKhoanResponse taoTaiKhoanQuanLy(TaoTaiKhoanQuanLyRequest request) {
        validateCredentials(request == null ? null : request.tenDangNhap(), request == null ? null : request.matKhau());
        return createAccount(request.tenDangNhap(), request.matKhau(), "Quản lý", "Quản lý", "manager");
    }

    @Transactional
    public TaiKhoanResponse taoTaiKhoanNhanVien(TaoTaiKhoanNhanVienRequest request) {
        validateCredentials(request == null ? null : request.tenDangNhap(), request == null ? null : request.matKhau());
        return createAccount(request.tenDangNhap(), request.matKhau(), "Nhân viên", "Nhân viên", "employee");
    }

    private TaiKhoanResponse createAccount(
            String username,
            String rawPassword,
            String roleName,
            String jobTitle,
            String profileSlug
    ) {
        if (taiKhoanRepository.existsByTenDangNhap(username)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }

        VaiTro role = vaiTroRepository.findByTenVaiTro(roleName)
                .orElseGet(() -> {
                    VaiTro newRole = new VaiTro();
                    newRole.setTenVaiTro(roleName);
                    newRole.setMoTa(jobTitle + " hệ thống");
                    return vaiTroRepository.save(newRole);
                });

        NhanVien nhanVien = new NhanVien();
        nhanVien.setHoTen((jobTitle + " - " + username)
                .substring(0, Math.min(50, jobTitle.length() + 3 + username.length())));
        nhanVien.setGioiTinh("Khác");
        nhanVien.setNgaySinh(LocalDate.of(2000, 1, 1));
        nhanVien.setChucVu(jobTitle);
        nhanVien.setDiaChi("Chưa cập nhật");
        nhanVien.setSoDienThoai(generatedPhoneNumber());
        nhanVien.setEmail(profileSlug + "-" + UUID.randomUUID() + "@qlykho.local");
        nhanVien = nhanVienRepository.save(nhanVien);

        TaiKhoan account = new TaiKhoan();
        account.setTenDangNhap(username);
        account.setMatKhau(passwordEncoder.encode(rawPassword));
        account.setTrangThai("Hoạt động");
        account.setMaVaiTro(role.getMaVaiTro());
        account.setMaNV(nhanVien.getMaNV());
        return toResponse(taiKhoanRepository.save(account));
    }

    private void validateCredentials(String username, String rawPassword) {
        if (isBlank(username) || isBlank(rawPassword)) {
            throw new IllegalArgumentException("Tên đăng nhập và mật khẩu là bắt buộc.");
        }
        if (username.length() > 50) {
            throw new IllegalArgumentException("Tên đăng nhập không được vượt quá 50 ký tự.");
        }
        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự.");
        }
    }

    @Transactional
    public TaiKhoanResponse taoTaiKhoan(TaoTaiKhoanRequest request) {
        if (request == null || request.nhanVien() == null) {
            throw new IllegalArgumentException("Thông tin nhân viên là bắt buộc.");
        }
        if (isBlank(request.tenDangNhap()) || isBlank(request.matKhau()) || isBlank(request.trangThai())) {
            throw new IllegalArgumentException("Tên đăng nhập, mật khẩu và trạng thái là bắt buộc.");
        }
        NhanVien nhanVien = request.nhanVien();
        if (isBlank(nhanVien.getHoTen())
                || isBlank(nhanVien.getGioiTinh())
                || nhanVien.getNgaySinh() == null
                || isBlank(nhanVien.getSoDienThoai())
                || isBlank(nhanVien.getEmail())
                || isBlank(nhanVien.getDiaChi())
                || isBlank(nhanVien.getChucVu())) {
            throw new IllegalArgumentException("Thông tin nhân viên không hợp lệ.");
        }
        if (request.maVaiTro() == null || !vaiTroRepository.existsById(request.maVaiTro())) {
            throw new IllegalArgumentException("Vai trò không tồn tại.");
        }
        if (taiKhoanRepository.existsByTenDangNhap(request.tenDangNhap())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }
        if (request.tenDangNhap().length() > 50) {
            throw new IllegalArgumentException("Tên đăng nhập không được vượt quá 50 ký tự.");
        }

        nhanVien.setMaNV(null);
        nhanVien = nhanVienRepository.save(nhanVien);
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setTenDangNhap(request.tenDangNhap());
        taiKhoan.setMatKhau(passwordEncoder.encode(request.matKhau()));
        taiKhoan.setTrangThai(request.trangThai());
        taiKhoan.setMaVaiTro(request.maVaiTro());
        taiKhoan.setMaNV(nhanVien.getMaNV());
        return toResponse(taiKhoanRepository.save(taiKhoan));
    }

    public TaiKhoanResponse capNhatVaiTro(Integer maTK, Integer maVaiTro) {
        if (maVaiTro == null || !vaiTroRepository.existsById(maVaiTro)) {
            throw new IllegalArgumentException("Vai trò không tồn tại.");
        }
        TaiKhoan taiKhoan = taiKhoanRepository.findById(maTK)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy tài khoản."));
        taiKhoan.setMaVaiTro(maVaiTro);
        return toResponse(taiKhoanRepository.save(taiKhoan));
    }

    public void xoaTaiKhoan(Integer maTK) {
        if (!taiKhoanRepository.existsById(maTK)) {
            throw new NoSuchElementException("Không tìm thấy tài khoản.");
        }
        taiKhoanRepository.deleteById(maTK);
    }

    private TaiKhoanResponse toResponse(TaiKhoan taiKhoan) {
        return new TaiKhoanResponse(
                taiKhoan.getMaTK(),
                taiKhoan.getTenDangNhap(),
                taiKhoan.getTrangThai(),
                taiKhoan.getMaVaiTro(),
                taiKhoan.getMaNV()
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String generatedPhoneNumber() {
        String phoneNumber;
        do {
            phoneNumber = "0" + ThreadLocalRandom.current().nextInt(100_000_000, 1_000_000_000);
        } while (nhanVienRepository.existsBySoDienThoai(phoneNumber));
        return phoneNumber;
    }
}