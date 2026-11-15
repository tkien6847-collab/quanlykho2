package com.qlykho.service;

import com.qlykho.entity.NhanVien;
import com.qlykho.repository.NhanVienRepository;
import com.qlykho.repository.PhieuNhapRepository;
import com.qlykho.repository.PhieuXuatRepository;
import com.qlykho.repository.TaiKhoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NhanVienService {
    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final PhieuNhapRepository phieuNhapRepository;
    private final PhieuXuatRepository phieuXuatRepository;

    public NhanVienService(
            NhanVienRepository nhanVienRepository,
            TaiKhoanRepository taiKhoanRepository,
            PhieuNhapRepository phieuNhapRepository,
            PhieuXuatRepository phieuXuatRepository
    ) {
        this.nhanVienRepository = nhanVienRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.phieuNhapRepository = phieuNhapRepository;
        this.phieuXuatRepository = phieuXuatRepository;
    }

    public List<NhanVien> layDanhSachNhanVien() {
        return nhanVienRepository.findAll();
    }

    @Transactional
    public NhanVien themNhanVien(NhanVien nhanVien) {
        validate(nhanVien, null);
        nhanVien.setMaNV(null);
        return nhanVienRepository.save(nhanVien);
    }

    @Transactional
    public NhanVien capNhatNhanVien(Integer maNV, NhanVien thongTinMoi) {
        NhanVien nhanVien = nhanVienRepository.findById(maNV)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhân viên."));
        validate(thongTinMoi, maNV);
        thongTinMoi.setMaNV(maNV);
        return nhanVienRepository.save(thongTinMoi);
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

    private void validate(NhanVien nhanVien, Integer currentMaNV) {
        if (nhanVien == null
                || isBlank(nhanVien.getHoTen())
                || isBlank(nhanVien.getGioiTinh())
                || nhanVien.getNgaySinh() == null
                || isBlank(nhanVien.getSoDienThoai())
                || isBlank(nhanVien.getEmail())
                || isBlank(nhanVien.getDiaChi())
                || isBlank(nhanVien.getChucVu())) {
            throw new IllegalArgumentException("Thông tin nhân viên là bắt buộc.");
        }
        if (nhanVien.getHoTen().length() > 50
                || nhanVien.getGioiTinh().length() > 10
                || nhanVien.getSoDienThoai().length() > 20
                || nhanVien.getEmail().length() > 100
                || nhanVien.getDiaChi().length() > 200
                || nhanVien.getChucVu().length() > 50) {
            throw new IllegalArgumentException("Thông tin nhân viên vượt quá độ dài cho phép.");
        }
        boolean duplicatePhone = currentMaNV == null
                ? nhanVienRepository.existsBySoDienThoai(nhanVien.getSoDienThoai())
                : nhanVienRepository.existsBySoDienThoaiAndMaNVNot(nhanVien.getSoDienThoai(), currentMaNV);
        if (duplicatePhone) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng.");
        }
        boolean duplicateEmail = currentMaNV == null
                ? nhanVienRepository.existsByEmail(nhanVien.getEmail())
                : nhanVienRepository.existsByEmailAndMaNVNot(nhanVien.getEmail(), currentMaNV);
        if (duplicateEmail) {
            throw new IllegalArgumentException("Email đã được sử dụng.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}