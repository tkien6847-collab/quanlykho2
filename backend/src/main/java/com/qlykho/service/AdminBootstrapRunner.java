package com.qlykho.service;

import com.qlykho.entity.NhanVien;
import com.qlykho.entity.TaiKhoan;
import com.qlykho.entity.VaiTro;
import com.qlykho.repository.NhanVienRepository;
import com.qlykho.repository.TaiKhoanRepository;
import com.qlykho.repository.VaiTroRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {
    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final VaiTroRepository vaiTroRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final String username;
    private final String password;
    private final String hoTen;
    private final String gioiTinh;
    private final String ngaySinh;
    private final String soDienThoai;
    private final String email;
    private final String diaChi;

    public AdminBootstrapRunner(
            TaiKhoanRepository taiKhoanRepository,
            NhanVienRepository nhanVienRepository,
            VaiTroRepository vaiTroRepository,
            BCryptPasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.username:}") String username,
            @Value("${app.bootstrap-admin.password:}") String password,
            @Value("${app.bootstrap-admin.ho-ten:}") String hoTen,
            @Value("${app.bootstrap-admin.gioi-tinh:}") String gioiTinh,
            @Value("${app.bootstrap-admin.ngay-sinh:}") String ngaySinh,
            @Value("${app.bootstrap-admin.so-dien-thoai:}") String soDienThoai,
            @Value("${app.bootstrap-admin.email:}") String email,
            @Value("${app.bootstrap-admin.dia-chi:}") String diaChi
    ) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        boolean configured = !username.isBlank() || !password.isBlank() || !hoTen.isBlank()
                || !gioiTinh.isBlank() || !ngaySinh.isBlank() || !soDienThoai.isBlank()
                || !email.isBlank() || !diaChi.isBlank();
        if (!configured) return;

        if (username.isBlank() || password.isBlank()) {
            throw new IllegalStateException("Cần cấu hình tên đăng nhập và mật khẩu để tạo tài khoản quản trị.");
        }
        if (password.length() < 8) {
            throw new IllegalStateException("APP_BOOTSTRAP_ADMIN_PASSWORD phải có ít nhất 8 ký tự.");
        }
        if (taiKhoanRepository.existsByTenDangNhap(username)) return;

        VaiTro role = vaiTroRepository.findByTenVaiTro("Quản trị viên")
                .orElseGet(() -> {
                    VaiTro newRole = new VaiTro();
                    newRole.setTenVaiTro("Quản trị viên");
                    newRole.setMoTa("Quản trị hệ thống");
                    return vaiTroRepository.save(newRole);
                });

        NhanVien employee = new NhanVien();
        employee.setHoTen(hoTen.strip());
        employee.setGioiTinh(ThongTinNhanVienText.giaTriTuyChon(gioiTinh));
        employee.setNgaySinh(ngaySinh.isBlank() ? null : LocalDate.parse(ngaySinh.strip()));
        employee.setSoDienThoai(ThongTinNhanVienText.giaTriTuyChon(soDienThoai));
        employee.setEmail(ThongTinNhanVienText.giaTriTuyChon(email));
        employee.setDiaChi(ThongTinNhanVienText.giaTriTuyChon(diaChi));
        employee.setChucVu("Quản trị viên");
        employee = nhanVienRepository.save(employee);

        TaiKhoan account = new TaiKhoan();
        account.setTenDangNhap(username);
        account.setMatKhau(passwordEncoder.encode(password));
        account.setTrangThai("Hoạt động");
        account.setMaVaiTro(role.getMaVaiTro());
        account.setMaNV(employee.getMaNV());
        taiKhoanRepository.save(account);
    }
}
