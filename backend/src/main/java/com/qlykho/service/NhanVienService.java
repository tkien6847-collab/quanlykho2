package com.qlykho.service;

import com.qlykho.dto.NhanVienDanhSachResponse;
import com.qlykho.dto.TaiKhoanResponse;
import com.qlykho.dto.TaoTaiKhoanNhanVienRequest;
import com.qlykho.dto.ThongTinCaNhan;
import com.qlykho.entity.NhanVien;
import com.qlykho.entity.TaiKhoan;
import com.qlykho.entity.VaiTro;
import com.qlykho.repository.NhanVienRepository;
import com.qlykho.repository.PhieuNhapRepository;
import com.qlykho.repository.PhieuXuatRepository;
import com.qlykho.repository.TaiKhoanRepository;
import com.qlykho.repository.VaiTroRepository;
import com.qlykho.security.AccountRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.qlykho.service.ThongTinNhanVienText.giaTriTuyChon;
import static com.qlykho.service.ThongTinNhanVienText.laChucVuQuanLy;

@Service
public class NhanVienService {
    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final PhieuNhapRepository phieuNhapRepository;
    private final PhieuXuatRepository phieuXuatRepository;
    private final VaiTroRepository vaiTroRepository;
    private final TaiKhoanService taiKhoanService;

    public NhanVienService(
            NhanVienRepository nhanVienRepository,
            TaiKhoanRepository taiKhoanRepository,
            PhieuNhapRepository phieuNhapRepository,
            PhieuXuatRepository phieuXuatRepository,
            VaiTroRepository vaiTroRepository,
            TaiKhoanService taiKhoanService
    ) {
        this.nhanVienRepository = nhanVienRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.phieuNhapRepository = phieuNhapRepository;
        this.phieuXuatRepository = phieuXuatRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.taiKhoanService = taiKhoanService;
    }

    @Transactional(readOnly = true)
    public List<NhanVienDanhSachResponse> layDanhSachNhanVien() {
        Map<Integer, TaiKhoan> accounts = taiKhoanRepository.findAll().stream()
                .collect(Collectors.toMap(TaiKhoan::getMaNV, Function.identity()));
        Map<Integer, AccountRole> roles = vaiTroRepository.findAll().stream()
                .collect(Collectors.toMap(VaiTro::getMaVaiTro, role -> AccountRole.fromName(role.getTenVaiTro())));
        return nhanVienRepository.findAll().stream()
                .filter(employee -> {
                    TaiKhoan account = accounts.get(employee.getMaNV());
                    return !laChucVuQuanLy(employee.getChucVu())
                            && (account == null || roles.get(account.getMaVaiTro()) == AccountRole.EMPLOYEE);
                })
                .map(employee -> directoryEmployee(employee, accounts.get(employee.getMaNV())))
                .toList();
    }

    @Transactional
    public NhanVienDanhSachResponse taoTaiKhoanNhanVien(TaoTaiKhoanNhanVienRequest request) {
        TaiKhoanResponse created = taiKhoanService.taoTaiKhoanNhanVien(request);
        NhanVien employee = nhanVienRepository.findById(created.maNV()).orElseThrow();
        TaiKhoan account = taiKhoanRepository.findById(created.maTK()).orElseThrow();
        return directoryEmployee(employee, account);
    }

    @Transactional
    public NhanVienDanhSachResponse capNhatTrangThaiTaiKhoan(Integer maNV, String trangThai) {
        if (!TaiKhoan.trangThaiHopLe(trangThai)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        }
        NhanVien employee = nhanVienRepository.findById(maNV)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhân viên."));
        if (laChucVuQuanLy(employee.getChucVu())) {
            throw new NoSuchElementException("Không tìm thấy hồ sơ nhân viên.");
        }
        TaiKhoan account = taiKhoanRepository.findByMaNVForUpdate(maNV)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên này chưa có tài khoản."));
        if (!isEmployeeAccount(account)) {
            throw new NoSuchElementException("Không tìm thấy tài khoản nhân viên.");
        }
        account.setTrangThai(trangThai);
        return directoryEmployee(employee, account);
    }

    public ThongTinCaNhan layThongTinCaNhan(String username) {
        return ThongTinNhanVienText.hoSoHienThi(findEmployeeForAccount(username), username);
    }

    @Transactional
    public ThongTinCaNhan capNhatThongTinCaNhan(String username, ThongTinCaNhan thongTin) {
        NhanVien employee = findEmployeeForAccount(username);
        String name = thongTin.hoTen() == null ? "" : thongTin.hoTen().strip();
        if (!name.isEmpty() && ThongTinNhanVienText.hoTenHienThi(name, username).isEmpty()) {
            throw new IllegalArgumentException("Hãy nhập họ tên hợp lệ hoặc để trống.");
        }
        ThongTinCaNhan updated = new ThongTinCaNhan(
                name, giaTriTuyChon(thongTin.gioiTinh()), thongTin.ngaySinh(), giaTriTuyChon(thongTin.soDienThoai()),
                giaTriTuyChon(thongTin.email()), giaTriTuyChon(thongTin.diaChi())
        );
        validateProfile(updated, employee.getMaNV());
        applyProfile(employee, updated);
        return ThongTinNhanVienText.hoSoHienThi(employee, username);
    }

    private NhanVien findEmployeeForAccount(String username) {
        TaiKhoan account = taiKhoanRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy tài khoản."));
        return nhanVienRepository.findById(account.getMaNV())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy hồ sơ cá nhân."));
    }

    @Transactional
    public NhanVien themNhanVien(NhanVien nhanVien) {
        ThongTinCaNhan profile = validateDirectoryEntry(nhanVien, null);
        nhanVien.setMaNV(null);
        nhanVien.setChucVu(nhanVien.getChucVu().strip());
        applyProfile(nhanVien, profile);
        return displayEmployee(nhanVienRepository.save(nhanVien), null);
    }

