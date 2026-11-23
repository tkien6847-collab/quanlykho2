package com.qlykho.service;

import com.qlykho.dto.ChiTietPhieuXuatRequest;
import com.qlykho.dto.TaoPhieuXuatRequest;
import com.qlykho.entity.ChiTietPhieuXuat;
import com.qlykho.entity.ChiTietPhieuXuatId;
import com.qlykho.entity.KhachHang;
import com.qlykho.entity.PhieuXuat;
import com.qlykho.repository.ChiTietPhieuXuatRepository;
import com.qlykho.repository.HangHoaRepository;
import com.qlykho.repository.KhachHangRepository;
import com.qlykho.repository.NhanVienRepository;
import com.qlykho.repository.PhieuXuatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class PhieuXuatService {
    private static final String STATUS_PENDING = "Chờ duyệt";
    private static final String STATUS_APPROVED = "Đã duyệt";

    private final PhieuXuatRepository phieuXuatRepository;
    private final ChiTietPhieuXuatRepository chiTietRepository;
    private final HangHoaRepository hangHoaRepository;
    private final NhanVienRepository nhanVienRepository;
    private final KhachHangRepository khachHangRepository;

    public PhieuXuatService(
            PhieuXuatRepository phieuXuatRepository,
            ChiTietPhieuXuatRepository chiTietRepository,
            HangHoaRepository hangHoaRepository,
            NhanVienRepository nhanVienRepository,
            KhachHangRepository khachHangRepository
    ) {
        this.phieuXuatRepository = phieuXuatRepository;
        this.chiTietRepository = chiTietRepository;
        this.hangHoaRepository = hangHoaRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.khachHangRepository = khachHangRepository;
    }

    public List<PhieuXuat> layDanhSachPhieuXuat() {
        return phieuXuatRepository.findAll();
    }

    @Transactional
    public PhieuXuat taoPhieuXuat(TaoPhieuXuatRequest request) {
        validate(request);
        PhieuXuat phieuXuat = new PhieuXuat();
        phieuXuat.setNgayXuat(request.ngayXuat());
        phieuXuat.setGhiChu(request.ghiChu());
        phieuXuat.setMaNV(request.maNV());
        phieuXuat.setMaKH(request.maKH());
        phieuXuat.setTrangThai(STATUS_PENDING);
        phieuXuat.setTongTien(request.chiTiet().stream()
                .map(line -> line.donGiaXuat().multiply(BigDecimal.valueOf(line.soLuongXuat())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        PhieuXuat saved = phieuXuatRepository.save(phieuXuat);

        List<ChiTietPhieuXuat> chiTiet = request.chiTiet().stream().map(line -> {
            ChiTietPhieuXuat detail = new ChiTietPhieuXuat();
            detail.setId(new ChiTietPhieuXuatId(saved.getMaPX(), line.maHH()));
            detail.setSoLuongXuat(line.soLuongXuat());
            detail.setDonGiaXuat(line.donGiaXuat());
            return detail;
        }).toList();
        chiTietRepository.saveAll(chiTiet);
        return saved;
    }

    public PhieuXuat capNhatTrangThai(Integer maPX, String trangThai) {
        if (!STATUS_APPROVED.equals(trangThai)) {
            throw new IllegalArgumentException("Chỉ hỗ trợ chuyển phiếu xuất sang trạng thái Đã duyệt.");
        }
        PhieuXuat phieuXuat = phieuXuatRepository.findById(maPX)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy phiếu xuất."));
        if (!STATUS_PENDING.equals(phieuXuat.getTrangThai())) {
            throw new IllegalArgumentException("Chỉ phiếu đang chờ duyệt mới có thể được duyệt.");
        }
        phieuXuat.setTrangThai(STATUS_APPROVED);
        return phieuXuatRepository.save(phieuXuat);
    }

    private void validate(TaoPhieuXuatRequest request) {
        if (request == null
                || request.ngayXuat() == null
                || request.maNV() == null
                || request.maKH() == null
                || request.chiTiet() == null
                || request.chiTiet().isEmpty()) {
            throw new IllegalArgumentException("Thông tin phiếu xuất không hợp lệ.");
        }
        if (!nhanVienRepository.existsById(request.maNV())) {
            throw new IllegalArgumentException("Nhân viên không tồn tại.");
        }
        KhachHang customer = khachHangRepository.findById(request.maKH())
                .orElseThrow(() -> new IllegalArgumentException("Khách hàng không tồn tại."));
        if (!KhachHang.HOAT_DONG.equals(customer.getTrangThai())) {
            throw new IllegalArgumentException("Khách hàng đã ngừng hoạt động.");
        }

        Set<Integer> productIds = new HashSet<>();
        for (ChiTietPhieuXuatRequest line : request.chiTiet()) {
            if (line == null
                    || line.maHH() == null
                    || line.soLuongXuat() == null
                    || line.soLuongXuat() <= 0
                    || line.donGiaXuat() == null
                    || line.donGiaXuat().signum() < 0
                    || !productIds.add(line.maHH())) {
                throw new IllegalArgumentException("Chi tiết phiếu xuất không hợp lệ hoặc bị trùng hàng hóa.");
            }
            if (!hangHoaRepository.existsById(line.maHH())) {
                throw new IllegalArgumentException("Hàng hóa không tồn tại: " + line.maHH());
            }
        }
    }
}
