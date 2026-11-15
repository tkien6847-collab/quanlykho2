package com.qlykho.dto;

import java.time.LocalDate;
import java.util.List;

public record TaoPhieuNhapRequest(
        LocalDate ngayNhap,
        String ghiChu,
        Integer maNV,
        Integer maNCC,
        String trangThai,
        List<ChiTietPhieuNhapRequest> chiTiet
) {
}
