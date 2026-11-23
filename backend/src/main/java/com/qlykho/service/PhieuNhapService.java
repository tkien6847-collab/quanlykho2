package com.qlykho.service;

import com.qlykho.dto.ChiTietPhieuNhapRequest;
import com.qlykho.dto.TaoPhieuNhapRequest;
import com.qlykho.entity.ChiTietPhieuNhap;
import com.qlykho.entity.ChiTietPhieuNhapId;
import com.qlykho.entity.NhaCungCap;
import com.qlykho.entity.PhieuNhap;
import com.qlykho.repository.ChiTietPhieuNhapRepository;
import com.qlykho.repository.HangHoaRepository;
import com.qlykho.repository.NhaCungCapRepository;
import com.qlykho.repository.NhanVienRepository;
import com.qlykho.repository.PhieuNhapRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class PhieuNhapService {
    private static final String STATUS_PENDING = "Chờ duyệt";
    private static final String STATUS_APPROVED = "Đã duyệt";

    private final PhieuNhapRepository phieuNhapRepository;
    private final ChiTietPhieuNhapRepository chiTietRepository;
    private final HangHoaRepository hangHoaRepository;
    private final NhanVienRepository nhanVienRepository;
    private final NhaCungCapRepository nhaCungCapRepository;

    public PhieuNhapService(
            PhieuNhapRepository phieuNhapRepository,
            ChiTietPhieuNhapRepository chiTietRepository,
            HangHoaRepository hangHoaRepository,
            NhanVienRepository nhanVienRepository,
            NhaCungCapRepository nhaCungCapRepository
    ) {
        this.phieuNhapRepository = phieuNhapRepository;
        this.chiTietRepository = chiTietRepository;
        this.hangHoaRepository = hangHoaRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.nhaCungCapRepository = nhaCungCapRepository;
    }

    public List<PhieuNhap> layDanhSachPhieuNhap() {
        return phieuNhapRepository.findAll();
    }

    @Transactional
    public PhieuNhap taoPhieuNhap(TaoPhieuNhapRequest request) {
        validate(request);
        PhieuNhap phieuNhap = new PhieuNhap();
        phieuNhap.setNgayNhap(request.ngayNhap());
        phieuNhap.setGhiChu(request.ghiChu());
        phieuNhap.setMaNV(request.maNV());
        phieuNhap.setMaNCC(request.maNCC());
        phieuNhap.setTrangThai(STATUS_PENDING);
        phieuNhap.setTongTien(request.chiTiet().stream()
                .map(line -> line.donGiaNhap().multiply(BigDecimal.valueOf(line.soLuongNhap())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        PhieuNhap saved = phieuNhapRepository.save(phieuNhap);

        List<ChiTietPhieuNhap> chiTiet = request.chiTiet().stream().map(line -> {
            ChiTietPhieuNhap detail = new ChiTietPhieuNhap();
            detail.setId(new ChiTietPhieuNhapId(saved.getMaPN(), line.maHH()));
            detail.setSoLuongNhap(line.soLuongNhap());
            detail.setDonGiaNhap(line.donGiaNhap());
            detail.setHanSuDung(line.hanSuDung());
            return detail;
        }).toList();
        chiTietRepository.saveAll(chiTiet);
        return saved;
    }

    @Transactional
    public PhieuNhap capNhatTrangThai(Integer maPN, String trangThai) {
        if (!STATUS_APPROVED.equals(trangThai)) {
            throw new IllegalArgumentException("Chỉ có thể chuyển phiếu sang trạng thái đã duyệt.");
        }
        PhieuNhap phieuNhap = phieuNhapRepository.findById(maPN)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy phiếu nhập."));
        if (!STATUS_PENDING.equals(phieuNhap.getTrangThai())) {
            throw new IllegalArgumentException("Chỉ phiếu đang chờ duyệt mới có thể được duyệt.");
        }
        for (ChiTietPhieuNhap detail : chiTietRepository.findById_MaPN(maPN)) {
            var product = hangHoaRepository.findById(detail.getId().getMaHH())
                    .orElseThrow(() -> new NoSuchElementException("Không tìm thấy hàng hóa trong phiếu nhập."));
            int currentStock = product.getSoLuongTon() == null ? 0 : product.getSoLuongTon();
            product.setSoLuongTon(currentStock + detail.getSoLuongNhap());
            hangHoaRepository.save(product);
        }
        phieuNhap.setTrangThai(STATUS_APPROVED);
        return phieuNhapRepository.save(phieuNhap);
    }

    private void validate(TaoPhieuNhapRequest request) {
        if (request == null
                || request.ngayNhap() == null
                || request.maNV() == null
                || request.maNCC() == null
                || request.chiTiet() == null
                || request.chiTiet().isEmpty()) {
            throw new IllegalArgumentException("Thông tin phiếu nhập không hợp lệ.");
        }
        if (!nhanVienRepository.existsById(request.maNV())) {
            throw new IllegalArgumentException("Nhân viên không tồn tại.");
        }
        NhaCungCap supplier = nhaCungCapRepository.findById(request.maNCC())
                .orElseThrow(() -> new IllegalArgumentException("Nhà cung cấp không tồn tại."));
        if (!NhaCungCap.HOAT_DONG.equals(supplier.getTrangThai())) {
            throw new IllegalArgumentException("Nhà cung cấp đã ngừng hoạt động.");
        }

        Set<Integer> productIds = new HashSet<>();
        for (ChiTietPhieuNhapRequest line : request.chiTiet()) {
            if (line == null
                    || line.maHH() == null
                    || line.soLuongNhap() == null
                    || line.soLuongNhap() <= 0
                    || line.donGiaNhap() == null
                    || line.donGiaNhap().signum() < 0
                    || line.hanSuDung() == null
                    || !productIds.add(line.maHH())) {
                throw new IllegalArgumentException("Chi tiết phiếu nhập không hợp lệ hoặc bị trùng hàng hóa.");
            }
            if (!hangHoaRepository.existsById(line.maHH())) {
                throw new IllegalArgumentException("Hàng hóa không tồn tại: " + line.maHH());
            }
        }
    }
}