    @Transactional
    public NhanVien capNhatNhanVien(Integer maNV, NhanVien thongTinMoi) {
        NhanVien nhanVien = nhanVienRepository.findById(maNV)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhân viên."));
        TaiKhoan account = taiKhoanRepository.findByMaNV(maNV).orElse(null);
        if (laChucVuQuanLy(nhanVien.getChucVu()) || (account != null && !isEmployeeAccount(account))) {
            throw new NoSuchElementException("Không tìm thấy hồ sơ nhân viên.");
        }
        ThongTinCaNhan profile = validateDirectoryEntry(thongTinMoi, maNV);
        nhanVien.setChucVu(thongTinMoi.getChucVu().strip());
        applyProfile(nhanVien, profile);
        return displayEmployee(nhanVien, account);
    }

    @Transactional
    public void xoaNhanVien(Integer maNV) {
        if (!nhanVienRepository.existsById(maNV)) {
            throw new NoSuchElementException("Không tìm thấy nhân viên.");
        }
        if (taiKhoanRepository.existsByMaNV(maNV)) {
            throw new IllegalArgumentException("Không thể xóa nhân viên đang liên kết với tài khoản.");
        }
        if (phieuNhapRepository.existsByMaNV(maNV) || phieuXuatRepository.existsByMaNV(maNV)) {
            throw new IllegalArgumentException("Không thể xóa nhân viên đã lập phiếu nhập hoặc phiếu xuất.");
        }
        nhanVienRepository.deleteById(maNV);
    }

    private boolean isEmployeeAccount(TaiKhoan account) {
        return vaiTroRepository.findById(account.getMaVaiTro())
                .map(role -> AccountRole.fromName(role.getTenVaiTro()) == AccountRole.EMPLOYEE)
                .orElse(false);
    }

    private NhanVienDanhSachResponse directoryEmployee(NhanVien employee, TaiKhoan account) {
        NhanVien display = displayEmployee(employee, account);
        String status = account == null ? null : account.dangHoatDong() ? TaiKhoan.HOAT_DONG : TaiKhoan.NGUNG_HOAT_DONG;
        return new NhanVienDanhSachResponse(
                display.getMaNV(), display.getHoTen(), display.getGioiTinh(), display.getNgaySinh(),
                display.getSoDienThoai(), display.getEmail(), display.getDiaChi(), display.getChucVu(),
                account == null ? null : account.getTenDangNhap(), status
        );
    }

    private NhanVien displayEmployee(NhanVien employee, TaiKhoan account) {
        String username = account == null ? "" : account.getTenDangNhap();
        NhanVien display = new NhanVien();
        display.setMaNV(employee.getMaNV());
        display.setChucVu(ThongTinNhanVienText.suaMaHoaCu(employee.getChucVu()));
        applyProfile(display, ThongTinNhanVienText.hoSoHienThi(employee, username));
        return display;
    }

    private void applyProfile(NhanVien employee, ThongTinCaNhan profile) {
        employee.setHoTen(profile.hoTen());
        employee.setGioiTinh(profile.gioiTinh());
        employee.setNgaySinh(profile.ngaySinh());
        employee.setSoDienThoai(profile.soDienThoai());
        employee.setEmail(profile.email());
        employee.setDiaChi(profile.diaChi());
    }

    private ThongTinCaNhan validateDirectoryEntry(NhanVien nhanVien, Integer currentMaNV) {
        if (nhanVien == null || isBlank(nhanVien.getChucVu())) {
            throw new IllegalArgumentException("Vui lòng nhập chức vụ của nhân viên.");
        }
        if (laChucVuQuanLy(nhanVien.getChucVu())) {
            throw new IllegalArgumentException("Danh sách này chỉ dành cho nhân viên.");
        }
        if (nhanVien.getChucVu().strip().length() > 50) {
            throw new IllegalArgumentException("Thông tin nhân viên vượt quá độ dài cho phép.");
        }
        ThongTinCaNhan profile = new ThongTinCaNhan(
                nhanVien.getHoTen() == null ? "" : nhanVien.getHoTen().strip(),
                giaTriTuyChon(nhanVien.getGioiTinh()), nhanVien.getNgaySinh(), giaTriTuyChon(nhanVien.getSoDienThoai()),
                giaTriTuyChon(nhanVien.getEmail()), giaTriTuyChon(nhanVien.getDiaChi())
        );
        validateProfile(profile, currentMaNV);
        return profile;
    }

    private void validateProfile(ThongTinCaNhan profile, Integer currentMaNV) {
        if (tooLong(profile.hoTen(), 50) || tooLong(profile.gioiTinh(), 10)
                || tooLong(profile.soDienThoai(), 20) || tooLong(profile.email(), 100) || tooLong(profile.diaChi(), 200)) {
            throw new IllegalArgumentException("Thông tin nhân viên vượt quá độ dài cho phép.");
        }
        boolean duplicatePhone = profile.soDienThoai() != null && (currentMaNV == null
                ? nhanVienRepository.existsBySoDienThoai(profile.soDienThoai())
                : nhanVienRepository.existsBySoDienThoaiAndMaNVNot(profile.soDienThoai(), currentMaNV));
        if (duplicatePhone) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng.");
        }
        boolean duplicateEmail = profile.email() != null && (currentMaNV == null
                ? nhanVienRepository.existsByEmail(profile.email())
                : nhanVienRepository.existsByEmailAndMaNVNot(profile.email(), currentMaNV));
        if (duplicateEmail) {
            throw new IllegalArgumentException("Email đã được sử dụng.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean tooLong(String value, int limit) {
        return value != null && value.length() > limit;
    }
}
